package net.gabor6505.java.pcbuilder.gui;

import net.gabor6505.java.pcbuilder.components.ComponentManager;
import net.gabor6505.java.pcbuilder.gui.dialog.ProgressDialog;
import net.gabor6505.java.pcbuilder.gui.dialog.ProgressDialogType;
import net.gabor6505.java.pcbuilder.utils.VersionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

// TODO make a details pane for components that will be shown when the user clicks a component
// TODO add profile and component editing capabilities
// TODO add error dialogs for Xml parse errors

// TODO document classes

// TODO implement shops to choose from and separate section for displaying the price of each category
public class MainFrame extends JFrame implements KeyEventDispatcher {

    public final static List<Image> APP_ICONS = new ArrayList<>();

    private static JScrollPane hoveredHorizontalScrollPane;
    private static JScrollPane hoveredVerticalScrollPane;

    private final static int WIDTH = 900;
    private final static int HEIGHT = 720;
    private final static int MIN_WIDTH = 480;
    private final static int MIN_HEIGHT = 360;

    private final ComparisonPane comparisonPane;

    private AtomicBoolean loading = new AtomicBoolean(true);
    private AtomicBoolean welcomeDialog = new AtomicBoolean(false);

    static {
        APP_ICONS.add(getImageByPath("/icons/icon.png"));
        APP_ICONS.add(getImageByPath("/icons/icon-512.png"));
        APP_ICONS.add(getImageByPath("/icons/icon-256.png"));
        APP_ICONS.add(getImageByPath("/icons/icon-128.png"));
        APP_ICONS.add(getImageByPath("/icons/icon-64.png"));
        APP_ICONS.add(getImageByPath("/icons/icon-32.png"));
        APP_ICONS.add(getImageByPath("/icons/icon-16.png"));
    }

    public MainFrame() {
        super();
        setIconImages(APP_ICONS);

        /*if (System.getProperty("os.name").toLowerCase().contains("mac os")) {
            try {
                com.apple.eawt.Application.getApplication().setDockIconImage(ImageIO.read(getClass().getResourceAsStream("/icons/icon.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
        setupLookAndFeel();
        UIManager.getDefaults().put("TextArea.font", UIManager.getFont("TextField.font"));

        setTitle("PC Builder");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);

        ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialogType.STARTUP);
        progressDialog.setVisible(loading);

        comparisonPane = new ComparisonPane(WIDTH, HEIGHT, this);
        ComponentManager.autoLoad(progressDialog);

        VersionManager.showWelcomeDialogIfNewerVersion(this, welcomeDialog);
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
        if (loading.get() || welcomeDialog.get()) return false;
        if (e.getID() != KeyEvent.KEY_PRESSED) return false;

        if (ProfileManager.getInstance() == null) return false;
        if (ProfileManager.getInstance().getOpenDialogCount() != 0) return false;
        if (e.isMetaDown() && e.getKeyCode() == KeyEvent.VK_H) return false;

        // Access basic functions with keyboard shortcuts
        switch (e.getKeyCode()) {
            case KeyEvent.VK_H:
                VersionManager.showHelpDialog(this);
                return true;
            case KeyEvent.VK_R:
                comparisonPane.reloadEverything();
                return true;
            case KeyEvent.VK_N:
                comparisonPane.getProfileManager().renameProfile();
                return true;
            case KeyEvent.VK_T:
            case KeyEvent.VK_PLUS:
            case KeyEvent.VK_ADD:
            case KeyEvent.VK_INSERT:
            case KeyEvent.VK_HELP:
                comparisonPane.getProfileManager().addProfile();
                return true;
            case KeyEvent.VK_W:
            case KeyEvent.VK_MINUS:
            case KeyEvent.VK_SUBTRACT:
            case KeyEvent.VK_DELETE:
                comparisonPane.getProfileManager().removeProfile();
                return true;
            case KeyEvent.VK_1:
                comparisonPane.getProfileManager().selectProfile(0);
                return true;
            case KeyEvent.VK_2:
                comparisonPane.getProfileManager().selectProfile(1);
                return true;
            case KeyEvent.VK_3:
                comparisonPane.getProfileManager().selectProfile(2);
                return true;
            case KeyEvent.VK_4:
                comparisonPane.getProfileManager().selectProfile(3);
                return true;
            case KeyEvent.VK_5:
                comparisonPane.getProfileManager().selectProfile(4);
                return true;
            case KeyEvent.VK_6:
                comparisonPane.getProfileManager().selectProfile(5);
                return true;
            case KeyEvent.VK_7:
                comparisonPane.getProfileManager().selectProfile(6);
                return true;
            case KeyEvent.VK_8:
                comparisonPane.getProfileManager().selectProfile(7);
                return true;
            case KeyEvent.VK_9:
                comparisonPane.getProfileManager().selectProfile(8);
                return true;
        }

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

    private static Image getImageByPath(String pathInJar) {
        return Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource(pathInJar));
    }
}
