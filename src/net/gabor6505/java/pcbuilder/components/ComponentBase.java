package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.types.Brand;
import net.gabor6505.java.pcbuilder.xml.NodeList;
import net.gabor6505.java.pcbuilder.xml.XmlContract;

import static net.gabor6505.java.pcbuilder.utils.General.replaceSpaces;

public class ComponentBase {

    private final Brand brand;
    private final String modelNumber;
    private final String imagePath;

    public ComponentBase(Brand brand, String modelNumber, String imagePath) {
        this.brand = brand;
        this.modelNumber = modelNumber;
        this.imagePath = imagePath;
    }

    public ComponentBase(NodeList componentInfoNode, XmlContract contract) {
        brand = Brand.getBrand(componentInfoNode);
        modelNumber = componentInfoNode.getNodeContent("model_number");

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

        String imgPath = XmlContract.Folder.IMAGES.getValue() + contract.getTrimmedFileName() + "/";

        if (!imagePathOverride.equals("")) {
            imgPath += imagePathOverride;
        } else {
            imgPath += replaceSpaces(brand.getName()) + "_" + replaceSpaces(modelNumber) + imagePathExtension;
        }

        imagePath = imgPath;
        System.out.println(imagePath);
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

    public String getImagePath() {
        return imagePath;
    }
}
