package net.gabor6505.java.pcbuilder.gui;

import net.gabor6505.java.pcbuilder.gui.dialog.AddProfileDialog;
import net.gabor6505.java.pcbuilder.gui.dialog.Dialogs;
import net.gabor6505.java.pcbuilder.utils.FileUtils;
import net.gabor6505.java.pcbuilder.utils.FileWorker;
import net.gabor6505.java.pcbuilder.utils.Utils;
import net.gabor6505.java.pcbuilder.xml.*;
import org.w3c.dom.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.gabor6505.java.pcbuilder.gui.ProfileManager.ProfileType.ZIP_FILE;
import static net.gabor6505.java.pcbuilder.gui.ProfileManager.ProfileType.ZIP_URL;
import static net.gabor6505.java.pcbuilder.utils.FileUtils.FileOperationResult.*;
import static net.gabor6505.java.pcbuilder.xml.XmlParser.*;

public class ProfileManager extends JComboBox<String> implements ActionListener {

    private static ProfileManager INSTANCE;

    public enum ProfileType {
        DIRECTORY,
        ZIP_FILE,
        ZIP_URL
    }

    private int prevSelectedIndex = -1;

    private final static String PROFILE_NODE_NAME = "profile";
    private final static String SELECTED_PROFILE_NODE_NAME = "selectedProfile";

    public final static String APP_DIRECTORY_NAME = System.getProperty("user.home") + "/.pcbuilder";

    public final static File APP_DIRECTORY = new File(APP_DIRECTORY_NAME);
    public final static File PROFILES_DIRECTORY = new File(APP_DIRECTORY_NAME + "/Profiles");
    public final static File PROFILES_INFO_FILE = new File(APP_DIRECTORY_NAME + "/profiles.xml");
    public final static File TEMP_DIRECTORY = new File(PROFILES_DIRECTORY.getPath() + "/Temp");

    private final static String defaultProfilesXml = "/sample_profiles.xml";
    private final static String defaultProfilesZip = "/Sample Profiles.zip";

    private final ComparisonPane comparisonPane;
    private final Dialogs dialogs;

    private int openDialogs = 0;

