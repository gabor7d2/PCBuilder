package net.gabor6505.java.pcbuilder.gui.dialog;

import net.gabor6505.java.pcbuilder.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProgressDialog extends JDialog implements WindowListener, PropertyChangeListener {

    private final static Dimension SIZE = new Dimension(384, 96);

    private final ProgressDialogType type;
    private final DialogClosingListener listener;

    private final JProgressBar progressBar;

    public ProgressDialog(JFrame parentFrame, ProgressDialogType type, String title) {
        super(parentFrame);
        this.type = type;

        if (type.getListener() != null) {
            this.listener = type.getListener();
        } else {
            this.listener = (e, dialog) -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }

        Utils.fixSize(this, SIZE);
        setResizable(false);
        setModalityType(type.isModal() ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);

        if (title == null) setTitle("Loading...");
        else setTitle(title);

        setDefaultCloseOperation(type.getCloseOperation());
        addWindowListener(this);

        JPanel contentPanel = new JPanel(new GridLayout(1, 1));

        progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
        progressBar.setIndeterminate(type.isIndeterminate());
        progressBar.setBorder(BorderFactory.createLineBorder(contentPanel.getBackground(), 24));
        contentPanel.add(progressBar, new GridBagConstraints());
        setContentPane(contentPanel);

        setLocationRelativeTo(null);
    }

    public ProgressDialog(JFrame parentFrame, ProgressDialogType type) {
        this(parentFrame, type, null);
    }

    /**
     * Makes the dialog visible without blocking the caller method
     * (Places <code>setVisible(true)</code> in the EventQueue)
     */
    public void setVisible() {
        EventQueue.invokeLater(() -> setVisible(true));
    }

    /**
     * Makes the dialog visible without blocking the caller method
     * (Places <code>setVisible(true)</code> in the EventQueue)
     * <br><br>
     * Also notifies the specified AtomicBoolean by setting
     * it's value to true when the dialog appears and to false
     * when the dialog is dismissed
     */
    public void setVisible(AtomicBoolean bool) {
        EventQueue.invokeLater(() -> {
            bool.set(true);
            setVisible(true);
            bool.set(false);
        });
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (listener != null) {
            listener.dialogClosing(e, this);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * Sets the ProgressBar of this dialog to the specified percent
     *
     * @param percent The percent, between 0 and 100
     */
    public void setProgress(int percent) {
        progressBar.setValue(percent);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (progressBar.isIndeterminate()) return;
        if (evt.getPropertyName().equals("progress")) {
            setProgress((Integer) evt.getNewValue());
        }
    }

    public interface DialogClosingListener {

        void dialogClosing(WindowEvent e, ProgressDialog dialog);
    }
}
