package net.gabor6505.java.pcbuilder.frames;

import net.gabor6505.java.pcbuilder.components.*;
import net.gabor6505.java.pcbuilder.elements.ComparisonPane;
import net.gabor6505.java.pcbuilder.elements.ComponentCategory;
import net.gabor6505.java.pcbuilder.utils.TypeNotPresentException;
import net.gabor6505.java.pcbuilder.xml.NodeList;
import net.gabor6505.java.pcbuilder.xml.XmlContract;
import net.gabor6505.java.pcbuilder.xml.XmlParser;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// TODO document classes
// TODO auto sizing label
// TODO make horizontal scrollbars hover over content and hide when mouse is not over it or after a bit of time the mouse has not moved
public class MainFrame extends JFrame {

    private final static int WIDTH = 800;
    private final static int HEIGHT = 720;

    private final static List<String> formFactors;

    private final ComparisonPane comparisonPane;

    static {
        NodeList root = XmlParser.parseXml(XmlContract.Folder.TYPES, "form_factors.xml");
        formFactors = root.getNodesContent("form_factor");
    }

    public MainFrame() {
        super();
        setLookAndFeel();
        comparisonPane = new ComparisonPane(WIDTH, HEIGHT, this);
        init();
        loadComponents();
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if (info.getName().equals("Nimbus")) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored2) {
            }
        }
    }

    private void init() {
        setTitle("PC Builder");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
        revalidate();
    }

    private void loadComponents() {
        GenericComponent.autoLoad();
        comparisonPane.disableCategory("Graphics Card");
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
}
