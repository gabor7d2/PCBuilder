package net.gabor6505.java.pcbuilder.gui;

import net.gabor6505.java.pcbuilder.utils.FileUtils;

import javax.swing.*;

public class Dialogs {

    /**
     * Enum containing the different ButtonTypes a Question dialog can have
     */
    public enum ButtonType {
        DEFAULT_OPTION(-1),
        YES_NO_OPTION(0),
        YES_NO_CANCEL_OPTION(1),
        OK_CANCEL_OPTION(2);

        private final int value;

        ButtonType(int value) {
            this.value = value;
        }

        public int get() {
            return value;
        }
    }

    /**
     * Enum containing the possible return values of a Question dialog
     */
    public enum Result {
        YES_OK_OPTION,
        NO_OPTION,
        CANCEL_OPTION,
        CLOSED_OPTION,
        UNKNOWN_OPTION;

        private static Result getResult(int value) {
            if (value == 0) return YES_OK_OPTION;
            if (value == 1) return NO_OPTION;
            if (value == 2) return CANCEL_OPTION;
            if (value == -1) return CLOSED_OPTION;
            return UNKNOWN_OPTION;
        }
    }

    private final JFrame parentFrame;

    /**
     * Initializes a new Dialogs instance with the specified parent frame
     *
     * @param parentFrame The frame that will be used as the parent frame
     *                    of the dialogs created with this Dialogs instance
     */
    public Dialogs(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    /**
     * Shows an error dialog with title "Error" and with the specified message
     *
     * @param message The message to display
     */
    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows an error dialog with the specified title and message
     *
     * @param title   The title of the dialog to display
     * @param message The message to display
     */
    public void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(parentFrame, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows an error dialog with the title "Error" and with the message of the specified FileOperationData
     *
     * @param data FileOperationData to get the message from
     */
    public void showErrorDialog(FileUtils.FileOperationData data) {
        showErrorDialog(data.getMessage());
    }

    /**
     * Shows an error dialog with the specified title and the message of the specified FileOperationData
     *
     * @param title The title of the dialog to display
     * @param data  FileOperationData to get the message from
     */
    public void showErrorDialog(String title, FileUtils.FileOperationData data) {
        showErrorDialog(title, data.getMessage());
    }

    /**
     * Shows a question dialog with the specified title, message and button type
     *
     * @param title   The title of the dialog to display
     * @param message The message to display
     * @param type    Specifies what buttons should be displayed on the dialog.
     *                <br>
     *                Use one of the types available in {@link ButtonType}
     * @return The button that the user pressed
     */
    public Result showQuestionDialog(String title, String message, ButtonType type) {
        return Result.getResult(JOptionPane.showConfirmDialog(parentFrame, message, title, type.get()));
    }

    /**
     * Shows a confirm dialog to the user with the specified title and message
     * where the user can either select YES or NO
     *
     * @param title   The title of the dialog to display
     * @param message The message to display
     * @return True if the user pressed the YES button
     */
    public boolean showConfirmDialog(String title, String message) {
        return Result.getResult(JOptionPane.showConfirmDialog(parentFrame, message, title, JOptionPane.YES_NO_OPTION)) == Result.YES_OK_OPTION;
    }

    /**
     * Shows an input dialog to the user with the specified title and message
     * that contains a single TextField where the user can enter one line of text
     *
     * @param title   The title of the dialog to display
     * @param message The message to display
     * @return The text inside the TextField if the OK button was pressed or null if the dialog was cancelled
     */
    public String showInputDialog(String title, String message) {
        return JOptionPane.showInputDialog(parentFrame, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Shows an input dialog to the user with the specified title and message
     * that contains a single TextField where the user can enter one line of text
     *
     * @param parentFrame The parent frame to attach this dialog to
     * @param title       The title of the dialog to display
     * @param message     The message to display
     * @return The text inside the TextField if the OK button was pressed or null if the dialog was cancelled
     */
    public static String showInputDialog(JFrame parentFrame, String title, String message) {
        return JOptionPane.showInputDialog(parentFrame, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Shows a dialog asking the user to overwrite the existing profile or to cancel the operation
     * <br>
     * Contains an OK and a Cancel button and returns true only if the user pressed the OK button
     *
     * @param profileName The name of the profile that already exists
     * @return True if and only if the user pressed the OK button
     */
    public boolean showProfileAlreadyExists(String profileName) {
        return showConfirmDialog("Profile already exists",
                "A profile called " + profileName + " already exists! Do you want to overwrite it?");
    }
}
