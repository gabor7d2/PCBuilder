package net.gabor6505.java.pcbuilder.utils;

import net.gabor6505.java.pcbuilder.gui.ProfileManager;
import net.gabor6505.java.pcbuilder.gui.dialog.ProgressDialog;
import net.gabor6505.java.pcbuilder.gui.dialog.ProgressDialogType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import static net.gabor6505.java.pcbuilder.utils.FileUtils.FileOperationResult.*;
import static net.gabor6505.java.pcbuilder.utils.FileUtils.*;

/**
 * Hybrid worker class that can do various different file operations once created
 * <br>
 * The worker does all the operations on a background thread, so it doesn't slow down the UI
 * <br><br>
 * Any worker instance should only be executed once, because workers are not reusable
 * <br>
 * If it is executed more than once, nothing will happen.
 */
public class FileWorker extends SwingWorker<FileOperationData<String>, Void> implements ProgressDialog.DialogClosingListener {

    private long maxProgressInBytes;

    private final ProgressDialog dialog;
    private boolean cancelled = false;

    private CompletedListener listener;
    private IOperation<Long> calculationOperation;
    private IOperation<FileOperationData<String>> operation;

    /**
     * Creates a new FileWorker instance and a ProgressDialog if the parentFrame variable is not null
     * <br><br>
     * If a ProgressDialog is not needed, the empty constructor should be used
     *
     * @param parentFrame        The parent frame to attach the progress dialog to or null if a progress dialog
     *                           should not be displayed
     * @param modal              Specifies whether the ProgressDialog should be modal
     *                           (if a dialog is modal, it cannot be closed)
     * @param maxProgressInBytes The maximum progress in bytes for the ProgressDialog
     *                           <br>
     *                           If you want this to be automatically calculated, leave it out
     */
    public FileWorker(JFrame parentFrame, boolean modal, int maxProgressInBytes) {
        setMaxProgress(maxProgressInBytes);

        if (parentFrame != null) {
            dialog = new ProgressDialog(parentFrame, new ProgressDialogType(DO_NOTHING_ON_CLOSE, false, modal, this));
            addPropertyChangeListener(dialog);
        } else dialog = null;
    }

    /**
     * Creates a new FileWorker instance and a ProgressDialog if the parentFrame variable is not null
     * <br><br>
     * If a ProgressDialog is not needed, the empty constructor should be used
     *
     * @param parentFrame The parent frame to attach the progress dialog to
     *                    or null if a progress dialog should not be displayed
     * @param modal       Specifies whether the ProgressDialog should be modal
     *                    (if a dialog is modal, it cannot be closed)
     */
    public FileWorker(JFrame parentFrame, boolean modal) {
        this(parentFrame, modal, -1);
    }

    /**
     * Creates a new FileWorker instance without a ProgressDialog
     */
    public FileWorker() {
        this(null, false);
    }

    /**
     * Sets the completed listener of this FileWorker
     * <br>
     * This listener will get notified right after the
     * execution completes
     * <br>
     * The ProgressDialog will be disposed before the listener
     * gets notified
     *
     * @param completedListener The listener
     */
    public void setCompletedListener(CompletedListener completedListener) {
        listener = completedListener;
    }

    /**
     * Cancels the operation of this file worker in a friendly way
     * <br>
     * If this worker is displaying a ProgressDialog, sets it to indeterminate mode
     * <br>
     * Any files that have been created up to this point
     * by the background task are going to get deleted
     */
    public void setCancelled() {
        this.cancelled = true;
        if (dialog != null) dialog.getProgressBar().setIndeterminate(true);
    }

    public void setOperation(IOperation<FileOperationData<String>> operation) {
        this.operation = operation;
    }

