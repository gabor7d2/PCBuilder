package net.gabor6505.java.pcbuilder.types;

import net.gabor6505.java.pcbuilder.utils.TypeNotPresentException;
import net.gabor6505.java.pcbuilder.xml.NodeList;
import net.gabor6505.java.pcbuilder.xml.XmlContract;
import net.gabor6505.java.pcbuilder.xml.XmlParser;

import java.util.ArrayList;
import java.util.List;

public class FormFactor implements ReloadListener {

    private final static List<String> formFactors = new ArrayList<>();

    static {
        TypeManager.addReloadListener(FormFactor.class.getName(), new FormFactor());
        load();
    }

    public static List<String> getFormFactors() {
        return formFactors;
    }

    public static String getFormFactor(String formFactor) {
        for (String formFact : formFactors) {
            if (formFact.equals(formFactor)) return formFact;
        }
        new TypeNotPresentException("Form factor \"" + formFactor + "\" is not registered in form_factors.xml").printStackTrace();
        return null;
    }

    private static void load() {
        NodeList root = XmlParser.parseXml(XmlContract.Folder.TYPES, "form_factors.xml");
        formFactors.addAll(root.getNodesContent("form_factor"));
    }

    @Override
    public void reload() {
        formFactors.clear();
        load();
    }
}
