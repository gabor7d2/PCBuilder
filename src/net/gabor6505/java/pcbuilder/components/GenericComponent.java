package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.elements.ComparisonPane;
import net.gabor6505.java.pcbuilder.elements.ComponentCategory;
import net.gabor6505.java.pcbuilder.types.Brand;
import net.gabor6505.java.pcbuilder.xml.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericComponent {

    private static ComparisonPane pane;

    private final static Map<String, List<Component>> components = new HashMap<>();

    private final static Map<String, String[]> nodeNames = new HashMap<>();
    private final static Map<String, IXmlComponentBuilder> dataHandlers = new HashMap<>();

    public static void setPane(ComparisonPane pane) {
        GenericComponent.pane = pane;
    }

    public static Map<String, List<Component>> getComponentMap() {
        return components;
    }

    public static List<Component> getComponents(String type) {
        if (!components.containsKey(type)) {
            load(type);
        }
        return components.get(type);
    }

    public static void removeComponents(String type) {
        components.remove(type);
    }

    public static void clear() {
        components.clear();
    }

    public static void load(String type, String[] nodeNames, IXmlComponentBuilder dataHandler) {
        String fileName = type.toLowerCase() + "s.xml";
        components.put(type, new ArrayList<>());

        try {
            Class compClass = Class.forName(GenericComponent.class.getPackage().getName() + "." + firstLetterUppercase(type));
            try {
                nodeNames = (String[]) compClass.getField("NODE_NAMES").get(null);
            } catch (Exception ignored) { }
            dataHandler = (IXmlComponentBuilder) compClass.getField("DATA_HANDLER").get(null);
        } catch (Exception ignored) {
        }

        if (dataHandler == null) {
            dataHandler = (currentNode, properties, list) -> list.add(new Component(currentNode.getComponentInfo(), properties, fileName));
        }

        dataHandlers.put(type, dataHandler);
        GenericComponent.nodeNames.put(type, nodeNames);

        XmlContract contract = new XmlContract(XmlContract.Folder.COMPONENTS, fileName, firstLetterUppercase(type), nodeNames, dataHandler);
        XmlParser.parseXmlComponents(contract, components.get(type));
    }

    public static void load(String type, String[] nodeNames) {
        load(type, nodeNames, null);
    }

    public static void load(String type) {
        load(type, null, null);
    }

    public static List<Component> reload(String type) {
        if (components.containsKey(type)) {
            load(type, nodeNames.get(type), dataHandlers.get(type));
        } else load(type, null, null);
        return getComponents(type);
    }

    public static void reload() {
        if (pane != null) pane.removeAllRows();
        for (String type : components.keySet()) {
            load(type, nodeNames.get(type), dataHandlers.get(type));
        }
    }

    private static String firstLetterUppercase(String input) {
        input = input.toLowerCase();
        if (input.length() == 0) return "";
        else return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
