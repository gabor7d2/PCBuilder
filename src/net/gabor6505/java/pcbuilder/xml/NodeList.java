package net.gabor6505.java.pcbuilder.xml;

import java.util.ArrayList;
import java.util.List;

public class NodeList extends ArrayList<Node> {

    public NodeList(org.w3c.dom.NodeList nodeList) {
        super(0);
        for (int i = 0; i < nodeList.getLength(); i++) {
            add(new Node(nodeList.item(i)));
        }
    }

    public NodeList() {
        super(0);
    }

    public Node getNode(String nodeName) {
        for (Node node : this) {
            if (node.getNodeName().equals(nodeName)) return node;
        }
        return new Node();
    }

    public NodeList getNodes(String nodeName) {
        NodeList list = new NodeList();

        for (Node node : this) {
            if (node.getNodeName().equals(nodeName)) list.add(node);
        }

        return list;
    }

    public NodeList getNodes() {
        NodeList list = new NodeList();
        list.addAll(this);
        return list;
    }

    public String getNodeContent(String nodeName) {
        Node node = getNode(nodeName);
        if (node == null) return null;
        else return node.getTextContent();
    }

    public List<String> getNodesContent(String nodeName) {
        List<String> returnList = new ArrayList<>(0);

        for (Node node : getNodes(nodeName)) {
            returnList.add(node.getTextContent());
        }

        return returnList;
    }

    public List<String> getNodesContent() {
        List<String> returnList = new ArrayList<>(0);

        for (Node node : getNodes()) {
            returnList.add(node.getTextContent());
        }

        return returnList;
    }

    public Node getAttribute(String attrName) {
        for (Node node : this) {
            if (node.getNodeAttribute(attrName).getNodeName().equals(attrName)) {
                return node.getNodeAttribute(attrName);
            }
        }
        return new Node();
    }

    public Node getAttribute(String nodeName, String attrName) {
        for (Node node : this) {
            if (node.getNodeName().equals(nodeName)) {
                if (node.getNodeAttribute(attrName).getNodeName().equals(attrName)) {
                    return node.getNodeAttribute(attrName);
                }
            }
        }
        return new Node();
    }

    public NodeList getAttributes(String attrName) {
        NodeList list = new NodeList();

        for (Node node : this) {
            if (node.getNodeAttribute(attrName).getNodeName().equals(attrName)) {
                list.add(node.getNodeAttribute(attrName));
            }
        }

        return list;
    }

    public NodeList getAttributes(String nodeName, String attrName) {
        NodeList list = new NodeList();

        for (Node node : this) {
            if (node.getNodeName().equals(nodeName)) {
                if (node.getNodeAttribute(attrName).getNodeName().equals(attrName)) {
                    list.add(node.getNodeAttribute(attrName));
                }
            }
        }

        return list;
    }

    public String getAttributeContent(String attrName) {
        for (Node node : this) {
            if (node.getNodeAttribute(attrName).getNodeName().equals(attrName)) {
                return node.getNodeAttribute(attrName).getTextContent();
            }
        }
        return null;
    }

    public String getAttributeContent(String nodeName, String attrName) {
        for (Node node : this) {
            if (node.getNodeName().equals(nodeName)) {
                if (node.getNodeAttribute(attrName).getNodeName().equals(attrName)) {
                    return node.getNodeAttribute(attrName).getTextContent();
                }
            }
        }
        return null;
    }

    public List<String> getAttributesContent(String attrName) {
        List<String> list = new ArrayList<>(0);

        for (Node node : this) {
            if (node.getNodeAttribute(attrName).getNodeName().equals(attrName)) {
                list.add(node.getNodeAttribute(attrName).getTextContent());
            }
        }

        return list;
    }

    public List<String> getAttributesContent(String nodeName, String attrName) {
        List<String> list = new ArrayList<>(0);

        for (Node node : this) {
            if (node.getNodeName().equals(nodeName)) {
                if (node.getNodeAttribute(attrName).getNodeName().equals(attrName)) {
                    list.add(node.getNodeAttribute(attrName).getTextContent());
                }
            }
        }

        return list;
    }

    public ComponentProperties getNodesContent(String[] nodeNames) {
        Object[] objects = new Object[nodeNames.length];
        for (int i = 0; i < nodeNames.length; i++) {
            objects[i] = getNodeContent(nodeNames[i]);
        }
        return new ComponentProperties(objects, nodeNames);
    }
}
