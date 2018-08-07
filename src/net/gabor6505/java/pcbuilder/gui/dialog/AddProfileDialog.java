package net.gabor6505.java.pcbuilder.gui.dialog;

import net.gabor6505.java.pcbuilder.gui.ProfileManager;
import net.gabor6505.java.pcbuilder.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static javax.swing.JFileChooser.APPROVE_OPTION;

public class AddProfileDialog {

    private static boolean firstFolderDialog = true;

    private static String previousFolder = ProfileManager.PROFILES_DIRECTORY.getPath();

    public AddProfileDialog(JFrame parentFrame, ProfileManager profileMgr) {
        String[] dialogOptions = new String[]{"URL", "Zip file", "Folder"};

        int chosenOption = JOptionPane.showOptionDialog(parentFrame,
                "Choose the data source of the profile to be loaded:",
                "Add Profile", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, dialogOptions, null);

        switch (chosenOption) {
            // URL
            case 0:
                String url = Dialogs.showInputDialog(parentFrame, "Specify URL",
                        "Please specify a URL pointing to a Zip archive:\n\n(Note that the progress bar might not show any\n" +
                                "progress being made if the url doesn't support it)");
                if (url != null) profileMgr.addProfile(ProfileManager.ProfileType.ZIP_URL, url);
                break;
            // Zip file
            case 1:
                FileDialog dialog2 = new FileDialog(parentFrame, "Choose one or more profile zip file(s)", FileDialog.LOAD);
                dialog2.setMultipleMode(true);
                dialog2.setVisible(true);

                for (File file : dialog2.getFiles()) {
                    profileMgr.addProfile(ProfileManager.ProfileType.ZIP_FILE, file.getPath());
                }
                break;
            // Folder
            case 2:
                // Use FileDialog if OS is Mac OS (because it looks native) and JFileChooser if OS is not Mac OS
                if (System.getProperty("os.name").toLowerCase().contains("mac os")) {
                    System.setProperty("apple.awt.fileDialogForDirectories", "true");
                    FileDialog dialog3 = new FileDialog(parentFrame, "Choose a profile folder", FileDialog.LOAD);
                    if (firstFolderDialog) {
                        dialog3.setDirectory(ProfileManager.PROFILES_DIRECTORY.getPath());
                        firstFolderDialog = false;
                    }
                    dialog3.setVisible(true);

                    if (dialog3.getFile() != null && dialog3.getDirectory() != null) {
                        File directory = new File(dialog3.getDirectory() + dialog3.getFile());
                        if (directory.isDirectory()) {
                            profileMgr.addProfile(ProfileManager.ProfileType.DIRECTORY, directory.getPath());
                        } else new Dialogs(parentFrame).showErrorDialog("The selected item is not a directory!");
                    }
                    System.setProperty("apple.awt.fileDialogForDirectories", "false");
                } else {
                    JFileChooser chooser = new JFileChooser(previousFolder);
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setDialogTitle("Choose a profile folder");
                    int returnValue = chooser.showDialog(parentFrame, "OK");

                    if (returnValue == APPROVE_OPTION) {
                        if (chooser.getSelectedFile() != null) {
                            File directory = chooser.getSelectedFile();
                            if (directory.isDirectory()) {
                                profileMgr.addProfile(ProfileManager.ProfileType.DIRECTORY, directory.getPath());
                                previousFolder = directory.getPath();
                            } else new Dialogs(parentFrame).showErrorDialog(new FileUtils.FileOperationData(FileUtils.FileOperationResult.NOT_A_DIRECTORY));
                        }
                    }
                }
                break;
        }
    }
}