    public ProfileManager(ComparisonPane pane) {
        INSTANCE = this;
        comparisonPane = pane;
        dialogs = new Dialogs(pane.getParentFrame());
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.DARK_GRAY));

        //processXmlParseResult(XmlParseResult.IO_ERROR);

        checkIO();
        loadProfiles();

        addActionListener(this);
        selectItemFromXml();
    }

    /**
     * Check if the required files and folders exist and create them if they don't
     */
    public static void checkIO() {
        if (!APP_DIRECTORY.exists()) APP_DIRECTORY.mkdirs();
        if (!PROFILES_DIRECTORY.exists()) {
            FileUtils.extractZipFromJar(defaultProfilesZip, PROFILES_DIRECTORY).printResultIfError();
        }
        if (!PROFILES_INFO_FILE.exists()) {
            FileUtils.copyFileFromJar(defaultProfilesXml, PROFILES_INFO_FILE, false).printResultIfError();
        }
    }

    /**
     * Load the profile names from the profiles.xml file to a List, if they exist in the Profiles folder
     */
    public void loadProfiles() {
        XmlParser.viewXml(PROFILES_INFO_FILE.getPath(), (doc, nodes) -> {
            for (Node profile : nodes.getNodes(PROFILE_NODE_NAME)) {
                String name = profile.getNodeAttributeContent("name");

                File profileFolder = new File(PROFILES_DIRECTORY.getPath() + "/" + name);
                if (profileFolder.exists() && profileFolder.isDirectory()) {
                    addItem(name);
                }
            }
        });
    }

    /**
     * Selects the profile that was last selected when the app closed
     */
    public void selectItemFromXml() {
        XmlParser.viewXml(PROFILES_INFO_FILE.getPath(), (doc, nodes) -> {
            if (nodes.getNodes(SELECTED_PROFILE_NODE_NAME).size() > 0) {
                Node selectedProfileInfo = nodes.getNode(SELECTED_PROFILE_NODE_NAME);
                setSelectedItem(selectedProfileInfo.getTextContent());
            } else {
                selectProfile();
            }
        });
    }

    /**
     * Shows an AddProfileDialog to the user to add a profile if there are no open dialogs
     */
    public void addProfile() {
        if (openDialogs == 0) {
            openDialogs++;
            new AddProfileDialog(comparisonPane.getParentFrame(), this);
            openDialogs--;
        }
    }

    /**
     * Add a profile to the Profiles folder and to the profiles.xml file
     *
     * @param type The type of the profile
     * @param uri  The uri of the profile zip file or folder
     *             if type is DIRECTORY, then the path to the directory
     *             if type is ZIP_FILE, then the path to the zip file
     *             if type is ZIP_URL, then the url of the zip file to be downloaded
     */
    public void addProfile(ProfileType type, String uri) {
        EventQueue.invokeLater(() -> addProfileTask(type, uri));
    }

    private void addProfileTask(ProfileType type, String uri) {
        reload();

        AtomicBoolean profileAlreadyExisted = new AtomicBoolean(false);
        List<String> profileNamesToAdd = new ArrayList<>();

        FileWorker.CompletedListener finalListener = ((result) -> {
            if (result.getResult() != SUCCESS && result.getResult() != SOURCE_SAME_AS_DESTINATION) {
                dialogs.showErrorDialog(result);
                return;
            }

            // Construct a list of profile names to register
            if (result.size() > 0) {
                switch (type) {
                    case DIRECTORY:
                        profileNamesToAdd.add(new File(result.get(0)).getName());
                        break;
                    case ZIP_FILE:
                    case ZIP_URL:
                        int slashesInFirstPath = Utils.countChar(result.get(0), File.separatorChar);
                        for (String str : result) {
                            int slashes = Utils.countChar(str, File.separatorChar);
                            if (slashes == slashesInFirstPath) profileNamesToAdd.add(new File(str).getName());
                        }
                        break;
                }
            } else dialogs.showErrorDialog(UNKNOWN.getMessage());

            for (String profileName : profileNamesToAdd) {
                addProfile(profileName);
                System.out.println("Added new profile with name: " + profileName);
            }

            if (profileAlreadyExisted.get()) comparisonPane.reloadEverything();
        });

        FileWorker worker = new FileWorker(comparisonPane.getParentFrame(), true);

        switch (type) {
            case DIRECTORY:
                File sourceDir = new File(uri);
                if (!sourceDir.isDirectory()) {
                    dialogs.showErrorDialog("You have selected a file instead of a directory!");
                    return;
                }

                File targetDir = new File(PROFILES_DIRECTORY + "/" + sourceDir.getName());
                worker.setCompletedListener((result) -> {
                    if (result.getResult() == DIRECTORY_ALREADY_EXISTS) {
                        boolean dialogResult = dialogs.showProfileAlreadyExists(targetDir.getName());

                        if (dialogResult) {
                            profileAlreadyExisted.set(true);
                            FileWorker worker2 = new FileWorker(comparisonPane.getParentFrame(), true);
                            worker2.setCompletedListener(finalListener);
                            worker2.copy(sourceDir, targetDir, true);
                        }

                    } else finalListener.completed(result);
                });
                worker.copy(sourceDir, targetDir, false);
                break;
            case ZIP_FILE:
            case ZIP_URL:
                final String notZipArchiveMsg;

                if (type == ZIP_FILE) {
                    notZipArchiveMsg = "The selected file is not a zip archive!";
                } else if (type == ZIP_URL) {
                    notZipArchiveMsg = "The specified url does not point to a zip archive!";
                } else notZipArchiveMsg = "";

                worker.setCompletedListener((result) -> {
                    if (result.getResult() == NOT_A_ZIP_ARCHIVE) {
                        dialogs.showErrorDialog("Not a zip archive", notZipArchiveMsg);
                        return;
                    }

                    if (result.getResult() == DIRECTORY_ALREADY_EXISTS) {
                        boolean dialogResult = dialogs.showProfileAlreadyExists(new File(result.get(0)).getName());

                        if (dialogResult) {
                            profileAlreadyExisted.set(true);

                            FileWorker worker2 = new FileWorker(comparisonPane.getParentFrame(), true);
                            worker2.setCompletedListener(finalListener);

                            if (type == ZIP_FILE) {
                                worker2.extractZipFile(PROFILES_DIRECTORY.getPath(), uri, true);
                            } else if (type == ZIP_URL) {
                                worker2.extractZipFromUrl(PROFILES_DIRECTORY.getPath(), uri, true);
                            }
                        }

                    } else finalListener.completed(result);
                });

                if (type == ZIP_FILE) {
                    worker.extractZipFile(PROFILES_DIRECTORY.getPath(), uri, false);
                } else if (type == ZIP_URL)
                    worker.extractZipFromUrl(PROFILES_DIRECTORY.getPath(), uri, false);
                break;
        }
    }

    /**
     * Add a profile to the profiles.xml file
     *
     * @param profileName The name of the profile to be added
     */
    private void addProfile(String profileName) {
        if (new File(PROFILES_DIRECTORY.getPath() + "/" + profileName).isDirectory()) {
            XmlParser.editXml(PROFILES_INFO_FILE.getPath(), (doc, nodes) -> {

                boolean containsName = false;
                for (Node profile : nodes.getNodes(PROFILE_NODE_NAME)) {
                    if (profile.getNodeAttributeContent("name").equals(profileName)) {
                        containsName = true;
                    }
                }

                if (!containsName) {
                    Element profileElement = doc.createElement(PROFILE_NODE_NAME);
                    profileElement.setAttribute("name", profileName);
                    doc.getDocumentElement().appendChild(profileElement);

                    addItem(profileName);
                    return true;
                }
                return false;
            });

            EventQueue.invokeLater(() -> {
                setSelectedItem(profileName);
            });
        }
    }

    /**
     * Shows a dialog where the user can rename the currently selected profile if there are no open dialogs
     */
    public void renameProfile() {
        if (openDialogs == 0) {
            openDialogs++;
            String oldName = getSelectedItem() == null ? "" : getSelectedItem().toString();
            Object newName = JOptionPane.showInputDialog(comparisonPane.getParentFrame(),
                    "Please specify the new name for the profile:", "Rename profile",
                    JOptionPane.PLAIN_MESSAGE, null, null, oldName);

            if (newName != null) {
                renameProfile(oldName, newName.toString());
            }
            openDialogs--;
        }
    }

    /**
     * Renames a profile in the Profiles folder and in the profiles.xml file
     *
     * @param oldName The original name of the profile
     * @param newName The new name of the profile
     */
    public void renameProfile(String oldName, String newName) {
        reload();

        if (oldName.equals(newName) || newName.trim().isEmpty()) return;

        File[] files = PROFILES_DIRECTORY.listFiles();
        if (files == null) return;

        System.out.println("Renaming profile " + oldName + " to " + newName);

        boolean overwriteConfirmed = false;

        // Decide if the item already exists in ComboBox and needs to be overwritten
        for (int i = 0; i < getItemCount(); i++) {
            if (getItemAt(i).equals(newName)) {
                boolean dialogResult = dialogs.showProfileAlreadyExists(newName);
                if (!dialogResult) return;
                else overwriteConfirmed = true;
                break;
            }
        }

        final boolean overwrite = overwriteConfirmed;

        // Handle renaming in ComboBox
        for (int i = 0; i < getItemCount(); i++) {
            if (getItemAt(i).equals(oldName)) {
                // Remove action listener to avoid reloading while "editing" the item's name in the combo box
                removeActionListener(this);
                removeItemAt(i);

                if (!overwrite) {
                    insertItemAt(newName, i);
                }

                setSelectedItem(newName);
                selectProfile(newName, true);
                addActionListener(this);
                break;
            }
        }

        // Remove the new profile name from profiles.xml if it already exists
        removeProfileFromXml(newName);

        // Handle renaming in profiles.xml file
        XmlParser.editXml(PROFILES_INFO_FILE.getPath(), (doc, nodes) -> {
            for (Node profile : nodes.getNodes(PROFILE_NODE_NAME)) {
                if (profile.getNodeAttributeContent("name").equals(oldName)) {
                    profile.getNodeAttribute("name").getNode().setTextContent(newName);
                    return true;
                }
            }
            return false;
        });

        // Handle renaming in Profiles folder
        for (File profileDir : files) {
            if (!profileDir.isDirectory()) continue;

            if (profileDir.getName().equals(oldName)) {
                File destDir = new File(profileDir.getParent() + "/" + newName);

                if (destDir.exists()) {
                    if (overwrite) FileUtils.delete(destDir);
                    else {
                        boolean dialogResult = dialogs.showProfileAlreadyExists(newName);
                        if (!dialogResult) return;
                        else FileUtils.delete(destDir);
                    }
                }

                profileDir.renameTo(destDir);
                break;
            }
        }
    }

    /**
     * Shows a confirmation dialog that the profile will be removed (if there are no open dialogs), and then
     * removes the currently selected profile from the Profiles folder and from the profiles.xml file
     */
    public void removeProfile() {
        if (getItemCount() < 2) return;

        if (openDialogs == 0) {
            openDialogs++;
            Object selectedObj = getSelectedItem();
            if (selectedObj != null) {
                boolean result = dialogs.showConfirmDialog("Remove Profile", "Are you sure you want to remove profile " + selectedObj + "?");
                if (result) removeProfile(selectedObj.toString());
            }
            openDialogs--;
        }
    }

    /**
     * Remove a profile from the Profiles folder and from the profiles.xml file if there are more than 1 items in this ComboBox
     *
     * @param profileName The name of the profile to be removed
     */
    public void removeProfile(String profileName) {
        reload();
        if (getItemCount() < 2) return;

        removeProfileFromXml(profileName);
        removeItem(profileName);
    }

    private static void removeProfileFromXml(String profileName) {
        XmlParser.editXml(PROFILES_INFO_FILE.getPath(), (doc, nodes) -> {
            for (Node profile : nodes.getNodes(PROFILE_NODE_NAME)) {
                if (profile.getNodeAttributeContent("name").equals(profileName)) {
                    doc.getDocumentElement().removeChild(profile.getNode());
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (getSelectedIndex() != prevSelectedIndex) {
            prevSelectedIndex = getSelectedIndex();
            selectProfile();
        }
    }

    private boolean firstSelection = true;

    /**
     * Selects a profile by name and then reloads the components if disableReload is false
     *
     * @param profileName   The name of the profile to be selected
     * @param disableReload Set this to true to skip reloading the types and components
     */
    public void selectProfile(String profileName, boolean disableReload) {
        XmlParser.editXml(PROFILES_INFO_FILE.getPath(), (doc, nodes) -> {
            if (nodes.getNodes(SELECTED_PROFILE_NODE_NAME).size() > 0) {
                Node selectedProfileInfo = nodes.getNode(SELECTED_PROFILE_NODE_NAME);
                selectedProfileInfo.getNode().setTextContent(profileName);
                System.out.println("Changing selectedProfile to " + profileName + (disableReload ? " (with reload disabled)" : ""));
            } else {
                Element selectedProfileElement = doc.createElement(SELECTED_PROFILE_NODE_NAME);
                selectedProfileElement.appendChild(doc.createTextNode(profileName));
                doc.getDocumentElement().appendChild(selectedProfileElement);
            }
            return true;
        });

        XmlContract.setActiveProfile(profileName);
        if (!firstSelection && !disableReload) comparisonPane.reloadEverything();

        firstSelection = false;
        reload();
    }

    /**
     * Selects a profile by name and then reloads the components
     *
     * @param profileName The name of the profile to be selected
     */
    public void selectProfile(String profileName) {
        selectProfile(profileName, false);
    }

    /**
     * Selects the profile currently selected in this JComboBox and then reloads the components
     */
    public void selectProfile() {
        Object selectedObj = getSelectedItem();
        if (selectedObj != null) {
            selectProfile(selectedObj.toString());
        }
    }

    public void selectProfile(int index) {
        if (getItemCount() > index) {
            setSelectedIndex(index);
        }
    }

    // TODO validate that all the profiles inside the profiles.xml (and maybe in the Profiles folder) still exist (check for external modification)
    private void reload() {
        /*removeActionListener(this);
        removeAllItems();

        checkIO();
        loadProfiles();

        addActionListener(this);
        selectItemFromXml();*/
        checkIO();
    }

    public static ProfileManager getInstance() {
        return INSTANCE;
    }

    public int getOpenDialogCount() {
        return openDialogs;
    }

    public void incrementOpenDialogCount() {
        openDialogs++;
    }

    public void decrementOpenDialogCount() {
        openDialogs--;
    }

    private void processXmlParseResult(XmlParseResult result) {
        if (result != XmlParseResult.SUCCESS) {
            Dialogs.Result dialogResult = dialogs.showQuestionDialog("Can't read profiles file",
                    result.getMessage() + "\n Do you want to regenerate it?", Dialogs.ButtonType.YES_NO_CANCEL_OPTION);
            if (dialogResult == Dialogs.Result.CANCEL_OPTION) {
                System.exit(0);
            } else if (dialogResult == Dialogs.Result.YES_OK_OPTION) {

            }
        }
    }
}
