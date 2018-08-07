package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.xml.ComponentProperties;
import net.gabor6505.java.pcbuilder.xml.IXmlComponentBuilder;
import net.gabor6505.java.pcbuilder.xml.NodeList;

import java.util.ArrayList;
import java.util.List;

// Universal class for handling both HDD's and SSD's
public class Storage extends Component {

    public final static IXmlComponentBuilder DATA_HANDLER = (storage, properties, list) -> list.add(new Storage(storage.getComponentInfo(), properties));

    public Storage(NodeList componentInfoNode, ComponentProperties properties) {
        super(componentInfoNode, properties, null);
    }

    @Override
    public List<String> getExtraInfo() {
        List<String> list = new ArrayList<>();
        addExtraInfo(list, "Capacity:", "capacity_mb", "");
        return list;
    }
}
