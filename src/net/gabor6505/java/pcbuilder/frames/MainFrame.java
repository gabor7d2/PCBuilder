package net.gabor6505.java.pcbuilder.frames;

import net.gabor6505.java.pcbuilder.utils.Format;
import net.gabor6505.java.pcbuilder.xml.NodeList;
import net.gabor6505.java.pcbuilder.xml.XmlContract;
import net.gabor6505.java.pcbuilder.xml.XmlParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.util.List;

public class MainFrame extends JFrame implements MouseListener {

    private final int WIDTH = 640;
    private final int HEIGHT = 400;

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
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setMaximumSize(new Dimension(WIDTH, Integer.MAX_VALUE));

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

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        getContentPane().add(panel);

        JButton button = new JButton("Click Me");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(button);
        button.addMouseListener(this);

        JRadioButton radioButton = new JRadioButton("Choose me");
        JRadioButton radioButton2 = new JRadioButton("Choose me instead");
        radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        radioButton2.setAlignmentX(Component.CENTER_ALIGNMENT);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(radioButton);
        buttonGroup.add(radioButton2);
        panel.add(radioButton);
        panel.add(radioButton2);

        JTextArea textArea = new JTextArea();
        textArea.setFont(textArea.getFont().deriveFont(12f));
        panel.add(textArea);

        JTextField text = new JTextField("Max RAM: " + Format.formatUnitValue(16000, Format.BYTES));
        text.setAlignmentX(Component.CENTER_ALIGNMENT);
        //text.setMargin(new Insets(30, 20, 30, 20));
        panel.add(text);

        JCheckBox checkBox = new JCheckBox("Tick me please :)");
        checkBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(checkBox);

        pack();
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

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    private long time = 0L;

    @Override
    public void mousePressed(MouseEvent e) {
        time = System.currentTimeMillis();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        try {
            Desktop d = Desktop.getDesktop();
            if (System.currentTimeMillis() - time >= 1500) {
                d.browse(new URI("https://www.youtube.com/watch?v=s3xzVfyGLn4&t=48s"));
            } else {
                d.browse(new URI("http://mek.oszk.hu/06300/06304/06304.htm"));
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

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
