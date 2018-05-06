package net.gabor6505.java.pcbuilder.xml;

import com.sun.istack.internal.Nullable;
import net.gabor6505.java.pcbuilder.types.Brand;

public class Node extends NodeList {

    private final org.w3c.dom.Node node;

    public Node(@Nullable org.w3c.dom.Node node) {
        super(node.getChildNodes());
        this.node = node;
    }

    public Node() {
        super();
        this.node = null;
    }

    public org.w3c.dom.Node getNode() {
        return node;
    }

    public String getNodeName() {
        return node.getNodeName();
    }

    public String getTextContent() {
        if (node == null) return null;
        else return node.getTextContent();
    }

    public Node getNodeAttribute(String attrName) {
        return new Node(node.getAttributes().getNamedItem(attrName));
    }

    public String getNodeAttributeContent(String attrName) {
        return node.getAttributes().getNamedItem(attrName).getTextContent();
    }

    public Node getComponentInfo() {
        return this.getNode("ComponentInfo");
    }

    public String getBrandName() {
        return getComponentInfo().getNodeContent("brand");
    }

    public Brand getBrand() {
        return Brand.getBrand(getComponentInfo());
    }

    public String getModelNumber() {
        return getComponentInfo().getNodeContent("model_number");
    }
}
