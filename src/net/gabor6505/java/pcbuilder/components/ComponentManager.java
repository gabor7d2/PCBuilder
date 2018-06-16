package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.utils.Utils;
import net.gabor6505.java.pcbuilder.xml.*;
import sun.awt.PeerEvent;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ComponentManager {

    private final static List<StateChangeListener> listeners = new ArrayList<>();
    private final static Map<String, List<Component>> components = new HashMap<>();

    public static Map<String, List<Component>> getComponentMap() {
        return components;
    }

    public static List<Component> getComponents(String type) {
        return components.get(type);
    }

    private static void load(String type, String displayName, String[] nodeNames, IXmlComponentBuilder dataHandler, String className, boolean enabled, boolean reload) {
        EventQueue.invokeLater(() -> {
            String[] nodeNames2 = nodeNames;
            IXmlComponentBuilder dataHandler2 = dataHandler;

            String fileName = type.toLowerCase() + "s.xml";
            components.put(type, new ArrayList<>());

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
                dataHandler2 = (currentNode, properties, list) -> {
                    list.add(new Component(currentNode.getComponentInfo(), properties, fileName));
                };
            }

            XmlContract contract = new XmlContract(XmlContract.Folder.COMPONENTS, fileName, firstLetterUppercase(type), nodeNames2, dataHandler2);
            XmlParser.parseXmlComponents(contract, components.get(type));
            if (!reload) {
                for (StateChangeListener l : listeners) {
                    l.loaded(type, displayName, enabled, components.get(type));
                }
            } else {
                for (StateChangeListener l : listeners) {
                    l.reloaded(type, displayName, enabled, components.get(type));
                }
            }
        });
    }

    private static void load(String type, String displayName, String[] nodeNames, IXmlComponentBuilder dataHandler, String className, boolean reload) {
        load(type, displayName, nodeNames, dataHandler, className, true, reload);
    }

    public static void autoLoad(JDialog progressDialog, boolean reload) {
        EventQueue.invokeLater(() -> {
            NodeList types = XmlParser.parseXml(XmlContract.Folder.COMPONENTS, "registered_categories.xml").getNodes("Component");
            for (Node type : types) {
                String[] nodeNames = type.getNodesContent("NodeName").toArray(new String[0]);

                String typeName = type.getNodeAttributeContent("name");
                String displayName = type.getNodeAttributeContent("displayName");

                if (displayName == null) displayName = typeName;
                String className = type.getNodeAttributeContent("className");

                String enabledAtStart = type.getNodeAttributeContent("enabledAtStart");
                boolean enabled = true;
                if (enabledAtStart != null) if (enabledAtStart.equals("false")) enabled = false;

                load(typeName, displayName, nodeNames, null, className, enabled, reload);
            }
            if (progressDialog != null) progressDialog.setVisible(false);
        });
    }

    public static void autoLoad(JDialog progressDialog) {
        autoLoad(progressDialog, false);
    }

    public static void autoLoad() {
        autoLoad(null);
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

    public static void reload() {
        components.clear();
        autoLoad(null, true);
    }

    public static void remove(String type) {
        Utils.postEvent(PeerEvent.LOW_PRIORITY_EVENT, () -> {
            components.remove(type);
            for (StateChangeListener l : listeners) {
                l.removed(type);
            }
        });
    }

    public static void clear() {
        Utils.postEvent(PeerEvent.LOW_PRIORITY_EVENT, () -> {
            for (String type : components.keySet()) {
                remove(type);
            }
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
