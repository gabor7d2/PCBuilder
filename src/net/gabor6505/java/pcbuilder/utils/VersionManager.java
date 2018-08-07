package net.gabor6505.java.pcbuilder.utils;

import net.gabor6505.java.pcbuilder.gui.ProfileManager;
import net.gabor6505.java.pcbuilder.gui.dialog.ScrollableDialog;
import net.gabor6505.java.pcbuilder.xml.Node;
import net.gabor6505.java.pcbuilder.xml.XmlParser;
import org.w3c.dom.Element;

import javax.swing.*;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.awt.Font.BOLD;
import static net.gabor6505.java.pcbuilder.gui.ProfileManager.PROFILES_INFO_FILE;

// TODO Implement update checking
public class VersionManager {

    private final static double VERSION = 1.2;
    private static double previousVersion = 1.0;

    public final static String CURRENT_VERSION_NODE_NAME = "currentVersion";
    public final static String CHANGELOG_FOLDER_IN_JAR = "/changelogs/";
    public final static String HELP_FILE_IN_JAR = "/help.txt";

    static {
        XmlParser.viewXml(PROFILES_INFO_FILE.getPath(), (doc, nodes) -> {
            if (nodes.getNodes(CURRENT_VERSION_NODE_NAME).size() > 0) {
                Node selectedProfileInfo = nodes.getNode(CURRENT_VERSION_NODE_NAME);

                try {
                    previousVersion = Double.parseDouble(selectedProfileInfo.getTextContent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        XmlParser.editXml(PROFILES_INFO_FILE.getPath(), (doc, nodes) -> {
            if (nodes.getNodes(CURRENT_VERSION_NODE_NAME).size() > 0) {
                if (VERSION < previousVersion) return false;
                Node selectedProfileInfo = nodes.getNode(CURRENT_VERSION_NODE_NAME);
                selectedProfileInfo.getNode().setTextContent(String.valueOf(VERSION));
            } else {
                Element selectedProfileElement = doc.createElement(CURRENT_VERSION_NODE_NAME);
                selectedProfileElement.appendChild(doc.createTextNode(String.valueOf(VERSION)));
                doc.getDocumentElement().appendChild(selectedProfileElement);
            }
            return true;
        });
    }

    private VersionManager() {

    }

    /**
     * Checks whether the current version of the app is the same as the version that was previously ran
     *
     * @return True if the version of the app changed by running this version
     * (or if the app has never been started before) or false if this is the same version as the previously
     * started version
     */
    public static boolean didVersionChange() {
        return VERSION != previousVersion;
    }

    /**
     * Checks whether the current version of the app is newer than the version that was previously ran
     *
     * @return True if the version of the app is newer than the previously ran version
     */
    public static boolean hasAppUpdated() {
        return VERSION > previousVersion;
    }

    /**
     * Shows the welcome dialog
     * <br><br>
     * Also notifies the specified AtomicBoolean by setting
     * it's value to true when the dialog appears and to false
     * when the dialog is dismissed
     */
    public static void showWelcomeDialog(JFrame parentFrame, AtomicBoolean bool) {
        String message = "Welcome to PC Builder version " + VERSION + "!";
        StringBuilder changelogBuilder = new StringBuilder();

        for (int version = (int) (VERSION * 10); version > (int) (previousVersion * 10); version--) {
            double currentVersion = (double) version / 10.0;
            System.out.println("Loading change log for version " + currentVersion);
            String changelog = Utils.readTextFromJar(CHANGELOG_FOLDER_IN_JAR + currentVersion + ".txt");

            if (changelog != null) {
                changelogBuilder.append("What's new in version ").append(currentVersion).append(":");
                changelogBuilder.append("\n\n").append(changelog).append("\n\n");
            }
        }

        if (changelogBuilder.length() > 1) {
            changelogBuilder.delete(changelogBuilder.length() - 3, changelogBuilder.length() - 1);
        }

        ScrollableDialog dialog = new ScrollableDialog(parentFrame, "Welcome", message, changelogBuilder.toString());
        Font font = dialog.getMessageArea().getFont();
        dialog.getMessageArea().setFont(new Font(font.getName(), BOLD, font.getSize()));
        EventQueue.invokeLater(() -> {
            if (bool != null) bool.set(true);
            dialog.getDialog().setVisible(true);
            if (bool != null) bool.set(false);
        });
    }

    /**
     * Shows the welcome dialog if this is a newer version than the previously ran version
     * <br><br>
     * Also notifies the specified AtomicBoolean by setting
     * it's value to true when the dialog appears and to false
     * when the dialog is dismissed
     */
    public static void showWelcomeDialogIfNewerVersion(JFrame parentFrame, AtomicBoolean bool) {
        if (!hasAppUpdated()) return;
        showWelcomeDialog(parentFrame, bool);
    }

    /**
     * Shows a help dialog
     */
    public static void showHelpDialog(JFrame parentFrame) {
        if (ProfileManager.getInstance().getOpenDialogCount() != 0) return;
        String help = Utils.readTextFromJar(HELP_FILE_IN_JAR);
        ScrollableDialog dialog = new ScrollableDialog(parentFrame, "Help", null, help);
        EventQueue.invokeLater(dialog::display);
    }
}
