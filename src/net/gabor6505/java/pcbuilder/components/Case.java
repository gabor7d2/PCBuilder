package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.xml.ComponentProperties;
import net.gabor6505.java.pcbuilder.xml.IXmlComponentBuilder;
import net.gabor6505.java.pcbuilder.xml.NodeList;

import java.util.ArrayList;
import java.util.List;

public class Case extends Component {

    public final static IXmlComponentBuilder DATA_HANDLER = (c, properties, list) -> list.add(new Case(c.getComponentInfo(), properties));

    public Case(NodeList componentInfoNode, ComponentProperties properties) {
        super(componentInfoNode, properties, "cases.xml");
    }

    @Override
    public List<String> getExtraInfo() {
        List<String> list = new ArrayList<>();
        addExtraInfo(list, "Cooler height:", "cooler_height_mm", "mm");
        return list;
    }
}
