package net.gabor6505.java.pcbuilder.xml;

import net.gabor6505.java.pcbuilder.components.Component;

import java.util.List;

public interface IXmlComponentBuilder {

    void processData(Node currentNode, ComponentProperties properties, List<Component> list);
}
