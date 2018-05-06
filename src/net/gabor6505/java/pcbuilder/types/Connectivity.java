package net.gabor6505.java.pcbuilder.types;

import net.gabor6505.java.pcbuilder.xml.ComponentProperties;
import net.gabor6505.java.pcbuilder.xml.Node;

public class Connectivity {

    public final static String[] NODE_NAMES = new String[]{"category", "type", "location", "version", "extra", "count"};

    private final ConnectivityType type;
    private final String version;
    private final String extra;
    private final short count;

    public Connectivity(ConnectivityType type, String version, String extra, short count) {
        this.type = type;
        this.version = version;
        this.extra = extra;
        this.count = count;
    }

    public Connectivity(ConnectivityType type, ComponentProperties properties) {
        this.type = type;
        version = properties.getString(3);
        extra = properties.getString(4);
        count = properties.getShort(5);
    }

    public Connectivity(Node connectivityNode) {
        this(ConnectivityType.getConnectivityType(connectivityNode), connectivityNode.getNodesContent(NODE_NAMES));
    }

    public ConnectivityType getType() {
        return type;
    }

    public String getCategory() {
        return type.getCategory();
    }

    public String getTypeName() {
        return type.getTypeName();
    }

    public String getLocation() {
        return type.getLocation();
    }

    public String getVersion() {
        return version;
    }

    public String getExtra() {
        return extra;
    }

    public short getCount() {
        return count;
    }
}
