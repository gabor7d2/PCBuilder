package net.gabor6505.java.pcbuilder.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XmlContract {

    public enum Folder {
        TYPES (CONFIG_FOLDER + "/types/"),
        COMPONENTS (CONFIG_FOLDER + "/components/");

        private final String folder;

        Folder(String folderName) {
            folder = folderName;
        }

        public String getValue() {
            return folder;
        }
    }

    private final static String CONFIG_FOLDER = "./config/";

    private final Folder folder;
    private final String fileName;
    private final String componentName;
    private final String[] nodeNames;
    private final IXmlComponentBuilder dataHandler;

    public XmlContract(Folder folder, String fileName, String componentName, String[] nodeNames, IXmlComponentBuilder dataHandler) {
        this.folder = folder;
        this.fileName = fileName;
        this.componentName = componentName;
        this.nodeNames = nodeNames;
        this.dataHandler = dataHandler;
    }

    public XmlContract(Folder folder, String fileName, IXmlComponentBuilder dataHandler) {
        this(folder, fileName, "", null, dataHandler);
    }

    public XmlContract(Folder folder, String fileName) {
        this(folder, fileName, "", null, null);
    }

    public String getFolderName() {
        return folder.getValue();
    }

    public String getFileName() {
        return fileName;
    }

    public String getComponentName() {
        return componentName;
    }

    public String[] getNodeNames() {
        return nodeNames;
    }

    public IXmlComponentBuilder getDataHandler() {
        return dataHandler;
    }

    public void processData(Node currentNode, ComponentProperties properties) {
        if (dataHandler == null) return;
        dataHandler.processData(currentNode, properties);
    }

    private String getTrimmedFileName() {
        if (getFileName().contains(".")) {
            return getFileName().substring(0, getFileName().lastIndexOf('.'));
        } else return getFileName();
    }

    public List<File> getFiles(boolean scanMoreFiles) {
        List<File> foundFiles = new ArrayList<>(0);

        if (scanMoreFiles) {
            File[] files = new File(getFolderName()).listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.getName().contains(getTrimmedFileName()) && file.isFile()) {
                        foundFiles.add(file);
                    }
                }
            }
        } else {
            File file = new File(getFolderName() + getFileName());
            if (file.exists() && file.isFile()) foundFiles.add(file);
        }

        return foundFiles;
    }
}