    /**
     * Sets the maximum progress that can be made (in bytes) to the specified value
     * <br>
     * If the specified value is less than 1, the ProgressDialog will become
     * indeterminate, or if it is at least 1, it will become determinate
     * <br>
     * If the specified value is less than 1, the worker will try to calculate
     * the correct value when it starts to do the background operation
     *
     * @param maxProgressInBytes The maximum achievable progress by the operation in bytes
     */
    public void setMaxProgress(long maxProgressInBytes) {
        this.maxProgressInBytes = maxProgressInBytes;
    }

    /**
     * Calling this method calls <code>execute()</code> on this worker and then shows the ProgressDialog
     */
    public void startOperation() {
        this.execute();
        EventQueue.invokeLater(() -> {
            ProfileManager.getInstance().incrementOpenDialogCount();
            if (dialog != null) {
                dialog.getProgressBar().setIndeterminate(true);
                dialog.setVisible(true);
            }
            ProfileManager.getInstance().decrementOpenDialogCount();
        });
    }

    @Override
    protected FileOperationData<String> doInBackground() {
        setMaxProgress(calculationOperation.doInBackground());
        System.out.println("The maximum progress in bytes is: " + maxProgressInBytes);
        System.out.println("Beginning to do the background operation...");
        return operation.doInBackground();
    }

