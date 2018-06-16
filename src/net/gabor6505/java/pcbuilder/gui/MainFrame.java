package net.gabor6505.java.pcbuilder.gui;

import net.gabor6505.java.pcbuilder.components.ComponentManager;
import net.gabor6505.java.pcbuilder.gui.dialog.LoadingDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

// TODO make a details pane for components that will be shown when the user clicks a component
// TODO make component panes resize when the product's model number / name is too long
// TODO add profile and component editing capabilities
// TODO add error message dialogs for Xml parse errors

// TODO document classes
// TODO make icon for app (bundle jar file for each OS platform)

// TODO implement component prices and shops to choose from

public class MainFrame extends JFrame implements KeyEventDispatcher {

    private static JScrollPane hoveredHorizontalScrollPane;
    private static JScrollPane hoveredVerticalScrollPane;

    private final static int WIDTH = 800;
    private final static int HEIGHT = 720;
    private final static int MIN_WIDTH = 480;
    private final static int MIN_HEIGHT = 360;

    private final ComparisonPane comparisonPane;

    public MainFrame() {
        super();

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));

        /*if (System.getProperty("os.name").toLowerCase().contains("mac os")) {
            try {
                Application.getApplication().setDockIconImage(ImageIO.read(getClass().getResourceAsStream("/icon.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        /*FileUtils.extractZip("../config2_extr", "../config2.zip", false, false).printResult();
        System.exit(0);*/

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
        setupLookAndFeel();

        setTitle("PC Builder");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);

        LoadingDialog loadingDialog = new LoadingDialog(this);

        comparisonPane = new ComparisonPane(WIDTH, HEIGHT, MainFrame.this);
        ComponentManager.autoLoad(loadingDialog);

        setupKeyBindings();
    }

    private void setupKeyBindings() {
        InputMap iMap = comparisonPane.getPanel().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap aMap = comparisonPane.getPanel().getActionMap();

        iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "navRight");
        iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "navLeft");
        iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "navUp");
        iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "navDown");

        aMap.put("navRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.paramString());
            }
        });
    }

    private void setupLookAndFeel() {
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

    public static void setHoveredHorizontalScrollPane(JScrollPane pane) {
        hoveredHorizontalScrollPane = pane;
    }

    public static void setHoveredVerticalScrollPane(JScrollPane pane) {
        hoveredVerticalScrollPane = pane;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED) return false;
        if (e.getComponent() instanceof JComboBox) return false;

        // TODO fix program not rechecking what the cursor hovers over when we move the vertical pane with up/down key
        if (hoveredHorizontalScrollPane != null) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_RIGHT:
                    hoveredHorizontalScrollPane.getHorizontalScrollBar().setValue(hoveredHorizontalScrollPane.getHorizontalScrollBar().getValue() + 32);
                    return true;
                case KeyEvent.VK_LEFT:
                    hoveredHorizontalScrollPane.getHorizontalScrollBar().setValue(hoveredHorizontalScrollPane.getHorizontalScrollBar().getValue() - 32);
                    return true;
            }
        }
        if (hoveredVerticalScrollPane != null) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    hoveredVerticalScrollPane.getVerticalScrollBar().setValue(hoveredVerticalScrollPane.getVerticalScrollBar().getValue() - 32);
                    return true;
                case KeyEvent.VK_DOWN:
                    hoveredVerticalScrollPane.getVerticalScrollBar().setValue(hoveredVerticalScrollPane.getVerticalScrollBar().getValue() + 32);
                    return true;
            }
        }
        return false;
    }
}
