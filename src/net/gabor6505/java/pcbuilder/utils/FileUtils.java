package net.gabor6505.java.pcbuilder.utils;

import com.sun.istack.internal.NotNull;
import net.gabor6505.java.pcbuilder.gui.ProfileManager;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static net.gabor6505.java.pcbuilder.utils.FileUtils.FileOperationResult.*;

public class FileUtils {

    /**
     * Enum containing the different results or "outcomes" a FileOperation in {@link FileUtils} can return
     */
    public enum FileOperationResult {
        SUCCESS("Operation completed successfully!"),
        SOURCE_SAME_AS_DESTINATION("Source path is the same as destination path!"),
        DIRECTORY_DOESNT_EXIST("The specified directory doesn't exist!"),
        FILE_DOESNT_EXIST("The specified file doesn't exist!"),
        FILE_IN_JAR_DOESNT_EXIST("The specified file doesn't exist in the JAR file!"),
        DIRECTORY_ALREADY_EXISTS("A directory with the same name already exists!"),
        FILE_ALREADY_EXISTS("A file with the same name already exists!"),
        DESTINATION_DIRECTORY_ALREADY_EXISTS("The destination directory already exists!"),
        COULD_NOT_COPY_TO_TARGET("Could not copy items at source path to destination path!"),
        COULD_NOT_DELETE_DIRECTORY("Could not delete directory!"),
        COULD_NOT_DELETE_FILE("Could not delete file!"),
        COULD_NOT_EXTRACT_ZIP("Could not extract zip file!"),
        MALFORMED_URL("The specified URL is malformed and cannot be processed!"),
        COULD_NOT_DOWNLOAD_FILE("Could not download file from the specified url!"),
        NOT_A_DIRECTORY("The specified path doesn't point to a directory!"),
        NOT_A_ZIP_ARCHIVE("The specified item is not a zip archive!"),
        UNKNOWN("An unknown error happened!");

        private final String localizedMessage;

        FileOperationResult(String locMsg) {
            localizedMessage = locMsg;
        }

        public String getMessage() {
            return localizedMessage;
        }
    }

    /**
     * Class for holding the result of a file operation and also it's data.
     * The data is stored in an ArrayList of any type and it is up to the
     * certain file operation methods to implement what the data might be.
     * <br><br>
     * For example, when the result is DIRECTORY_ALREADY_EXISTS, then it is
     * useful to include the path of the directory that threw the error
     *
     * @param <E> The type of the data that's stored
     */
    public static class FileOperationData<E> extends ArrayList<E> {

        /**
         * Holds the result of this operation
         */
        private final FileOperationResult result;

        public FileOperationData(@NotNull FileOperationResult result) {
            this.result = result;
        }

        public FileOperationData(@NotNull FileOperationResult result, List<E> data) {
            this.result = result;
            addAll(data);
        }

        public FileOperationData(@NotNull FileOperationResult result, E... data) {
            this.result = result;
            addAll(Arrays.asList(data));
        }

        public FileOperationResult getResult() {
            return result;
        }

        public String getMessage() {
            return result == null ? "" : result.getMessage();
        }

        /**
         * Prints out all the information about this file operation
         */
        public void printResult() {
            System.out.println();
            System.out.println("File operation result: " + getResult().toString() + " - " + getMessage());
            System.out.println("Data of file operation: " + (size() == 0 ? "No data is available" : ""));
            for (Object obj : this) {
                System.out.println("    " + obj.toString());
            }
        }

        /**
         * Prints out all the information about this file operation if it was not successful
         */
        public void printResultIfError() {
            if (result != SUCCESS) printResult();
        }
    }

    private FileUtils() {

    }

