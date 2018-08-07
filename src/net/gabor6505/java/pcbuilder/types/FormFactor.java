package net.gabor6505.java.pcbuilder.types;

import net.gabor6505.java.pcbuilder.xml.NodeList;
import net.gabor6505.java.pcbuilder.xml.XmlContract;
import net.gabor6505.java.pcbuilder.xml.XmlParser;

import java.util.ArrayList;
import java.util.List;

public class FormFactor implements TypeManager.ReloadListener {

    private final static XmlContract CONTRACT = new XmlContract(XmlContract.Folder.TYPES, "form_factors.xml");

    private final static List<String> formFactors = new ArrayList<>();

    static {
        TypeManager.addReloadListener(FormFactor.class.getName(), new FormFactor(), 0);
        load();
    }

    public static List<String> getFormFactors() {
        return formFactors;
    }

    public static String getFormFactor(String formFactor) {
        for (String formFact : formFactors) {
            if (formFact.equals(formFactor)) return formFact;
        }
        new TypeNotPresentException("Form factor", CONTRACT, formFactor).printStackTrace();
        return null;
    }

    private static void load() {
        NodeList root = XmlParser.parseXml(CONTRACT);
        formFactors.addAll(root.getNodesContent("form_factor"));
    }

    @Override
    public void reload() {
        formFactors.clear();
        load();
    }
}
