package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.types.Brand;
import net.gabor6505.java.pcbuilder.utils.Format;
import net.gabor6505.java.pcbuilder.utils.Utils;
import net.gabor6505.java.pcbuilder.xml.ComponentProperties;
import net.gabor6505.java.pcbuilder.xml.NodeList;
import net.gabor6505.java.pcbuilder.xml.XmlContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.gabor6505.java.pcbuilder.utils.Utils.replaceSpaces;

// TODO support for more than one shop
public class Component extends ComponentProperties {

    private final Brand brand;
    private final String modelNumber;
    private final String imagePath;

    private final String productSite;
    private final String priceSite;
    private final String shopSite;
    private final String price;
    //private final Map<String, String> shopSites = new HashMap<>();

    /**
     * Creates a new Component
     */
    public Component(Brand brand, String modelNumber, String productSite, String priceSite, String shopSite, String price, String imagePath, ComponentProperties properties) {
        super(properties.getValues(), properties.getKeys());
        this.brand = brand;
        this.modelNumber = modelNumber;
        this.productSite = productSite;
        this.priceSite = priceSite;
        this.shopSite = shopSite;
        this.imagePath = imagePath;
        this.price = price;
    }

    /**
     * Creates a new Component, constructs a file path if filePath is null,
     * and constructs the imagePath of the component
     */
    public Component(NodeList componentInfoNode, ComponentProperties properties, String filePath) {
        super(properties.getValues(), properties.getKeys());
        brand = Brand.getBrand(componentInfoNode);
        modelNumber = componentInfoNode.getNodeContent("model_number");
        productSite = componentInfoNode.getNodeContent("product_site");
        priceSite = componentInfoNode.getNodeContent("price_site");
        shopSite = componentInfoNode.getNodeContent("shop_site");
        price = componentInfoNode.getNodeContent("price");

        if (filePath == null) {
            String categoryNodeName = componentInfoNode.get(0).getNode().getParentNode().getParentNode().getNodeName();
            filePath = categoryNodeName.toLowerCase() + "s.xml";
        }

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

        String imgPath = XmlContract.Folder.COMPONENT_IMAGES.getValue() + Utils.removeExtension(filePath) + "/";

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

    public String getPriceSite() {
        return priceSite;
    }

    public String getShopSite() {
        return shopSite;
    }

    public String getPrice() {
        return price;
    }

    public String getFormattedPrice() {
        return Format.formatCurrency(price);
    }

    public String getImagePath() {
        return imagePath;
    }

    /**
     * Gets the list of extra info Strings that are going to be
     * displayed below the component's brand and model number
     * <br><br>
     * Override this method to specify what extra information
     * should be displayed
     *
     * @return The list of lines the UI should display
     */
    public List<String> getExtraInfo() {
        return new ArrayList<>();
    }

    /**
     * Adds an extra info line to the specified list, after
     * checking if the specified key is not null and not empty
     * <br><br>
     * Inserting spaces is handled automatically.
     * <br><br>
     * Inserts the specified values before and after the property's value
     *
     * @param list        The list to add the value to
     * @param valueBefore The value to add before
     * @param key         The name of the property
     * @param valueAfter  The value to add after
     */
    public void addExtraInfo(List<String> list, String valueBefore, String key, String valueAfter) {
        if (list == null || key == null || valueBefore == null || valueAfter == null) return;
        if (checkValidity(key))
            list.add(valueBefore + (valueBefore.isEmpty() ? "" : " ") + get(key) + (valueAfter.isEmpty() ? "" : " ") + valueAfter);
    }

    /**
     * Adds an extra info to the specified list, after checking if
     * the specified key is not null and not empty
     * <br><br>
     * Inserting spaces is handled automatically.
     *
     * @param list The list to add the value to
     * @param key  The name of the property
     */
    public void addExtraInfo(List<String> list, String key) {
        addExtraInfo(list, "", key, "");
    }

    /**
     * Adds an extra info line to the specified list, after
     * checking if the specified key is not null and not empty
     * <br><br>
     * Inserting spaces is handled automatically.
     * <br><br>
     * Inserts the specified separator between the 2 properties' values
     *
     * @param list      The list to add the value to
     * @param key1      The name of the first property
     * @param separator The separator that should be between the first and second property
     * @param key2      The name of the second property
     */
    public void addExtraInfos(List<String> list, String key1, String separator, String key2) {
        if (list == null || key1 == null || key2 == null || separator == null) return;
        if (checkValidity(key1) && checkValidity(key2))
            list.add(get(key1) + " " + separator + (separator.isEmpty() ? "" : " ") + get(key2));
    }

    /**
     * Adds an extra info line to the specified list, after
     * checking if the specified key is not null and not empty
     * <br><br>
     * Inserting spaces is handled automatically.
     *
     * @param list The list to add the value to
     * @param key1 The name of the first property
     * @param key2 The name of the second property
     */
    public void addExtraInfos(List<String> list, String key1, String key2) {
        addExtraInfos(list, key1, "", key2);
    }
}
