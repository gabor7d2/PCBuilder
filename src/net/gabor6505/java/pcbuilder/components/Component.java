package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.types.Brand;
import net.gabor6505.java.pcbuilder.xml.ComponentProperties;
import net.gabor6505.java.pcbuilder.xml.NodeList;
import net.gabor6505.java.pcbuilder.xml.XmlContract;

import java.util.ArrayList;
import java.util.List;

import static net.gabor6505.java.pcbuilder.utils.Utils.replaceSpaces;

public class Component extends ComponentProperties {

    private final Brand brand;
    private final String modelNumber;
    private final String productSite;
    private final String imagePath;

    public Component(Brand brand, String modelNumber, String productSite, String imagePath, ComponentProperties properties) {
        super(properties.getValues(), properties.getKeys());
        this.brand = brand;
        this.modelNumber = modelNumber;
        this.productSite = productSite;
        this.imagePath = imagePath;
    }

    public Component(NodeList componentInfoNode, ComponentProperties properties, XmlContract contract) {
        super(properties.getValues(), properties.getKeys());
        brand = Brand.getBrand(componentInfoNode);
        modelNumber = componentInfoNode.getNodeContent("model_number");
        productSite = componentInfoNode.getNodeContent("product_site");

        String imagePathOverride = componentInfoNode.getNodeContent("image_path_override");
        if (imagePathOverride == null) {
            imagePathOverride = "";
        } else {
            imagePathOverride = replaceSpaces(imagePathOverride);
        }

        String imagePathExtension = componentInfoNode.getNodeContent("image_path_extension");
        if (imagePathExtension == null) {
            imagePathExtension = "";
        } else {
            imagePathExtension = "_" + replaceSpaces(imagePathExtension);
        }

        if (contract == null) {
            imagePath = null;
            return;
        }

        String imgPath = XmlContract.Folder.COMPONENT_IMAGES.getValue() + contract.getTrimmedFileName() + "/";

        if (!imagePathOverride.equals("")) {
            imgPath += imagePathOverride;
        } else {
            imgPath += replaceSpaces(brand.getName()) + "_" + replaceSpaces(modelNumber) + imagePathExtension;
        }

        imgPath += ".png";
        imagePath = imgPath;
    }

    public Brand getBrand() {
        return brand;
    }

    public String getBrandName() {
        return brand.getName();
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public String getProductSite() {
        return productSite;
    }

    public String getImagePath() {
        return imagePath;
    }

    public List<String> getExtraInfo() {
        return new ArrayList<>();
    }
}
