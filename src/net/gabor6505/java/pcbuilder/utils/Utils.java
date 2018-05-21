package net.gabor6505.java.pcbuilder.utils;

import java.awt.*;
import java.net.URI;
import java.net.URL;

public final class Utils {

    private Utils() {
    }

    public static String replaceSpaces(String inputStr) {
        return inputStr.replace(' ', '_');
    }

    public static String removeExtension(String file) {
        if (file.contains(".")) {
            return file.substring(0, file.lastIndexOf('.'));
        } else return file;
    }

    public static void fixSize(Component comp, Dimension d) {
        comp.setSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(d);
        comp.setPreferredSize(d);
    }

    public static void fixSize(Component comp, int width, int height) {
        fixSize(comp, new Dimension(width, height));
    }

    public static void fixSize(Component comp, Component componentSizeSource) {
        fixSize(comp, componentSizeSource.getSize());
    }

    public static void adjustHeight(Component comp, int height) {
        Utils.fixSize(comp, comp.getWidth(), comp.getHeight() + height);
    }

    public static void adjustHeight(Component comp, Component childComp) {
        Utils.adjustHeight(comp, childComp.getMinimumSize().height);
    }

    public static void adjustWidth(Component comp, int width) {
        Utils.fixSize(comp, comp.getWidth() + width, comp.getHeight());
    }

    public static void adjustWidth(Component comp, Component childComp) {
        Utils.adjustWidth(comp, childComp.getMinimumSize().width);
    }

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

    public static boolean openWebsite(URL url) {
        try {
            return openWebsite(url.toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean openWebsite(String address) {
        try {
            return openWebsite(new URI(address));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
