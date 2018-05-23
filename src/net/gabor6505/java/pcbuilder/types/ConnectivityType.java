package net.gabor6505.java.pcbuilder.types;

import net.gabor6505.java.pcbuilder.utils.TypeNotPresentException;
import net.gabor6505.java.pcbuilder.xml.*;

import java.util.ArrayList;
import java.util.List;

// TODO Fix getConnectivityType sometimes returning null with no reason
public class ConnectivityType implements ReloadListener {

    public final static XmlContract CONTRACT = new XmlContract(XmlContract.Folder.TYPES, "connectivity_types.xml");

    private final static List<ConnectivityType> connectivityTypes = new ArrayList<>(0);

    static {
        TypeManager.addReloadListener(ConnectivityType.class.getName(), new ConnectivityType(null, null, null));
        load();
    }

    private final String category;
    private final String typeName;
    private final String location;

    private ConnectivityType(String category, String typeName, String location) {
        this.category = category;
        this.typeName = typeName;
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public String getTypeName() {
        return typeName;
    }

    public static List<ConnectivityType> getConnectivityTypes() {
        return connectivityTypes;
    }

    public static ConnectivityType getConnectivityType(String category, String typeName, String location) {
        //System.out.println(connectivityTypes.size());
        for (ConnectivityType type : connectivityTypes) {
            if (type.getCategory().equals(category)
                    && type.getLocation().equals(location)
                    && type.getTypeName().equals(typeName)) return type;
        }
        //new TypeNotPresentException("Connectivity Type [" + category + ", " + typeName + ", " + location + "] is not registered in " + CONTRACT.getFileName()).printStackTrace();
        return null;
    }

    public static ConnectivityType getConnectivityType(ComponentProperties properties) {
        return getConnectivityType(properties.getString(0), properties.getString(1), properties.getString(2));
    }

    public static ConnectivityType getConnectivityType(Node connectivityNode) {
        return getConnectivityType(connectivityNode.getNodesContent(Connectivity.NODE_NAMES));
    }

    private static void load() {
        NodeList root = XmlParser.parseXml(CONTRACT);

        for (Node category : root.getNodes("category")) {
            String name = category.getNodeAttributeContent("name");
            String location = category.getNodeAttributeContent("location");

            for (String type : category.getNodesContent("type")) {
                connectivityTypes.add(new ConnectivityType(name, location, type));
            }
        }
    }

    @Override
    public void reload() {
        connectivityTypes.clear();
        load();
    }
}
