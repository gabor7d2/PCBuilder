package net.gabor6505.java.pcbuilder.xml;

import org.w3c.dom.Document;

public interface IXmlDocumentEditor {

    /**
     * Use this method to view a document and edit it
     *
     * @param doc The document that got loaded
     * @param nodes All the top level nodes below the document root node
     * @return True if the document should be saved to disk, false otherwise
     */
    boolean editDocument(Document doc, NodeList nodes);
}
