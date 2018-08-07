package net.gabor6505.java.pcbuilder.gui.dialog;

import net.gabor6505.java.pcbuilder.gui.ProfileManager;
import net.gabor6505.java.pcbuilder.utils.Utils;

import javax.swing.*;
import java.awt.*;

public class ScrollableDialog {

    private final static Dimension scrollPaneMaxSize = new Dimension(512, 512);

    private final JTextArea messageArea;
    private final JTextArea scrollableMessageArea;

    private final JDialog dialog;

    public ScrollableDialog(JFrame parentFrame, String title, String message, String scrollableMessage) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if (message != null) {
            messageArea = new JTextArea(message);
            messageArea.setEditable(false);
            messageArea.setBackground(panel.getBackground());
            messageArea.setBorder(BorderFactory.createMatteBorder(16, 0, 16, 0, panel.getBackground()));

            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                messageArea.setFont(messageArea.getFont().deriveFont(12f));
            }

            panel.add(messageArea);
        } else messageArea = null;

        if (scrollableMessage != null) {
            scrollableMessageArea = new JTextArea(scrollableMessage);
            scrollableMessageArea.setEditable(false);
            scrollableMessageArea.setBackground(panel.getBackground());
            scrollableMessageArea.setBorder(BorderFactory.createLineBorder(panel.getBackground(), 4));

            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                scrollableMessageArea.setFont(scrollableMessageArea.getFont().deriveFont(12f));
            }

            JScrollPane scrollPane = new JScrollPane(scrollableMessageArea);
            scrollPane.setBackground(panel.getBackground());
            scrollPane.getViewport().setBackground(panel.getBackground());
            scrollPane.setBorder(null);
            panel.add(scrollPane);

            if (scrollPane.getPreferredSize().width > scrollPaneMaxSize.width) {
                scrollPane.setPreferredSize(new Dimension(scrollPaneMaxSize.width, scrollPane.getPreferredSize().height));
            }
            if (scrollPane.getPreferredSize().height > scrollPaneMaxSize.height) {
                scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, scrollPaneMaxSize.height));
            }

            if (scrollPane.getVerticalScrollBar().isVisible()) {
                Utils.fixSize(scrollPane, new Dimension(scrollPane.getPreferredSize().width + scrollPane.getVerticalScrollBar().getPreferredSize().width, scrollPane.getPreferredSize().height));
            }
            if (scrollPane.getHorizontalScrollBar().isVisible()) {
                Utils.fixSize(scrollPane, new Dimension(scrollPane.getPreferredSize().width, scrollPane.getPreferredSize().height + scrollPane.getHorizontalScrollBar().getPreferredSize().height));
            }
        } else scrollableMessageArea = null;

        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE);
        dialog = optionPane.createDialog(parentFrame, title);
    }

    /**
     * Displays this dialog, this method does not return until the dialog has been closed
     */
    public void display() {
        boolean handleDialogCount = ProfileManager.getInstance() != null;
        if (handleDialogCount) ProfileManager.getInstance().incrementOpenDialogCount();
        dialog.setVisible(true);
        if (handleDialogCount) ProfileManager.getInstance().decrementOpenDialogCount();
    }

    public JTextArea getMessageArea() {
        return messageArea;
    }

    public JTextArea getScrollableMessageArea() {
        return scrollableMessageArea;
    }

    public JDialog getDialog() {
        return dialog;
    }


}
