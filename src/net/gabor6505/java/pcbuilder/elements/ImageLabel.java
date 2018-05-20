package net.gabor6505.java.pcbuilder.elements;

import net.gabor6505.java.pcbuilder.utils.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ImageLabel extends JLabel {

    public ImageLabel(String filePath, int width, int height, Container container) {
        Utils.fixSize(this, width, height);

        Image image = null;
        try {
            File imageFile = new File(filePath);
            image = ImageIO.read(imageFile).getScaledInstance(width, height, Image.SCALE_SMOOTH);
        } catch (IOException ignored) {
        }

        if (image != null) setIcon(new ImageIcon(image));
        if (container != null) container.add(this);
    }

    public ImageLabel(String filePath, int width, int height) {
        this(filePath, width, height, null);
    }

    public void setBorder(Color color, int thickness) {
        super.setBorder(BorderFactory.createLineBorder(color, thickness));
        Utils.fixSize(this, getWidth() + thickness * 2, getHeight() + thickness * 2);
    }
}