    /**
     * Extracts a zip directly from an url, and does not download a temporary zip file,
     * but instead uses a {@link BufferedInputStream} to write the website's zip's contents directly to disk
     * Extracts the zip's contents to inside the specified destinationDirPath
     *
     * @param destinationDirPath       The path to extract the zip's contents into
     * @param url                      The url that is directly pointing to a zip file
     * @param overwrite                Specifies whether the program should overwrite the specified extraction directory if the directory already exists
     * @param overwriteWithZipContents Specifies if the program should overwrite all files and folders on disk that are extracted from the zip file
     * @return The result of the operation
     */
    public static FileOperationData<String> extractZipFromUrl(String destinationDirPath, String url, boolean overwrite, boolean overwriteWithZipContents) {
        try (BufferedInputStream bis = new BufferedInputStream(new URL(url).openStream())) {
            File destDir = new File(destinationDirPath);
            if (!destDir.exists()) destDir.mkdirs();
            //else return DIRECTORY_ALREADY_EXISTS;

            return extractZip(destinationDirPath, bis, overwrite, overwriteWithZipContents);
        } catch (IOException e) {
            if (e instanceof MalformedURLException) {
                return new FileOperationData<>(MALFORMED_URL, url);
            } else e.printStackTrace();
        }
        return new FileOperationData<>(UNKNOWN);
    }

    /**
     * Downloads a file with the specified file name from the specified url to the specified folder
     *
     * @param destinationDirPath The path of the directory where the file should be downloaded
     * @param url                The url to download the file from
     * @param fileName           The name of the file that gets downloaded
     * @return The result of the operation
     */
    public static FileOperationData<String> downloadFileFromUrl(String destinationDirPath, String url, String fileName) {
        try (BufferedInputStream bis = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fos = new FileOutputStream(destinationDirPath + File.separator + fileName)) {

            copyStream(bis, fos);
            return new FileOperationData<>(SUCCESS);
        } catch (Exception e) {
            if (e instanceof MalformedURLException) {
                return new FileOperationData<>(MALFORMED_URL, url);
            } else e.printStackTrace();
        }
        return new FileOperationData<>(COULD_NOT_DOWNLOAD_FILE);
    }

