package net.gabor6505.java.pcbuilder.utils;

import net.gabor6505.java.pcbuilder.xml.Node;
import net.gabor6505.java.pcbuilder.xml.XmlParser;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static net.gabor6505.java.pcbuilder.gui.ProfileManager.PROFILES_INFO_FILE;

public final class Format {

    public final static String CURRENCY_NODE_NAME = "currency";

    public static String currencyPrefix, currencySuffix;

    public final static String[] BYTES = new String[]{"KB", "MB", "GB"};
    public final static String[] HERTZ = new String[]{"kHz", "MHz", "GHz"};
    public final static String[] WATTS = new String[]{"mW", "W"};

    private Format() {

    }

    private static String formatUnitValue(double value, String[] units, boolean useDefaultUnit) {
        if (units == null) return null;

        NumberFormat formatter = DecimalFormat.getNumberInstance();
        formatter.setMaximumFractionDigits(2);

        String returnValue = "";

        if (useDefaultUnit) {
            if (units.length > 1) returnValue = formatter.format(value) + " " + units[1];
        } else {
            if (value >= 1000 && units.length > 2) {
                returnValue = formatter.format(value / 1000.0) + " " + units[2];
            } else if (value >= 1 && units.length > 1) {
                returnValue = formatter.format(value) + " " + units[1];
            } else if (value >= 0 && units.length > 0) {
                returnValue = formatter.format(value * 1000.0) + " " + units[0];
            }
        }
        return returnValue;
    }

    public static String formatUnitValue(double value, String[] units) {
        if (units == null) return null;
        return formatUnitValue(value, units, false);
    }

    public static String formatUnitValueDefault(double value, String[] units) {
        if (units == null) return null;
        return formatUnitValue(value, units, true);
    }

    public static String formatBoolean(boolean bool) {
        return bool ? "Yes" : "No";
    }

    public static String formatUnitValue(String value, String[] units) {
        if (value == null || units == null) return null;
        double convertedValue = Double.parseDouble(value);
        return formatUnitValue(convertedValue, units);
    }

    public static String formatUnitValueDefault(String value, String[] units) {
        if (value == null || units == null) return null;
        double convertedValue = Double.parseDouble(value);
        return formatUnitValueDefault(convertedValue, units);
    }

    public static String formatBoolean(String bool) {
        if (bool == null) return null;
        return bool.toLowerCase().equals("true") ? "Yes" : bool.toLowerCase().equals("false") ? "No" : "Unknown";
    }

    public static String formatCurrency(String value) {
        if (value == null) return null;

        NumberFormat formatter = DecimalFormat.getNumberInstance();
        formatter.setMaximumFractionDigits(2);

        double valueDouble = Double.parseDouble(value);
        return currencyPrefix + formatter.format(valueDouble) + currencySuffix;
    }

    public static void loadCurrency() {
        if (currencyPrefix != null && currencySuffix != null) return;

        XmlParser.viewXml(PROFILES_INFO_FILE.getPath(), (doc, nodes) -> {
            Node currency = nodes.getNode(CURRENCY_NODE_NAME);

            if (currency != null) {
                currencyPrefix = currency.getNodeAttributeContent("prefix");
                currencySuffix = currency.getNodeAttributeContent("suffix");
            } else System.out.println("Currency node doesn't exist!");
        });

        if (currencyPrefix == null) currencyPrefix = "";
        if (currencySuffix == null) currencySuffix = "";
    }
}
