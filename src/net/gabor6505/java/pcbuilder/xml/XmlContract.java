package net.gabor6505.java.pcbuilder.xml;

import net.gabor6505.java.pcbuilder.components.Component;
import net.gabor6505.java.pcbuilder.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlContract {

    public enum Folder {
        TYPES ("/types/"),
        COMPONENTS ("/components/"),
        COMPONENT_IMAGES("/component_images/"),
        CATEGORY_IMAGES ("/component_images/categories/");

        private static String profilesFolder = System.getProperty("user.home") + "/.pcbuilder/Profiles";
        private static String activeProfile = "";

        private final String folder;

        Folder(String folderName) {
            folder = folderName;
        }

        public String getValue() {
            return profilesFolder + "/" + activeProfile + folder;
        }

        public static void setProfilesFolder(String folderPath) {
            profilesFolder = folderPath;
        }

        public static void setActiveProfile(String profileName) {
            activeProfile = profileName;
        }
    }

    private final Folder folder;
    private final String fileName;
    private final String componentName;
    private final String[] nodeNames;
    private final IXmlComponentBuilder dataHandler;

    public XmlContract(Folder folder, String fileName, String componentName, String[] nodeNames, IXmlComponentBuilder dataHandler) {
        this.folder = folder;
        this.fileName = fileName;
        this.componentName = componentName;
        if (nodeNames == null) this.nodeNames = new String[0];
        else this.nodeNames = nodeNames;
        this.dataHandler = dataHandler;
    }

    public XmlContract(Folder folder, String fileName, IXmlComponentBuilder dataHandler) {
        this(folder, fileName, "", new String[0], dataHandler);
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

    public void processData(Node currentNode, ComponentProperties properties, List<Component> list) {
        if (dataHandler == null) return;
        dataHandler.processData(currentNode, properties, list);
    }

    public String getTrimmedFileName() {
        return Utils.removeExtension(getFileName());
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

    public static void setProfilesFolder(String folderPath) {
        Folder.setProfilesFolder(folderPath);
    }

    public static void setActiveProfile(String profileName) {
        Folder.setActiveProfile(profileName);
    }
}
