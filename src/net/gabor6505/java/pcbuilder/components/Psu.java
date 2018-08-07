package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.xml.ComponentProperties;
import net.gabor6505.java.pcbuilder.xml.IXmlComponentBuilder;
import net.gabor6505.java.pcbuilder.xml.NodeList;

import java.util.ArrayList;
import java.util.List;

public class Psu extends Component {

    public final static IXmlComponentBuilder DATA_HANDLER = (psu, properties, list) -> list.add(new Psu(psu.getComponentInfo(), properties));

    public Psu(NodeList componentInfoNode, ComponentProperties properties) {
        super(componentInfoNode, properties, "psus.xml");
    }

    @Override
    public List<String> getExtraInfo() {
        List<String> list = new ArrayList<>();
        addExtraInfo(list, "performance_w");
        addExtraInfo(list, "", "efficiency_rating", "Efficiency");
        addExtraInfo(list, "modularity");
        return list;
    }
}
