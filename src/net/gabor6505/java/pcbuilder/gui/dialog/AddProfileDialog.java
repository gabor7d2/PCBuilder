package net.gabor6505.java.pcbuilder.gui.dialog;

import net.gabor6505.java.pcbuilder.gui.Dialogs;
import net.gabor6505.java.pcbuilder.gui.ProfileManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class AddProfileDialog {

    public AddProfileDialog(JFrame parentFrame, ProfileManager profileMgr) {
        String[] dialogOptions = new String[]{"URL", "Zip file", "Folder"};

        int chosenOption = JOptionPane.showOptionDialog(parentFrame,
                "Choose the data source of the profile to be loaded:",
                "Add Profile", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, dialogOptions, null);

        switch (chosenOption) {
            // URL
            case 0:
                String url = Dialogs.showInputDialog(parentFrame, "Specify URL", "Please specify a URL pointing to a Zip archive:");
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
                System.setProperty("apple.awt.fileDialogForDirectories", "true");
                FileDialog dialog3 = new FileDialog(parentFrame, "Choose a profile folder", FileDialog.LOAD);
                dialog3.setDirectory(ProfileManager.PROFILES_DIRECTORY.getPath());
                dialog3.setVisible(true);

                if (dialog3.getFile() != null && dialog3.getDirectory() != null) {
                    File directory = new File(dialog3.getDirectory() + dialog3.getFile());
                    if (directory.isDirectory()) {
                        profileMgr.addProfile(ProfileManager.ProfileType.DIRECTORY, directory.getPath());
                    }
                }
                System.setProperty("apple.awt.fileDialogForDirectories", "false");
                break;
        }
    }
}
