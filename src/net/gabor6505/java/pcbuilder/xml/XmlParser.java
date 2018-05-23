package net.gabor6505.java.pcbuilder.xml;

import net.gabor6505.java.pcbuilder.components.Component;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;

public class XmlParser {

    private XmlParser() {

    }

    public static NodeList[] parseXml(XmlContract contract, boolean scanMoreFiles) {
        return parseXml(contract, scanMoreFiles, true);
    }

    public static NodeList[] parseXml(XmlContract contract, boolean scanMoreFiles, boolean printTime) {
        List<File> files = contract.getFiles(scanMoreFiles);
        NodeList[] returnNodes = new NodeList[files.size()];

        long startTime = System.currentTimeMillis();
        long startTime2 = 0;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            for (int i = 0; i < files.size(); i++) {
                Document doc = dBuilder.parse(files.get(i));
                returnNodes[i] = new NodeList(doc.getDocumentElement().getChildNodes());
            }

            startTime2 = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (printTime) System.out.println("Parsing " + contract.getFileName() + " took " + (System.currentTimeMillis() - startTime) + " ms (nodes took " + (System.currentTimeMillis() - startTime2) + " ms)");

        return returnNodes;
    }

    public static NodeList parseXml(XmlContract contract) {
        NodeList[] nodes = parseXml(contract, false, true);
        if (nodes.length > 0) return nodes[0];
        else return new NodeList();
    }

    public static NodeList parseXml(XmlContract.Folder folder, String fileName) {
        return parseXml(new XmlContract(folder, fileName));
    }

    public static void parseXmlComponents(XmlContract contract, List<Component> list) {
        long startTime = System.currentTimeMillis();
        NodeList[] nodes = parseXml(contract, true, false);
        System.out.println("Parsing " + contract.getFileName() + " took " + (System.currentTimeMillis() - startTime) + " ms");

        for (NodeList root : nodes) {
            for (Node componentNode : root.getNodes(contract.getComponentName())) {

                if (contract.getNodeNames() == null) contract.processData(componentNode, null, list);
                else contract.processData(componentNode, componentNode.getNodesContent(contract.getNodeNames()), list);
            }
        }
        System.out.println("Loading " + contract.getFileName() + " took " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
