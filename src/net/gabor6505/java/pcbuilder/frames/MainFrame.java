package net.gabor6505.java.pcbuilder.frames;

import net.gabor6505.java.pcbuilder.elements.ScrollPane2D;
import net.gabor6505.java.pcbuilder.xml.NodeList;
import net.gabor6505.java.pcbuilder.xml.XmlContract;
import net.gabor6505.java.pcbuilder.xml.XmlParser;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private final static int WIDTH = 720;
    private final static int HEIGHT = 480;

    private final static List<String> formFactors;

    static {
        NodeList root = XmlParser.parseXml(XmlContract.Folder.TYPES, "form_factors.xml");
        formFactors = root.getNodesContent("form_factor");
    }

    public MainFrame() {
        super();
        init();
    }

    private void init() {
        setTitle("PC Builder");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setMinimumSize(new Dimension(WIDTH, HEIGHT));
        //setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

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

        ScrollPane2D comparisonPane = new ScrollPane2D(WIDTH, HEIGHT);
        setContentPane(comparisonPane);

        for (int j = 0; j < 15; j++) {
            List<JComponent> testLabels = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                JLabel longLabel = new JLabel("asefaesfesfesfgesgersgrsgdrsgdrsgderg ");
                testLabels.add(longLabel);
            }
            comparisonPane.addRow(testLabels);
        }

        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);

        try {
            Class.forName("net.gabor6505.java.pcbuilder.components.Motherboard");
            Class.forName("net.gabor6505.java.pcbuilder.components.Cpu");
            Class.forName("net.gabor6505.java.pcbuilder.components.Ram");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getFormFactors() {
        return formFactors;
    }

    public static String getFormFactor(String formFactor) {
        for (String formFact : formFactors) {
            if (formFact.equals(formFactor)) return formFact;
        }
        return null;
    }
}
