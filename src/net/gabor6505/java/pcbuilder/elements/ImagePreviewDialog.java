package net.gabor6505.java.pcbuilder.elements;

import net.gabor6505.java.pcbuilder.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import static net.gabor6505.java.pcbuilder.gui.MainFrame.APP_ICONS;

public class ImagePreviewDialog extends JDialog implements KeyListener {

    private ImageLabel imageLabel;

    /**
     * Creates a modal JDialog with the specified title which contains
     * an ImageLabel containing the image at the specified image path
     * The created dialog can be shown using <code>setVisible(true)</code>
     *
     * @param imagePath The path of the image that should be previewed
     * @param title     The title of the preview window/dialog
     * @param width     The width the image should be scaled to
     * @param height    The height the image should be scaled to
     */
    public ImagePreviewDialog(String imagePath, String title, int width, int height) {
        setTitle(title);
        setResizable(false);
        setBackground(Color.WHITE);
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImages(APP_ICONS);
        addKeyListener(this);

        imageLabel = new ImageLabel(imagePath, width, height, false);
        imageLabel.addKeyListener(this);
        imageLabel.setBackground(Color.WHITE);
        setContentPane(imageLabel);

        pack();
        Utils.fixSize(this);
        setLocationRelativeTo(null);
    }

    public ImageLabel getImageLabel() {
        return imageLabel;
    }

    /**
     * Updates the ImageLabel's image, the width and height of the dialog and label are unchanged
     * <br>
     * The new image will be scaled to fit inside the label
     *
     * @param imagePath The path of the new image to be shown
     */
    public void updateImage(String imagePath) {
        imageLabel.updateImage(imagePath, false);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
