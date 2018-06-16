package net.gabor6505.java.pcbuilder.types;

import net.gabor6505.java.pcbuilder.xml.*;

import java.util.ArrayList;
import java.util.List;

public class RamPlatform implements ReloadListener {

    public final static XmlContract CONTRACT = new XmlContract(XmlContract.Folder.TYPES, "ram_types.xml");
    public final static String[] NODE_NAMES = new String[]{"type", "frequency"};

    private final static List<RamPlatform> ramPlatforms = new ArrayList<>(0);

    static {
        TypeManager.addReloadListener(RamPlatform.class.getName(), new RamPlatform(null, 0), 0);
        load();
    }

    private final String typeName;
    private final int frequencyMHz;

    private RamPlatform(String typeName, int frequencyMHz) {
        this.typeName = typeName;
        this.frequencyMHz = frequencyMHz;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getFrequencyMHz() {
        return frequencyMHz;
    }

    public static RamPlatform getRamPlatform(String typeName, int frequencyMHz) {
        for (RamPlatform type : ramPlatforms) {
            if (type.getTypeName().equals(typeName) && type.getFrequencyMHz() == frequencyMHz) {
                return type;
            }
        }
        new TypeNotPresentException("Ram Platform", CONTRACT, typeName, String.valueOf(frequencyMHz)).printStackTrace();
        return null;
    }

    public static RamPlatform getRamPlatform(ComponentProperties properties) {
        return getRamPlatform(properties.getString(0), properties.getInt(1));
    }

    public static RamPlatform getRamPlatform(Node ramNode) {
        return getRamPlatform(ramNode.getNodesContent(NODE_NAMES));
    }

    private static void load() {
        NodeList root = XmlParser.parseXml(CONTRACT);

        for (Node type : root.getNodes("type")) {
            String name = type.getNodeAttributeContent("name");

            for (String frequency : type.getNodesContent("frequency")) {
                ramPlatforms.add(new RamPlatform(name, Integer.parseInt(frequency)));
            }
        }
    }

    @Override
    public void reload() {
        ramPlatforms.clear();
        load();
    }
}
