package net.gabor6505.java.pcbuilder.xml;

import org.w3c.dom.Document;

public interface IXmlDocumentViewer {

    /**
     * Use this method to view a document
     *
     * @param doc The document that got loaded
     * @param nodes All the top level nodes below the document root node
     */
    void viewDocument(Document doc, NodeList nodes);
}
