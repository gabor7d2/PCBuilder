package net.gabor6505.java.pcbuilder.gui.dialog;

import net.gabor6505.java.pcbuilder.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class LoadingDialog extends JDialog implements WindowListener {

    private final static Dimension SIZE = new Dimension(384, 96);

    public LoadingDialog(JFrame parentFrame) {
        super(parentFrame);
        Utils.fixSize(this, SIZE);
        setResizable(false);

        setTitle("Loading...");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

        JPanel contentPanel = new JPanel(new GridLayout(1, 1));

        JProgressBar progress = new JProgressBar(SwingConstants.HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setBorder(BorderFactory.createLineBorder(contentPanel.getBackground(), 24));
        contentPanel.add(progress, new GridBagConstraints());

        setContentPane(contentPanel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
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
}