    @Override
    protected void done() {
        if (dialog != null) dialog.dispose();
        try {
            listener.completed(get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean first = true;

    private void postProgress(long progressInBytes) {
        if (progressInBytes < 1 || maxProgressInBytes < 1) return;
        if (progressInBytes > maxProgressInBytes) return;

        double progress = ((double) progressInBytes / maxProgressInBytes) * 100;

        if (first && dialog != null) {
            first = false;
            dialog.getProgressBar().setIndeterminate(false);
            dialog.getProgressBar().setStringPainted(true);
        }
        setProgress((int) progress);
    }

    // What should happen when the progress dialog is closed by the user
    @Override
    public void dialogClosing(WindowEvent e, ProgressDialog dialog) {
        setCancelled();
        dialog.setTitle("Cancelling...");
    }

    /**
     * Starts a zip extraction with the specified parameters
     * <br>
     * Extracts a zip file from the specified input stream to inside the specified directory path
     * <br><br>
     * Note: The maximum progress can't be calculated from input streams because they would need
     * to be reset, so if it hasn't been specified before, the ProgressDialog will be indeterminate
     *
     * @param destDirPath        The path to extract the zip's contents into
     * @param zipFileInputStream The input stream of the zip file
     * @param overwrite          Specifies if the program should overwrite all files and folders on disk that are extracted from the zip file
     */
    public void extractZip(String destDirPath, InputStream zipFileInputStream, boolean overwrite) {
        if (dialog != null) dialog.setTitle("Extracting...");
        calculationOperation = () -> maxProgressInBytes;
        operation = () -> extractZipBackground(destDirPath, zipFileInputStream, overwrite);
        startOperation();
    }

    /**
     * Starts a zip extraction with the specified parameters
     * <br>
     * Extracts a zip file from the specified path to inside the specified directory path
     * <br><br>
     * If the maximum progress hasn't been specified before, it will be calculated before the operation starts
     *
     * @param destDirPath The path to extract the zip's contents into
     * @param zipFilePath The path of the zip file to be extracted
     * @param overwrite   Specifies if the program should overwrite all files and folders on disk that are extracted from the zip file
     */
    public void extractZipFile(String destDirPath, String zipFilePath, boolean overwrite) {
        if (dialog != null) dialog.setTitle("Extracting...");
        calculationOperation = () -> {
            if (maxProgressInBytes < 1) {
                return new File(zipFilePath).length();
            }
            return maxProgressInBytes;
        };
        operation = () -> {
            try {
                return extractZipBackground(destDirPath, new FileInputStream(zipFilePath), overwrite);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return new FileOperationData<>(FILE_DOESNT_EXIST, zipFilePath);
            }
        };
        startOperation();
    }

    /**
     * Starts a zip extraction with the specified parameters
     * <br>
     * Extracts a zip directly from an url, and does not download a temporary zip file,
     * but instead uses a {@link BufferedInputStream} to write the website's zip's contents directly to disk
     * Extracts the zip's contents inside the specified destinationDirPath
     * <br><br>
     * If the maximum progress hasn't been specified before, it will be calculated before the operation starts
     *
     * @param destDirPath The path to extract the zip's contents into
     * @param url         The url that is directly pointing to a zip file
     * @param overwrite   Specifies if the program should overwrite all files and folders on disk that are extracted from the zip file
     */
    public void extractZipFromUrl(String destDirPath, String url, boolean overwrite) {
        calculationOperation = urlCalcOperation(url);
        operation = () -> {
            if (dialog != null) dialog.setTitle("Downloading...");
            try {
                URLConnection connection = new URL(url).openConnection();
                return extractZipBackground(destDirPath, connection.getInputStream(), overwrite);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return new FileOperationData<>(MALFORMED_URL, url);
            } catch (IOException e) {
                e.printStackTrace();
                return new FileOperationData<>(UNKNOWN, url);
            }
        };
        startOperation();
    }

    /**
     * Starts a file download with the specified parameters
     * <br>
     * Downloads a file with the specified file name from the specified url to the specified folder
     * <br><br>
     * If the maximum progress hasn't been specified before, it will be calculated before the operation starts
     *
     * @param destDirPath The path of the directory where the file should be downloaded
     * @param url         The url to download the file from
     * @param fileName    The name of the file that gets downloaded
     * @param overwrite   Specifies whether the file should be overwritten if it already exists
     */
    public void downloadFile(String destDirPath, String url, String fileName, boolean overwrite) {
        calculationOperation = urlCalcOperation(url);
        operation = () -> {
            if (dialog != null) dialog.setTitle("Downloading...");
            try (BufferedInputStream bis = new BufferedInputStream(new URL(url).openStream());
                 FileOutputStream fos = new FileOutputStream(destDirPath + File.separator + fileName)) {

                File file = new File(destDirPath + File.separator + fileName);
                if (file.exists() && !overwrite) return new FileOperationData<>(FILE_ALREADY_EXISTS, file.getPath());

                copyStream(bis, fos, 0);

                if (cancelled) {
                    if (dialog != null) dialog.dispose();
                    FileUtils.delete(new File(destDirPath + File.separator + fileName));
                    return new FileOperationData<>(CANCELLED);
                }
                return new FileUtils.FileOperationData<>(SUCCESS);
            } catch (Exception e) {
                if (e instanceof MalformedURLException) {
                    return new FileUtils.FileOperationData<>(MALFORMED_URL, url);
                } else e.printStackTrace();
            }
            return new FileUtils.FileOperationData<>(COULD_NOT_DOWNLOAD_FILE);
        };
        startOperation();
    }

    /**
     * Starts a file copy with the specified parameters
     * <br>
     * Copies either a file or a folder with all of it's sub folders and sub files
     * Overwrites the targetLocation if overwrite is set to true
     * <br><br>
     * If the maximum progress hasn't been specified before, it will be calculated before the operation starts
     *
     * @param sourceLocation The file representing the source location
     * @param targetLocation The file representing the target location
     * @param overwrite      If set to true, it will overwrite any existing files and folders
     */
    public void copy(File sourceLocation, File targetLocation, boolean overwrite) {
        if (dialog != null) dialog.setTitle("Copying...");
        calculationOperation = () -> {
            if (maxProgressInBytes < 1) {
                return getSize(sourceLocation);
            }
            return maxProgressInBytes;
        };
        operation = () -> {
            if (sourceLocation.getAbsolutePath().equals(targetLocation.getAbsolutePath()))
                return new FileOperationData<>(SOURCE_SAME_AS_DESTINATION, targetLocation.getPath(), sourceLocation.getPath());

            if (overwrite) delete(targetLocation);

            try {
                if (sourceLocation.isDirectory()) {
                    if (targetLocation.exists() && !overwrite)
                        return new FileOperationData<>(DIRECTORY_ALREADY_EXISTS, targetLocation.getPath(), sourceLocation.getPath());
                    copyDirectory(sourceLocation, targetLocation, overwrite, 0);
                } else {
                    if (targetLocation.exists() && !overwrite)
                        return new FileOperationData<>(FILE_ALREADY_EXISTS, targetLocation.getPath(), sourceLocation.getPath());
                    copyFile(sourceLocation, targetLocation, overwrite, 0);
                }

                if (cancelled) {
                    if (dialog != null) dialog.dispose();
                    FileUtils.delete(targetLocation);
                    return new FileOperationData<>(CANCELLED, targetLocation.getPath(), sourceLocation.getPath());
                }

                return new FileOperationData<>(SUCCESS, targetLocation.getPath(), sourceLocation.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new FileOperationData<>(COULD_NOT_COPY_TO_TARGET, targetLocation.getPath(), sourceLocation.getPath());
        };
        startOperation();
    }

    private long getSize(File target) {
        if (target.isDirectory()) {
            return getFolderSize(target);
        } else {
            return getFileSize(target);
        }
    }

    private long getFileSize(File target) {
        return target.length();
    }

    private long getFolderSize(File target) {
        long result = 0;

        String[] fileNames = target.list();
        if (fileNames == null) return -1;
        for (String f : fileNames) {
            if (cancelled) {
                return -1;
            }

            if (new File(target, f).isDirectory()) {
                result += getFolderSize(new File(target, f));
            } else {
                result += getFileSize(new File(target, f));
            }
        }

        return result;
    }

    public IOperation<Long> urlCalcOperation(String url) {
        return () -> {
            if (dialog != null) dialog.setTitle("Connecting...");
            if (maxProgressInBytes < 1) {
                try {
                    URLConnection connection = new URL(url).openConnection();
                    return connection.getContentLengthLong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return maxProgressInBytes;
        };
    }

    /**
     * Calculates a zip input stream's size if it would have been decompressed
     * <br>
     * <b>This method does not work unfortunately so it's obsolete for now</b>
     *
     * @param zipInputStream The input stream of the zip file
     * @return The size of all the files inside the zip file if it would have been decompressed
     */
    private long getZipDecompressedSize(InputStream zipInputStream) {
        try (BufferedInputStream bis = new BufferedInputStream(zipInputStream);
             ZipInputStream zis = new ZipInputStream(bis)) {

            if (!isZipArchive(bis)) return -1;

            ZipEntry ze;
            int calculatedMaximumSize = 0;

            while ((ze = zis.getNextEntry()) != null) {
                if (cancelled) {
                    zis.closeEntry();
                    zis.close();
                    if (dialog != null) dialog.dispose();
                    return -1;
                }

                // Skip file if it is a __MACOSX folder or a file in a __MACOSX folder or if it is a .DS_Store file
                if (ze.getName().contains("__MACOSX") || ze.getName().contains(".DS_Store")) {
                    zis.closeEntry();
                    continue;
                }

                int currentEntrySize = (int) ze.getSize();
                System.out.println("Zipentry size: " + ze.getSize());
                System.out.println("Zipentry compressed size: " + ze.getCompressedSize());
                if (currentEntrySize > 0) calculatedMaximumSize += currentEntrySize;

                zis.closeEntry();
            }
            zis.closeEntry();

            return calculatedMaximumSize;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private FileOperationData<String> extractZipBackground(String destDirPath, InputStream zipFileInputStream, boolean overwriteWithZipContents) {
        // Get the destination directory
        File destinationDir = new File(destDirPath);

        // Create dest dir if it doesn't exist
        if (!destinationDir.exists()) destinationDir.mkdirs();

        if (!destinationDir.isDirectory())
            return new FileUtils.FileOperationData<>(NOT_A_DIRECTORY, destDirPath);

        try (BufferedInputStream bis = new BufferedInputStream(zipFileInputStream);
             ZipInputStream zis = new ZipInputStream(bis)) {

            if (!isZipArchive(bis)) return new FileUtils.FileOperationData<>(NOT_A_ZIP_ARCHIVE);

            List<String> paths = new ArrayList<>();
            ZipEntry ze;

            long progressInBytes = 0;

            while ((ze = zis.getNextEntry()) != null) {
                if (cancelled) {
                    zis.closeEntry();
                    zis.close();
                    for (String path : paths) {
                        FileUtils.delete(new File(path));
                    }
                    if (dialog != null) dialog.dispose();
                    return new FileOperationData<>(CANCELLED);
                }

                String zipEntryName = ze.getName();
                File currentFile = new File(destDirPath + File.separator + zipEntryName);

                // Skip file if it is a __MACOSX folder or a file in a __MACOSX folder or if it is a .DS_Store file
                if (currentFile.getPath().contains("__MACOSX") || currentFile.getPath().contains(".DS_Store")) {
                    zis.closeEntry();
                    continue;
                }

                // Add the current file path to the list that will be returned
                paths.add(currentFile.getAbsolutePath());

                // Delete the current file first if overwriteWithZipContents is enabled
                if (overwriteWithZipContents) delete(currentFile);

                // Create a directory if this zip entry is a directory or write the file to disk if it is not
                if (ze.isDirectory()) {
                    if (currentFile.exists()) {
                        zis.closeEntry();
                        zis.close();
                        return new FileUtils.FileOperationData<>(DIRECTORY_ALREADY_EXISTS, currentFile.getPath());
                    } else currentFile.mkdirs();
                } else {
                    if (currentFile.exists()) {
                        zis.closeEntry();
                        zis.close();
                        return new FileUtils.FileOperationData<>(FILE_ALREADY_EXISTS, currentFile.getPath());
                    } else {
                        FileOutputStream fos = new FileOutputStream(currentFile);
                        progressInBytes = copyStream(zis, fos, progressInBytes);
                        fos.close();
                    }
                }
                zis.closeEntry();
            }

            zis.closeEntry();
            return new FileUtils.FileOperationData<>(SUCCESS, paths);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FileUtils.FileOperationData<>(COULD_NOT_EXTRACT_ZIP);
    }

    private long copyDirectory(File source, File target, boolean overwrite, long progressInBytes) throws IOException {
        if (target.exists() && !overwrite) return progressInBytes;
        if (overwrite) delete(target);
        if (!target.exists()) target.mkdir();

        String[] fileNames = source.list();
        if (fileNames == null) return progressInBytes;
        for (String f : fileNames) {
            if (cancelled) {
                return progressInBytes;
            }

            if (new File(source, f).isDirectory()) {
                progressInBytes = copyDirectory(new File(source, f), new File(target, f), overwrite, progressInBytes);
            } else {
                progressInBytes = copyFile(new File(source, f), new File(target, f), overwrite, progressInBytes);
            }
        }

        return progressInBytes;
    }

    private long copyFile(File source, File target, boolean overwrite, long progressInBytes) throws IOException {
        if (target.exists() && !overwrite) return progressInBytes;
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(target)) {

            progressInBytes = copyStream(in, out, progressInBytes);
        }
        return progressInBytes;
    }

    /**
     * Copies an input stream to an output stream with a buffer size
     * of 4096 bytes while updating the current progress
     *
     * @param in              The input stream
     * @param out             The output stream
     * @param progressInBytes The progress in bytes that has already been done
     * @return The progress in bytes, either the same as the input or a larger value if at least 1 byte was copied
     * @throws IOException If an I/O error occurs
     */
    private long copyStream(InputStream in, OutputStream out, long progressInBytes) throws IOException {
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) > 0) {
            if (cancelled) {
                return progressInBytes;
            }

            out.write(buffer, 0, length);
            progressInBytes += length;
            postProgress(progressInBytes);
        }
        return progressInBytes;
    }

    public interface IOperation<T> {

        T doInBackground();
    }

    public interface CompletedListener {

        void completed(FileOperationData<String> data);
    }
}
