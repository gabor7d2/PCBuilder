package net.gabor6505.java.pcbuilder.frames;

import net.gabor6505.java.pcbuilder.components.ComponentManager;
import net.gabor6505.java.pcbuilder.elements.ComparisonPane;
import net.gabor6505.java.pcbuilder.utils.TypeNotPresentException;
import net.gabor6505.java.pcbuilder.xml.NodeList;
import net.gabor6505.java.pcbuilder.xml.XmlContract;
import net.gabor6505.java.pcbuilder.xml.XmlParser;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// TODO document classes
// TODO auto sizing label
public class MainFrame extends JFrame {

    private final static int WIDTH = 800;
    private final static int HEIGHT = 720;

    private ComparisonPane comparisonPane;

    public MainFrame() {
        super();
        setLookAndFeel();
        comparisonPane = new ComparisonPane(WIDTH, HEIGHT, MainFrame.this);
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
    }

    private void loadComponents() {
        ComponentManager.autoLoad();
        comparisonPane.disableCategory("Graphics Card");
    }
}
