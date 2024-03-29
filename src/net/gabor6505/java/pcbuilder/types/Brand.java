package net.gabor6505.java.pcbuilder.types;

import net.gabor6505.java.pcbuilder.xml.NodeList;
import net.gabor6505.java.pcbuilder.xml.XmlContract;
import net.gabor6505.java.pcbuilder.xml.XmlParser;

import java.util.ArrayList;
import java.util.List;

public class Brand implements TypeManager.ReloadListener {

    public final static XmlContract CONTRACT = new XmlContract(XmlContract.Folder.TYPES, "brands.xml");

    private final static List<Brand> brands = new ArrayList<>(0);

    static {
        TypeManager.addReloadListener(Brand.class.getName(), new Brand(null), 0);
        load();
    }

    private final String name;

    private Brand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<Brand> getBrands() {
        return brands;
    }

    public static Brand getBrand(String brandName) {
        for (Brand brand : brands) {
            if (brand.getName().equals(brandName)) return brand;
        }
        new TypeNotPresentException("Brand", CONTRACT, brandName).printStackTrace();
        return null;
    }

    public static Brand getBrand(NodeList componentInfoNode) {
        return getBrand(componentInfoNode.getNodeContent("brand"));
    }

    private static void load() {
        NodeList root = XmlParser.parseXml(CONTRACT);

        for (String brand : root.getAttributesContent("brand", "name")) {
            brands.add(new Brand(brand));
        }
    }

    @Override
    public void reload() {
        brands.clear();
        load();
    }
}
