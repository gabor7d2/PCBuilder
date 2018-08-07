package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.xml.ComponentProperties;
import net.gabor6505.java.pcbuilder.xml.IXmlComponentBuilder;
import net.gabor6505.java.pcbuilder.xml.NodeList;

import java.util.ArrayList;
import java.util.List;

public class Cooler extends Component {

    public final static IXmlComponentBuilder DATA_HANDLER = (cooler, properties, list) -> list.add(new Cooler(cooler.getComponentInfo(), properties));

    public Cooler(NodeList componentInfoNode, ComponentProperties properties) {
        super(componentInfoNode, properties, "coolers.xml");
    }

    @Override
    public List<String> getExtraInfo() {
        List<String> list = new ArrayList<>();
        addExtraInfo(list, "Cooler height:", "cooler_height_mm", "mm");
        return list;
    }
}