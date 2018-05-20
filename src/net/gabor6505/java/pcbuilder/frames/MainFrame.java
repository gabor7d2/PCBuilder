package net.gabor6505.java.pcbuilder.frames;

import net.gabor6505.java.pcbuilder.components.Cpu;
import net.gabor6505.java.pcbuilder.components.Motherboard;
import net.gabor6505.java.pcbuilder.components.Ram;
import net.gabor6505.java.pcbuilder.elements.ComparisonPane;
import net.gabor6505.java.pcbuilder.elements.ComponentCategory;
import net.gabor6505.java.pcbuilder.xml.NodeList;
import net.gabor6505.java.pcbuilder.xml.XmlContract;
import net.gabor6505.java.pcbuilder.xml.XmlParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

// TODO document classes
// TODO auto sizing label
// TODO make horizontal scrollbars hover over content and hide when mouse is not over it or after a bit of time the mouse has not moved
public class MainFrame extends JFrame implements KeyListener {

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
        comparisonPane = new ComparisonPane(WIDTH, HEIGHT, this);
        init();
    }

    private void init() {
        setTitle("PC Builder");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setMinimumSize(new Dimension(WIDTH, HEIGHT));
        //setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            /*UIDefaults defaults = UIManager.getLookAndFeel().getDefaults();
            for (Object value : defaults.values()) {
                System.out.println(value);
            }*/
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

        comparisonPane.addCategory(new ComponentCategory("Motherboard", Motherboard.getMotherboardList()));
        comparisonPane.addCategory(new ComponentCategory("CPU", Cpu.getCpuList()));
        comparisonPane.addCategory(new ComponentCategory("RAM", Ram.getRamList()));

        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
        revalidate();
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

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
