package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.xml.ComponentProperties;
import net.gabor6505.java.pcbuilder.xml.NodeList;

class IGpu extends Component {

    public final static String[] NODE_NAMES = new String[]{"base_frequency_mhz", "max_frequency_mhz", "supported_displays"};

    IGpu(NodeList componentInfoNode, ComponentProperties properties) {
        super(componentInfoNode, properties, "");
    }
}
