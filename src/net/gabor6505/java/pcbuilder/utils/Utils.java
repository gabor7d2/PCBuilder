package net.gabor6505.java.pcbuilder.utils;

import sun.awt.PeerEvent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class Utils {

    private Utils() {

    }

    /**
     * Replaces all spaces in the input string with underscores ('_')
     *
     * @param inputStr The string to process
     * @return The string with all of it's spaces replaced with underscores
     */
    public static String replaceSpaces(String inputStr) {
        return inputStr.replace(' ', '_');
    }

    /**
     * Removes the extension of the specified file name
     * (by removing the last dot and everything after it from the file name)
     *
     * @param file The name of the file
     * @return The name of the file without it's extension
     */
    public static String removeExtension(String file) {
        if (file.contains(".")) {
            return file.substring(0, file.lastIndexOf('.'));
        } else return file;
    }

    /**
     * Fixes the specified component's size to it's preferred size
     * so that it won't be resized automatically
     *
     * @param comp The component that's size should be fixed
     */
    public static void fixSize(Component comp) {
        Dimension d = comp.getPreferredSize();
        comp.setSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(d);
        comp.setPreferredSize(d);
    }

    /**
     * Fixes the specified component's size to the specified size
     * so that it won't be resized automatically
     *
     * @param comp The component that's size should be fixed
     * @param d    The dimension that is used to fix the size of the component
     */
    public static void fixSize(Component comp, Dimension d) {
        comp.setSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(d);
        comp.setPreferredSize(d);
    }

    /**
     * Fixes the specified component's size to the specified width and height
     * so that it won't be resized automatically
     *
     * @param comp The component that's size should be fixed
     * @param width The width to be used to fix the component's size
     * @param height The width to be used to fix the component's size
     */
    public static void fixSize(Component comp, int width, int height) {
        fixSize(comp, new Dimension(width, height));
    }

    /**
     * Fixes the specified component's size to the size of the specified source component
     * so that it won't be resized automatically
     *
     * @param comp The component that's size should be fixed
     * @param componentSizeSource The component that is used to get the desired size from
     */
    public static void fixSize(Component comp, Component componentSizeSource) {
        fixSize(comp, componentSizeSource.getSize());
    }

    public static void adjustWidth(Component comp, int width) {
        fixSize(comp, comp.getWidth() + width, comp.getHeight());
    }

    public static void adjustWidth(Component comp, Component childComp) {
        adjustWidth(comp, childComp.getPreferredSize().width);
    }

    public static void adjustHeight(Component comp, int height) {
        fixSize(comp, comp.getWidth(), comp.getHeight() + height);
    }

    public static void adjustHeight(Component comp, Component childComp) {
        adjustHeight(comp, childComp.getPreferredSize().height);
    }

    /**
     * Opens the specified address in the default browser
     *
     * @param uri The uri which should be opened
     * @return True if the website was successfully opened or
     * false if the address is null or empty, or if an error occurred
     */
    public static boolean openWebsite(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Opens the specified address in the default browser
     *
     * @param url The url which should be opened
     * @return True if the website was successfully opened or
     * false if the address is null or empty, or if an error occurred
     */
    public static boolean openWebsite(URL url) {
        try {
            return openWebsite(url.toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Opens the specified address in the default browser
     *
     * @param address The website's address which should be opened
     * @return True if the website was successfully opened or
     * false if the address is null or empty, or if an error occurred
     */
    public static boolean openWebsite(String address) {
        if (address == null || address.isEmpty()) return false;
        try {
            return openWebsite(new URI(address));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Posts an event to the EventQueue with the specified priority
     *
     * @param priority The priority of the event
     * @param runnable The runnable that should be executed
     */
    public static void postEvent(long priority, Runnable runnable) {
        EventQueue eq = Toolkit.getDefaultToolkit().getSystemEventQueue();
        eq.postEvent(new PeerEvent(Toolkit.getDefaultToolkit(), runnable, priority));
    }

    /**
     * Counts how many characters of the specified type are in the input
     *
     * @param input The input to scan
     * @param c The character that should be counted
     * @return How many times the input contains the specified character
     */
    public static int countChar(String input, char c) {
        int counter = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == c) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Reads the specified text file inside the jar file
     * and puts a line separator between each line
     *
     * @param filePathInJar The path of the text file inside the jar
     * @return The text file's content
     */
    public static String readTextFromJar(String filePathInJar) {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(VersionManager.class.getResourceAsStream(filePathInJar), StandardCharsets.UTF_8))) {
            String line;
            while((line = br.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (Exception e) {
            return null;
        }

        return builder.toString();
    }
}
