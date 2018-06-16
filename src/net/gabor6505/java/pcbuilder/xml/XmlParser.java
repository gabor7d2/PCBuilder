package net.gabor6505.java.pcbuilder.xml;

import net.gabor6505.java.pcbuilder.components.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.util.List;

public class XmlParser {

    public enum XmlParseResult {
        SUCCESS("Parse completed successfully!"),
        FILE_DOESNT_EXIST("The specified file doesn't exist!"),
        IO_ERROR("An IO error occurred while parsing!"),
        MALFORMED_DOCUMENT("The specified xml document is malformed!"),
        UNKNOWN("An unknown error happened while parsing!");

        private final String localizedMessage;

        XmlParseResult(String locMsg) {
            localizedMessage = locMsg;
        }

        public String getMessage() {
            return localizedMessage;
        }
    }

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
        if (printTime)
            System.out.println("Parsing " + contract.getFileName() + " took " + (System.currentTimeMillis() - startTime) + " ms (nodes took " + (System.currentTimeMillis() - startTime2) + " ms)");

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

    /**
     * Loads an xml file, and then passes it's data to a class that implements {@link IXmlDocumentEditor},
     * then saves the modifications made in the {@link IXmlDocumentEditor} back to disk
     *
     * @param filePath The path of the xml file to load
     * @param editor   The editor which handles modifying the document
     * @return The result of the xml parse operation
     */
    public static XmlParseResult editXml(String filePath, IXmlDocumentEditor editor) {
        try {
            // Read file
            File file = new File(filePath);
            if (!file.exists()) return XmlParseResult.FILE_DOESNT_EXIST;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(file);
            NodeList docNodes = new NodeList(doc.getDocumentElement().getChildNodes());

            // Process and modify it's contents
            editor.editDocument(doc, docNodes);

            // Fix spacing
            XPath xPath = XPathFactory.newInstance().newXPath();
            org.w3c.dom.NodeList nodeList = (org.w3c.dom.NodeList) xPath.evaluate("//text()[normalize-space()='']", doc, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); ++i) {
                org.w3c.dom.Node node = nodeList.item(i);
                node.getParentNode().removeChild(node);
            }

            // Write back to disk
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // Create a new file if it somehow disappeared between loading it and editing it
            if (!file.exists()) file.createNewFile();

            DOMSource domSource = new DOMSource(doc);
            StreamResult sr = new StreamResult(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
            tf.transform(domSource, sr);

            return XmlParseResult.SUCCESS;
        } catch (SAXException e) {
            e.printStackTrace();
            return XmlParseResult.MALFORMED_DOCUMENT;
        } catch (IOException e) {
            e.printStackTrace();
            return XmlParseResult.IO_ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            return XmlParseResult.UNKNOWN;
        }
    }

    /**
     * Loads an xml file, and then passes it's data to a class that implements {@link IXmlDocumentViewer},
     * this is the same as {@link XmlParser#parseXml(XmlContract.Folder, String)}, but it is for any xml file
     *
     * @param filePath The path of the xml file to load
     * @param viewer   The viewer which handles viewing of the content and getting the information from the document
     * @return The result of the xml parse operation
     */
    public static XmlParseResult viewXml(String filePath, IXmlDocumentViewer viewer) {
        try {
            // Read xml
            File file = new File(filePath);
            if (!file.exists()) return XmlParseResult.FILE_DOESNT_EXIST;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(file);
            NodeList docNodes = new NodeList(doc.getDocumentElement().getChildNodes());

            // Process it's contents
            viewer.viewDocument(doc, docNodes);

            return XmlParseResult.SUCCESS;
        } catch (SAXException e) {
            e.printStackTrace();
            return XmlParseResult.MALFORMED_DOCUMENT;
        } catch (IOException e) {
            e.printStackTrace();
            return XmlParseResult.IO_ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            return XmlParseResult.UNKNOWN;
        }
    }
}
