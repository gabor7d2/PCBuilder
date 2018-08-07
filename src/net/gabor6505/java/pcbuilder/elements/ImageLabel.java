package net.gabor6505.java.pcbuilder.elements;

import net.gabor6505.java.pcbuilder.utils.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static sun.awt.PeerEvent.LOW_PRIORITY_EVENT;

// TODO Fix memory leak inside ImageLabels (probably the Images stay in memory after the Label has been destroyed)
public class ImageLabel extends JLabel {

    private static List<ImageWorker> workers = new ArrayList<>();

    // Counts how many full reloads have been done
    private static int RELOAD_COUNT = 0;

    // Stores what reload count was when this ImageLabel was created
    private final int currentReloadCount;

    private String imagePath;
    private int width, height;

    private class ImageWorker extends SwingWorker<Icon, Object> {

        private final Icon icon;

        private ImageWorker() {
            icon = null;
        }

        private ImageWorker(Icon imageIcon) {
            icon = imageIcon;
        }

        @Override
        protected Icon doInBackground() {
            if (RELOAD_COUNT > currentReloadCount) return null;
            if (icon != null) return icon;
            return loadImage(imagePath, width, height);
            //return null;
        }

        @Override
        protected void done() {
            EventQueue.invokeLater(() -> {
                try {
                    setIcon(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Makes a new ImageLabel with the specified image inside it and places it in the specified container
     *
     * @param imagePath  The path of the image (can be null)
     * @param imageLabel The ImageLabel that should be used to get the image from and display it
     * @param width      The width of the label
     * @param height     The height of the label
     * @param container  The container in which this label should be added, if it is null then it
     *                   is not added to any container
     */
    public ImageLabel(String imagePath, ImageLabel imageLabel, int width, int height, Container container) {
        this.imagePath = imagePath;
        this.width = width;
        this.height = height;
        currentReloadCount = RELOAD_COUNT;
        Utils.fixSize(this, width, height);

        workers.add(new ImageWorker(imageLabel.getIcon()));

        if (container != null) container.add(this);
    }

    /**
     * Makes a new ImageLabel with the specified image inside it and places it in the specified container
     *
     * @param imagePath  The path of the image (can be null)
     * @param labelImage The image that should be displayed on this label
     * @param width      The width of the label
     * @param height     The height of the label
     * @param container  The container in which this label should be added, if it is null then it
     *                   is not added to any container
     */
    public ImageLabel(String imagePath, Icon labelImage, int width, int height, Container container) {
        this.imagePath = imagePath;
        this.width = width;
        this.height = height;
        currentReloadCount = RELOAD_COUNT;
        Utils.fixSize(this, width, height);

        workers.add(new ImageWorker(labelImage));

        if (container != null) container.add(this);
    }

    /**
     * Makes a new ImageLabel with the specified image inside it
     *
     * @param imagePath  The path of the image (can be null)
     * @param labelImage The ImageIcon that should be displayed on this label
     * @param width      The width of the label
     * @param height     The height of the label
     */
    public ImageLabel(String imagePath, ImageIcon labelImage, int width, int height) {
        this(imagePath, labelImage, width, height, null);
    }

    /**
     * Makes a new ImageLabel with the specified width and height, places it in the specified container
     * and loads in an image from the specified file path to place onto the label
     * <br><br>
     * If loadInBackground is set to true, the image will be loaded as a background task,
     * thus not slowing down other things
     * <br><br>
     * The width and the height of the label don't have to match the image's resolution, because
     * the image gets scaled to fit the label (smooth scaling)
     *
     * @param imagePath        The path of the image to be placed onto this label
     * @param width            The width of the label
     * @param height           The height of the label
     * @param container        The container in which this label should be added, if it is null then it
     *                         is not added to any container
     * @param loadInBackground Set this to true to load the image in a background
     *                         thread so it doesnt slow down other things in the UI
     */
    public ImageLabel(String imagePath, int width, int height, Container container, boolean loadInBackground) {
        this.imagePath = imagePath;
        this.width = width;
        this.height = height;
        currentReloadCount = RELOAD_COUNT;

        Utils.fixSize(this, width, height);
        loadImage(loadInBackground);

        if (container != null) container.add(this);
    }

    /**
     * Makes a new ImageLabel with the specified width and height
     * and loads in an image from the specified file path to place onto the label
     * <br><br>
     * If loadInBackground is set to true, the image will be loaded as a background task,
     * thus not slowing down other things
     * <br><br>
     * The width and the height of the label don't have to match the image's resolution, because
     * the image gets scaled to fit the label (smooth scaling)
     *
     * @param imagePath        The path of the image to be placed onto this label
     * @param width            The width of the label
     * @param height           The height of the label
     * @param loadInBackground Set this to true to load the image in a background
     *                         thread so it doesnt slow down other things in the UI
     */
    public ImageLabel(String imagePath, int width, int height, boolean loadInBackground) {
        this(imagePath, width, height, null, loadInBackground);
    }

    /**
     * Makes a new ImageLabel with the specified width and height, places it in the specified container
     * and loads in an image from the specified file path to place onto the label
     * <br><br>
     * The image will be loaded as a background task, thus not slowing down other things
     * If you want to disable background loading, use {@link #ImageLabel(String, int, int, Container, boolean)}
     * <br><br>
     * The width and the height of the label don't have to match the image's resolution, because
     * the image gets scaled to fit the label (smooth scaling)
     *
     * @param imagePath The path of the image to be placed onto this label
     * @param width     The width of the label
     * @param height    The height of the label
     * @param container The container in which this label should be added, if it is null then it
     *                  is not added to any container
     */
    public ImageLabel(String imagePath, int width, int height, Container container) {
        this(imagePath, width, height, container, true);
    }

    /**
     * Makes a new ImageLabel with the specified width and height
     * and loads in an image from the specified file path to place onto the label
     * <br><br>
     * The image will be loaded as a background task, thus not slowing down other things
     * If you want to disable background loading, use {@link #ImageLabel(String, int, int, boolean)}
     * <br><br>
     * The width and the height of the label don't have to match the image's resolution, because
     * the image gets scaled to fit the label (smooth scaling)
     *
     * @param imagePath The path of the image to be placed onto this label
     * @param width     The width of the label
     * @param height    The height of the label
     */
    public ImageLabel(String imagePath, int width, int height) {
        this(imagePath, width, height, null, true);
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getLabelWidth() {
        return width;
    }

    public int getLabelHeight() {
        return height;
    }

    /**
     * Sets the border of this ImageLabel to the specified color and thickness
     * <br>
     * If this method is used more than once on the same instance, the label's size will break!
     *
     * @param color     The color of the border
     * @param thickness The thickness of the border (in pixels)
     */
    public void setBorder(Color color, int thickness) {
        super.setBorder(BorderFactory.createLineBorder(color, thickness));
        Utils.fixSize(this, getWidth() + thickness * 2, getHeight() + thickness * 2);
    }

    /**
     * Changes the ImageLabel's image to the specified image
     * <br><br>
     * The label's width and height will match the specified image's width and height
     *
     * @param labelImage The ImageIcon that should be displayed on this label
     */
    public void updateImage(ImageIcon labelImage) {
        Utils.fixSize(this, labelImage.getIconWidth(), labelImage.getIconHeight());
        setIcon(labelImage);
    }

    /**
     * Changes the ImageLabel's image to an image that's at the specified imagePath
     *
     * @param filePath         The file path of the new image
     * @param width            The width the label should be
     * @param height           The height the label should be
     * @param loadInBackground Set this to true to load the image in a background
     *                         thread so it doesnt slow down other things in the UI
     */
    public void updateImage(String filePath, int width, int height, boolean loadInBackground) {
        this.imagePath = filePath;
        this.width = width;
        this.height = height;

        Utils.fixSize(this, width, height);
        loadImage(loadInBackground);
    }

    /**
     * Changes the ImageLabel's image to an image that's at the specified imagePath
     * <br><br>
     * The load happens in a background task by default, use {@link ImageLabel#updateImage(String, int, int, boolean)}
     * instead if you want to specify whether the image should be loaded in the background or not
     *
     * @param filePath The file path of the new image
     * @param width    The width the label should be
     * @param height   The height the label should be
     */
    public void updateImage(String filePath, int width, int height) {
        updateImage(filePath, width, height, true);
    }

    /**
     * Changes the ImageLabel's image to an image that's at the specified imagePath
     * <br><br>
     * The label's size will stay the same and the image will be scaled to fit the label
     *
     * @param filePath         The file path of the new image
     * @param loadInBackground Set this to true to load the image in a background
     *                         thread so it doesnt slow down other things in the UI
     */
    public void updateImage(String filePath, boolean loadInBackground) {
        updateImage(filePath, width, height, loadInBackground);
    }

    /**
     * Changes the ImageLabel's image to an image that's at the specified imagePath
     * <br><br>
     * The load happens in a background task by default, use {@link ImageLabel#updateImage(String, int, int, boolean)}
     * instead if you want to specify whether the image should be loaded in the background or not
     * <br><br>
     * The label's size will stay the same and the image will be scaled to fit the label
     *
     * @param filePath The file path of the new image
     */
    public void updateImage(String filePath) {
        updateImage(filePath, width, height, true);
    }

    private void loadImage(boolean inBackground) {
        if (RELOAD_COUNT > currentReloadCount) return;
        if (inBackground) {
            workers.add(new ImageWorker());
        } else {
            setIcon(loadImage(imagePath, width, height));
        }
    }

    private static Icon loadImage(String filePath, int width, int height) {
        Image image = null;

        try {
            File imageFile = new File(filePath);
            image = ImageIO.read(imageFile).getScaledInstance(width, height, Image.SCALE_SMOOTH);
        } catch (IOException e) {

        }

        if (image == null) return null;
        return new ImageIcon(image);
    }

    public static void startWorkers() {
        for (ImageWorker w : workers) {
            w.execute();
        }
    }

    /**
     * Discards all currently running background tasks, so any images that didn't start loading yet gets cancelled and left as an empty label
     */
    public static void discardBackgroundTasks() {
        RELOAD_COUNT++;
    }
}
