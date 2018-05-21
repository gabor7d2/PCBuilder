package net.gabor6505.java.pcbuilder.types;

import net.gabor6505.java.pcbuilder.utils.TypeNotPresentException;
import net.gabor6505.java.pcbuilder.xml.NodeList;
import net.gabor6505.java.pcbuilder.xml.XmlContract;
import net.gabor6505.java.pcbuilder.xml.XmlParser;

import java.util.ArrayList;
import java.util.List;

public class Brand {

    public final static XmlContract CONTRACT = new XmlContract(XmlContract.Folder.TYPES, "brands.xml");

    private final static List<Brand> brands = new ArrayList<>(0);

    static {
        NodeList root = XmlParser.parseXml(CONTRACT);

        for (String brand : root.getAttributesContent("brand", "name")) {
            brands.add(new Brand(brand));
        }
    }

    private final String name;

    private Brand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Brand parsedNode(String nodeName, String nodeContent, List<String> nodeAttributes, List<String> nodeAttributesContent) {
        return new Brand(nodeAttributesContent.get(0));
    }

    public static List<Brand> getBrands() {
        return brands;
    }

    public static Brand getBrand(String brandName) {
        for (Brand brand : brands) {
            if (brand.getName().equals(brandName)) return brand;
        }
        new TypeNotPresentException("Brand \"" + brandName + "\" is not registered in " + CONTRACT.getFileName()).printStackTrace();
        return null;
    }

    public static Brand getBrand(NodeList componentInfoNode) {
        return getBrand(componentInfoNode.getNodeContent("brand"));
    }
}
