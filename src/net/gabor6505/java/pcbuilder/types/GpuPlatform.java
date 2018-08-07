package net.gabor6505.java.pcbuilder.types;

import net.gabor6505.java.pcbuilder.xml.*;

import java.util.ArrayList;
import java.util.List;

public class GpuPlatform implements TypeManager.ReloadListener {

    public final static XmlContract CONTRACT = new XmlContract(XmlContract.Folder.TYPES, "gpu_types.xml");
    public final static String[] NODE_NAMES = new String[]{"brand", "name"};

    private final static List<GpuPlatform> gpuPlatforms = new ArrayList<>(0);

    static {
        TypeManager.addReloadListener(GpuPlatform.class.getName(), new GpuPlatform(null, null, null), 1);
        load();
    }

    private final Brand brand;
    private final String name;
    private final String prefix;

    private GpuPlatform(Brand brand, String name, String prefix) {
        this.brand = brand;
        this.name = name;
        this.prefix = prefix;
    }

    public Brand getBrand() {
        return brand;
    }

    public String getBrandName() {
        return brand.getName();
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getFormattedName() {
        return prefix + " " + name;
    }

    public static List<GpuPlatform> getGpuPlatforms() {
        return gpuPlatforms;
    }

    public static GpuPlatform getGpuPlatform(String brandName, String name) {
        for (GpuPlatform type : gpuPlatforms) {
            if (type.getBrandName().equals(brandName) && type.getName().equals(name)) {
                return type;
            }
        }
        new TypeNotPresentException("Gpu Platform", CONTRACT, brandName, name).printStackTrace();
        return null;
    }

    public static GpuPlatform getGpuPlatform(Brand brand, String name) {
        return getGpuPlatform(brand.getName(), name);
    }

    public static GpuPlatform getGpuPlatform(ComponentProperties properties) {
        return getGpuPlatform(properties.getString(0), properties.getString(1));
    }

    public static GpuPlatform getGpuPlatform(Node gpuNode) {
        return getGpuPlatform(gpuNode.getNodesContent(NODE_NAMES));
    }

    private static void load() {
        NodeList root = XmlParser.parseXml(CONTRACT);

        for (Node type : root.getNodes("type")) {
            String brandName = type.getNodeAttributeContent("brand");
            String prefix = type.getNodeAttributeContent("prefix");
            Brand brand = Brand.getBrand(brandName);

            if (brand != null) {
                for (String name : type.getNodesContent("name")) {
                    gpuPlatforms.add(new GpuPlatform(brand, name, prefix));
                }
            }
        }
    }

    @Override
    public void reload() {
        gpuPlatforms.clear();
        load();
    }
}
