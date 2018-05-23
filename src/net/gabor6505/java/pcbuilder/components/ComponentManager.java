package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.xml.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ComponentManager {

    private final static List<StateChangeListener> listeners = new ArrayList<>();

    private final static Map<String, List<Component>> components = new HashMap<>();
    private final static Map<String, String> displayNames = new HashMap<>();

    private final static Map<String, String[]> nodeNames = new HashMap<>();
    private final static Map<String, IXmlComponentBuilder> dataHandlers = new HashMap<>();

    public static Map<String, List<Component>> getComponentMap() {
        return components;
    }

    public static List<Component> getComponents(String type) {
        return components.get(type);
    }

    private static void load(String type, String displayName, String[] nodeNames, IXmlComponentBuilder dataHandler, String className, boolean reload) {
        EventQueue.invokeLater(() -> {
            String[] nodeNames2 = nodeNames;
            IXmlComponentBuilder dataHandler2 = dataHandler;

            String fileName = type.toLowerCase() + "s.xml";
            components.put(type, new ArrayList<>());
            displayNames.put(type, displayName);

            try {
                Class compClass = Class.forName(ComponentManager.class.getPackage().getName() + "." + (className == null ? firstLetterUppercase(type) : className));
                try {
                    nodeNames2 = (String[]) compClass.getField("NODE_NAMES").get(null);
                } catch (Exception ignored) {
                }
                dataHandler2 = (IXmlComponentBuilder) compClass.getField("DATA_HANDLER").get(null);
            } catch (Exception ignored) {
            }

            if (dataHandler2 == null) {
                dataHandler2 = (currentNode, properties, list) -> list.add(new Component(currentNode.getComponentInfo(), properties, fileName));
            }

            dataHandlers.put(type, dataHandler2);
            ComponentManager.nodeNames.put(type, nodeNames2);

            XmlContract contract = new XmlContract(XmlContract.Folder.COMPONENTS, fileName, firstLetterUppercase(type), nodeNames2, dataHandler2);
            XmlParser.parseXmlComponents(contract, components.get(type));
            if (!reload) {
                for (StateChangeListener l : listeners) {
                    l.loaded(type, displayName, components.get(type));
                }
            } else {
                for (StateChangeListener l : listeners) {
                    l.reloaded(type, displayName, components.get(type));
                }
            }
        });
    }

    public static void autoLoad() {
        EventQueue.invokeLater(() -> {
            NodeList types = XmlParser.parseXml(XmlContract.Folder.COMPONENTS, "registered_categories.xml").getNodes("Component");
            for (Node type : types) {
                String[] nodeNames = type.getNodesContent("NodeName").toArray(new String[0]);

                String typeName = type.getNodeAttributeContent("name");
                String displayName = type.getNodeAttributeContent("displayName");

                if (displayName == null) displayName = typeName;
                String className = type.getNodeAttributeContent("className");

                load(typeName, displayName, nodeNames, null, className, false);
            }
        });
    }

    private static void load(String type, String displayName, String[] nodeNames, IXmlComponentBuilder dataHandler, boolean reload) {
        load(type, displayName, nodeNames, dataHandler, null, reload);
    }

    public static void load(String type, String displayName, String[] nodeNames, IXmlComponentBuilder dataHandler) {
        load(type, displayName, nodeNames, dataHandler, false);
    }

    public static void load(String type, String displayName, String[] nodeNames) {
        load(type, displayName, nodeNames, null);
    }

    public static void load(String type, String displayName) {
        EventQueue.invokeLater(() -> {
            NodeList types = XmlParser.parseXml(XmlContract.Folder.COMPONENTS, "registered_categories.xml").getNodes("Component");
            for (Node node : types) {
                String typeName = node.getNodeAttributeContent("name");
                if (typeName.equals(type)) {
                    String[] nodeNames = node.getNodesContent("NodeName").toArray(new String[0]);
                    load(typeName, displayName, nodeNames);
                    return;
                }
            }
            load(type, displayName, null);
        });
    }

    public static void load(String type) {
        load(type, type);
    }

    public static void reload(String type) {
        EventQueue.invokeLater(() -> {
            if (components.containsKey(type)) {
                load(type, displayNames.get(type), nodeNames.get(type), dataHandlers.get(type), true);
            }
        });
    }

    public static void reload() {
        for (String type : components.keySet()) {
            reload(type);
        }
    }

    public static void remove(String type) {
        EventQueue.invokeLater(() -> {
            components.remove(type);
            for (StateChangeListener l : listeners) {
                l.removed(type);
            }
        });
    }

    public static void clear() {
        EventQueue.invokeLater(() -> {
            for (String type : components.keySet()) {
                remove(type);
            }
            nodeNames.clear();
            dataHandlers.clear();
        });
    }

    public static void addStateChangeListener(StateChangeListener l) {
        listeners.add(l);
    }

    public static void removeStateChangeListener(StateChangeListener l) {
        listeners.remove(l);
    }

    private static String firstLetterUppercase(String input) {
        input = input.toLowerCase();
        if (input.length() == 0) return "";
        else return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
