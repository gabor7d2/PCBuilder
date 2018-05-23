package net.gabor6505.java.pcbuilder.xml;

import net.gabor6505.java.pcbuilder.utils.Format;

import java.util.Arrays;

public class ComponentProperties {

    private final String[] nodeNames;
    private final Object[] properties;

    public ComponentProperties(Object[] objectArray, String[] correspondingNodeNames) {
        properties = objectArray;
        nodeNames = correspondingNodeNames;
    }

    private void doCheck() {
        if (properties.length != nodeNames.length) throw new IllegalStateException();
    }

    public Object getObject(int index) {
        doCheck();
        if (properties.length < index || index < 0) return null;
        return properties[index];
    }

    public String getString(int index) {
        doCheck();
        if (getObject(index) == null) return null;
        return getObject(index).toString();
    }

    public boolean getBoolean(int index) {
        doCheck();
        return Boolean.parseBoolean(getObject(index).toString());
    }

    public int getInt(int index) {
        doCheck();
        if (getObject(index) == null) return 0;
        return Integer.parseInt(getObject(index).toString());
    }

    public short getShort(int index) {
        doCheck();
        if (getObject(index) == null) return 0;
        return Short.parseShort(getObject(index).toString());
    }

    public double getDouble(int index) {
        doCheck();
        if (getObject(index) == null) return 0;
        return Double.parseDouble(getObject(index).toString());
    }

    public long getLong(int index) {
        doCheck();
        if (getObject(index) == null) return 0;
        return Long.parseLong(getObject(index).toString());
    }

    public Object getObject(String key) {
        return getObject(Arrays.asList(nodeNames).indexOf(key));
    }

    public String getString(String key) {
        return getString(Arrays.asList(nodeNames).indexOf(key));
    }

    public boolean getBoolean(String key) {
        return getBoolean(Arrays.asList(nodeNames).indexOf(key));
    }

    public int getInt(String key) {
        return getInt(Arrays.asList(nodeNames).indexOf(key));
    }

    public short getShort(String key) {
        return getShort(Arrays.asList(nodeNames).indexOf(key));
    }

    public double getDouble(String key) {
        return getDouble(Arrays.asList(nodeNames).indexOf(key));
    }

    public long getLong(String key) {
        return getLong(Arrays.asList(nodeNames).indexOf(key));
    }


    public String getBooleanFormatted(String key) {
        return Format.formatBoolean(getBoolean(key));
    }

    public String get(String key) {
        if (key.substring(key.length() - 4).equals("_mhz")) {
            return Format.formatUnitValue(getString(key), Format.HERTZ);
        } else if (key.substring(key.length() - 3).equals("_mb")) {
            return Format.formatUnitValue(getString(key), Format.BYTES);
        } else if (key.substring(key.length() - 2).equals("_w")) {
            return Format.formatUnitValue(getString(key), Format.WATTS);
        }
        return getString(key);
    }

    public String getDef(String key) {
        if (key.substring(key.length() - 4).equals("_mhz")) {
            return Format.formatUnitValueDefault(getString(key), Format.HERTZ);
        } else if (key.substring(key.length() - 3).equals("_mb")) {
            return Format.formatUnitValueDefault(getString(key), Format.BYTES);
        } else if (key.substring(key.length() - 2).equals("_w")) {
            return Format.formatUnitValueDefault(getString(key), Format.WATTS);
        }
        return getString(key);
    }

    public boolean exists(String key) {
        return Arrays.asList(nodeNames).contains(key);
    }

    public int getSize() {
        doCheck();
        return properties.length;
    }

    public String getNodeName(int index) {
        doCheck();
        return nodeNames[index];
    }

    public String[] getKeys() {
        return nodeNames;
    }

    public Object[] getValues() {
        return properties;
    }
}