    /**
     * Extracts a zip file from the specified input stream to inside the specified directory path
     *
     * @param destinationDirPath       The directory path to extract the zip's contents into
     * @param zipFileInputStream       The input stream of the zip file
     * @param overwrite                Specifies whether the program should overwrite the specified extraction directory if the directory already exists
     * @param overwriteWithZipContents Specifies if the program should overwrite all files and folders on disk that are extracted from the zip file
     * @return The result of the operation
     */
    public static FileOperationData<String> extractZip(String destinationDirPath, InputStream zipFileInputStream, boolean overwrite, boolean overwriteWithZipContents) {
        // Get the destination directory
        File destinationDir = new File(destinationDirPath);

        // Delete the dest dir if overwrite is enabled
        if (overwrite) deleteDirectory(destinationDir);
        // Create dest dir if it doesn't exist
        if (!destinationDir.exists()) destinationDir.mkdirs();

        if (!destinationDir.isDirectory()) return new FileOperationData<>(NOT_A_DIRECTORY, destinationDirPath);

        try (BufferedInputStream bis = new BufferedInputStream(zipFileInputStream);
             ZipInputStream zis = new ZipInputStream(bis)) {

            if (!isZipArchive(bis)) return new FileOperationData<>(NOT_A_ZIP_ARCHIVE);

            List<String> paths = new ArrayList<>();
            ZipEntry ze;

            while ((ze = zis.getNextEntry()) != null) {
                String zipEntryName = ze.getName();
                File currentFile = new File(destinationDirPath + File.separator + zipEntryName);

                // Skip file if it is a __MACOSX folder or a file in a __MACOSX folder
                if (currentFile.getPath().contains("__MACOSX")) {
                    zis.closeEntry();
                    continue;
                }

                // Add the current file path to the list that will be returned
                paths.add(currentFile.getAbsolutePath());

                // Delete the current file first if overwriteWithZipContents is enabled
                if (overwriteWithZipContents) deleteFile(currentFile);

                // Create a directory if this zip entry is a directory or write the file to disk if it is not
                if (ze.isDirectory()) {
                    if (currentFile.exists()) {
                        zis.closeEntry();
                        zis.close();
                        return new FileOperationData<>(DIRECTORY_ALREADY_EXISTS, currentFile.getPath());
                    } else currentFile.mkdirs();
                } else {
                    if (currentFile.exists()) {
                        zis.closeEntry();
                        zis.close();
                        return new FileOperationData<>(FILE_ALREADY_EXISTS, currentFile.getPath());
                    } else {
                        FileOutputStream fos = new FileOutputStream(currentFile);
                        copyStream(zis, fos);
                        fos.close();
                    }
                }

                zis.closeEntry();
            }

            zis.closeEntry();
            return new FileOperationData<>(SUCCESS, paths);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FileOperationData<>(COULD_NOT_EXTRACT_ZIP);
    }

    /**
     * Extracts a zip file from the specified path to inside the specified directory path
     *
     * @param destinationDirPath       The path to extract the zip's contents to
     * @param zipFilePath              The path of the zip file
     * @param overwrite                Specifies whether the program should overwrite the specified extraction directory if the directory already exists
     * @param overwriteWithZipContents Specifies if the program should overwrite all files and folders on disk that are extracted from the zip file
     * @return The result of the operation
     */
    public static FileOperationData<String> extractZip(String destinationDirPath, String zipFilePath, boolean overwrite, boolean overwriteWithZipContents) {
        try (FileInputStream in = new FileInputStream(zipFilePath)) {
            return extractZip(destinationDirPath, in, overwrite, overwriteWithZipContents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new FileOperationData<>(COULD_NOT_EXTRACT_ZIP);
    }

    /**
     * Deletes a directory and all of it's subdirectories, including all files contained in them
     *
     * @param directory The directory to delete
     * @return The result of the operation
     */
    public static FileOperationResult deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) deleteDirectory(file);
                    else file.delete();
                }
            } else return NOT_A_DIRECTORY;
            return directory.delete() ? SUCCESS : COULD_NOT_DELETE_DIRECTORY;
        }
        return DIRECTORY_DOESNT_EXIST;
    }

    /**
     * Deletes the specified file, if the file is a normal file or if the file is a directory
     *
     * @param file The normal file or directory to delete
     * @return The result of the operation
     */
    public static FileOperationResult deleteFile(File file) {
        if (file.isFile()) {
            boolean delete = file.delete();
            return delete ? SUCCESS : COULD_NOT_DELETE_FILE;
        } else if (file.isDirectory()) return deleteDirectory(file);
        return UNKNOWN;
    }

    /**
     * Returns all directory names contained directly in the specified parent directory in an ArrayList
     *
     * @param directory The parent directory from which to get the list of subdirectories
     * @return The list of directories the specified parent directory has
     */
    public static List<String> listDirectoryNames(File directory) {
        List<String> list = new ArrayList<>();
        if (!directory.exists()) return list;
        if (!directory.isDirectory()) return list;

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) list.add(file.getName());
            }
        }
        return list;
    }

    /**
     * Returns all the file names of the specified directory, or only the normal files (that are not directories) if onlyNormalFiles is true
     *
     * @param directory       The directory from which to collect file names
     * @param onlyNormalFiles Specifies whether this method should only return the normal files, that are not directories
     * @return The list of all file names or the list of normal file names in the specified directory
     */
    public static List<String> listFileNames(File directory, boolean onlyNormalFiles) {
        List<String> list = new ArrayList<>();
        if (!directory.exists()) return list;
        if (!directory.isDirectory()) return list;

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (onlyNormalFiles) {
                    if (file.isFile()) list.add(file.getName());
                } else list.add(file.getName());
            }
        }
        return list;
    }

    /**
     * Copies either a file or a folder with all of it's sub folders and sub files
     * Overwrites any existing files if overwrite is enabled
     *
     * @param sourceLocation The file representing the source location
     * @param targetLocation The file representing the target location
     * @param overwrite If set to true, it will overwrite any existing files and folders
     * @return The result of the operation
     */
    public static FileOperationData<String> copy(File sourceLocation, File targetLocation, boolean overwrite) {
        if (sourceLocation.getAbsolutePath().equals(targetLocation.getAbsolutePath()))
            return new FileOperationData<>(SOURCE_SAME_AS_DESTINATION, targetLocation.getPath(), sourceLocation.getPath());
        try {
            if (sourceLocation.isDirectory()) {
                copyDirectory(sourceLocation, targetLocation, overwrite);
            } else {
                copyFile(sourceLocation, targetLocation, overwrite);
            }
            return new FileOperationData<>(SUCCESS, targetLocation.getPath(), sourceLocation.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FileOperationData<>(COULD_NOT_COPY_TO_TARGET, targetLocation.getPath(), sourceLocation.getPath());
    }

    private static void copyDirectory(File source, File target, boolean overwrite) {
        if (target.exists() && !overwrite) return;
        if (overwrite) deleteFile(target);
        if (!target.exists()) {
            target.mkdir();
        }

        String[] fileNames = source.list();
        if (fileNames == null) return;
        for (String f : fileNames) {
            copy(new File(source, f), new File(target, f), overwrite);
        }
    }

    private static void copyFile(File source, File target, boolean overwrite) throws IOException {
        if (target.exists() && !overwrite) return;
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(target)) {
            copyStream(in, out);
        }
    }

    /**
     * Copies an input stream to an output stream with a buffer size of 4096 bytes
     *
     * @param in  The input stream
     * @param out The output stream
     * @throws IOException If an I/O error occurs
     */
    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
    }

    /**
     * Converts all paths that are in the specified String list and returns a List with
     * the file names in it (a file name is the piece of string after the last slash "/")
     * <br><br>
     * Works with both normal Files and Directories.
     *
     * @param paths The paths that should be converted
     * @return The file names gotten from paths
     */
    public static List<String> getFileNamesFromPaths(List<String> paths) {
        List<String> fileNames = new ArrayList<>();
        for (String path : paths) {
            int lastSlash = path.lastIndexOf("/") + 1;
            if (path.length() > lastSlash) {
                fileNames.add(path.substring(lastSlash));
            } else fileNames.add("");
        }
        return fileNames;
    }

    /**
     * Checks whether an input stream is a zip archive, independent of the file extension used
     * Note: This method does not close the input stream, so it needs to be closed after this method is called
     *
     * @param bis The BufferedInputStream of a zip archive
     * @return True if the specified input stream corresponds to a zip archive, false otherwise or if an error happened
     */
    public static boolean isZipArchive(BufferedInputStream bis) {
        int fileSignature = 0;
        try {
            if (bis.available() < 4) return false;
            DataInputStream din = new DataInputStream(bis);
            bis.mark(4);
            fileSignature = din.readInt();
            bis.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
    }

    /**
     * Checks whether a file is a zip archive, independent of the file extension used
     *
     * @param file The zip file
     * @return True if the specified file corresponds to a zip archive, false otherwise or if an error happened
     */
    public static boolean isZipArchive(File file) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            return isZipArchive(bis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Copies a file from inside this running JAR to the local file system to the specified destination
     *
     * @param pathInJar           The path of the file in the JAR file (e.g. "/cakes.jpg" or "/res/desserts/cakes.jpg")
     * @param destPath            The destination path of the file to be copied.
     *                            This must the exact path of the file, e.g. "./defaults/desserts/cakes.jpg"
     *                            <br>
     *                            if destPathIsDirectory is set to true, this should be the path of a directory
     *                            where the file should be placed with the same name as inside the JAR file
     * @param destPathIsDirectory If set to true, this method will treat the specified
     *                            destPath as a path of a directory to place the file into
     * @return The result of the operation
     */
    public static FileOperationData<String> copyFileFromJar(String pathInJar, String destPath, boolean destPathIsDirectory) {
        if (destPathIsDirectory) {
            new File(destPath).mkdirs();
        } else {
            new File(destPath).getParentFile().mkdirs();
        }

        String destFilePath;
        if (destPathIsDirectory) {
            destFilePath = destPath + "/" + new File(pathInJar).getName();
        } else {
            destFilePath = destPath;
        }

        if (new File(destFilePath).exists()) {
            return new FileOperationData<>(FILE_ALREADY_EXISTS, destFilePath);
        }

        InputStream in = ProfileManager.class.getResourceAsStream(pathInJar);
        if (in == null) return new FileOperationData<>(FILE_IN_JAR_DOESNT_EXIST, pathInJar);

        try (BufferedInputStream bis = new BufferedInputStream(in);
             FileOutputStream fos = new FileOutputStream(destFilePath)) {

            FileUtils.copyStream(bis, fos);
            return new FileOperationData<>(SUCCESS, destFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new FileOperationData<>(UNKNOWN);
    }

    /**
     * Copies a file from inside this running JAR to the local file system to the specified destination
     *
     * @param pathInJar The path of the file in the JAR file (e.g. "/cakes.jpg" or "/res/desserts/cakes.jpg")
     * @param destFile  The destination file that the specified path in jar should be written to.
     *                  This must the exact file that should be written, e.g. new File("./defaults/desserts/cakes.jpg")
     * @return The result of the operation
     */
    public static FileOperationData<String> copyFileFromJar(String pathInJar, File destFile, boolean destFileIsDirectory) {
        return copyFileFromJar(pathInJar, destFile.getPath(), destFileIsDirectory);
    }

    /**
     * Extracts a zip file contained within this running JAR at the specified path, to the specified directory
     *
     * @param zipPathInJar             The path of the zip file inside the JAR file
     * @param destDirPath              The path of the directory where the zip's contents should be extracted
     * @param overwrite                Specifies whether the program should overwrite the specified extraction directory if the directory already exists
     * @param overwriteWithZipContents Specifies if the program should overwrite all files and folders on disk that are extracted from the zip file
     * @return The result of the operation
     */
    public static FileOperationData<String> extractZipFromJar(String zipPathInJar, String destDirPath, boolean overwrite, boolean overwriteWithZipContents) {
        new File(destDirPath).getParentFile().mkdirs();

        InputStream in = ProfileManager.class.getResourceAsStream(zipPathInJar);
        if (in == null) return new FileOperationData<>(FILE_IN_JAR_DOESNT_EXIST, zipPathInJar);

        try (BufferedInputStream bis = new BufferedInputStream(in)) {
            return FileUtils.extractZip(destDirPath, bis, overwrite, overwriteWithZipContents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new FileOperationData<>(UNKNOWN);
    }

    /**
     * Extracts a zip file contained within this running JAR at the specified path, to the specified directory
     *
     * @param zipPathInJar             The path of the zip file inside the JAR file
     * @param destDir                  The directory where the zip's contents should be extracted
     * @param overwrite                Specifies whether the program should overwrite the specified extraction directory if the directory already exists
     * @param overwriteWithZipContents Specifies if the program should overwrite all files and folders on disk that are extracted from the zip file
     * @return The result of the operation
     */
    public static FileOperationData<String> extractZipFromJar(String zipPathInJar, File destDir, boolean overwrite, boolean overwriteWithZipContents) {
        return extractZipFromJar(zipPathInJar, destDir.getPath(), overwrite, overwriteWithZipContents);
    }

    /**
     * Extracts a zip file contained within this running JAR at the specified path, to the specified directory
     * <br>
     * If any folders or files inside the zip file already exists at the specified extract directory, they will be skipped
     *
     * @param zipPathInJar The path of the zip file inside the JAR file
     * @param destDirPath  The path of the directory where the zip's contents should be extracted
     * @return The result of the operation
     */
    public static FileOperationData<String> extractZipFromJar(String zipPathInJar, String destDirPath) {
        return extractZipFromJar(zipPathInJar, destDirPath, false, false);
    }

    /**
     * Extracts a zip file contained within this running JAR at the specified path, to the specified directory
     * <br>
     * If any folders or files inside the zip file already exists at the specified extract directory, they will be skipped
     *
     * @param zipPathInJar The path of the zip file inside the JAR file
     * @param destDir      The directory where the zip's contents should be extracted
     * @return The result of the operation
     */
    public static FileOperationData<String> extractZipFromJar(String zipPathInJar, File destDir) {
        return extractZipFromJar(zipPathInJar, destDir.getPath());
    }
}
