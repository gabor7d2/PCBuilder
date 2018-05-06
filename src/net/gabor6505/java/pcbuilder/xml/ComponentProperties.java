package net.gabor6505.java.pcbuilder.xml;

public class ComponentProperties {

    private String[] nodeNames;
    private Object[] properties;

    public ComponentProperties(Object[] objectArray, String[] correspondingNodeNames) {
        properties = objectArray;
        nodeNames = correspondingNodeNames;
    }

    private void doCheck() {
        if (properties.length != nodeNames.length) throw new IllegalStateException();
    }

    public Object get(int index) {
        doCheck();
        return properties[index];
    }

    public String getString(int index) {
        doCheck();
        if (properties[index] == null) return null;
        return properties[index].toString();
    }

    public boolean getBoolean(int index) {
        doCheck();
        return Boolean.parseBoolean(properties[index].toString());
    }

    public int getInt(int index) {
        doCheck();
        if (properties[index] == null) return 0;
        return Integer.parseInt(properties[index].toString());
    }

    public short getShort(int index) {
        doCheck();
        if (properties[index] == null) return 0;
        return Short.parseShort(properties[index].toString());
    }

    public double getDouble(int index) {
        doCheck();
        if (properties[index] == null) return 0;
        return Double.parseDouble(properties[index].toString());
    }

    public long getLong(int index) {
        doCheck();
        if (properties[index] == null) return 0;
        return Long.parseLong(properties[index].toString());
    }

    public int getSize() {
        doCheck();
        return properties.length;
    }

    public String getNodeName(int index) {
        doCheck();
        return nodeNames[index];
    }
}
