package net.gabor6505.java.pcbuilder.gui;

import net.gabor6505.java.pcbuilder.components.ComponentManager;
import net.gabor6505.java.pcbuilder.components.StateChangeListener;
import net.gabor6505.java.pcbuilder.elements.*;
import net.gabor6505.java.pcbuilder.types.TypeManager;
import net.gabor6505.java.pcbuilder.utils.Format;
import net.gabor6505.java.pcbuilder.utils.Utils;
import net.gabor6505.java.pcbuilder.utils.VersionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sun.awt.PeerEvent.LOW_PRIORITY_EVENT;

public class ComparisonPane extends ScrollPane2D implements ActionListener, StateChangeListener {

    private final Map<String, ComponentCategory> categoryMap = new HashMap<>();
    private final Map<String, Integer> categoryIndexMap = new HashMap<>();

    private final JFrame parentFrame;

    private final JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel footerPanel;

    private AutoScrollPane headerPane;

    private JLabel totalPrice;
    private ProfileManager profileManager;

    public ComparisonPane(int windowWidth, int windowHeight, JFrame frame) {
        super(windowWidth, windowHeight);
        parentFrame = frame;
        mainPanel = new JPanel(new BorderLayout());

        EventQueue.invokeLater(() -> {
            MainFrame.setHoveredVerticalScrollPane(this);
            mainPanel.add(this);

            headerPane = new AutoScrollPane(SwingConstants.HORIZONTAL, true);
            headerPane.getViewport().setBackground(Color.DARK_GRAY);
            headerPanel = headerPane.getContentPanel();
            headerPanel.setBackground(Color.DARK_GRAY);
            headerPanel.setBorder(BorderFactory.createMatteBorder(8, 4, 8, 4, Color.DARK_GRAY));
            mainPanel.add(headerPane, BorderLayout.NORTH);

            AutoScrollPane footerPane = new AutoScrollPane(SwingConstants.HORIZONTAL, true);
            footerPane.getViewport().setBackground(Color.DARK_GRAY);
            footerPanel = footerPane.getContentPanel();
            footerPanel.setBackground(Color.DARK_GRAY);
            footerPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 8));
            mainPanel.add(footerPane, BorderLayout.SOUTH);

            //String totalStr = Format.formatCurrency(String.valueOf(10000000), "", " Ft");
            //totalPrice = new JLabel("Total Price: " + totalStr);
            totalPrice = new JLabel("Total Price: 0 Ft");

            totalPrice.setForeground(Color.WHITE);
            totalPrice.setFont(totalPrice.getFont().deriveFont(Font.BOLD, 13));
            totalPrice.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 8, Color.DARK_GRAY));

            //Utils.fixSize(totalPrice, totalPrice.getPreferredSize());
            //totalPrice.setText("Total Price: 0 Ft");
            totalPrice.setVisible(false);
            footerPanel.add(totalPrice);

            profileManager = new ProfileManager(this);
            footerPanel.add(profileManager);

            JButton rename = new JButton("Rename");
            rename.setBackground(Color.DARK_GRAY);
            rename.addActionListener(e -> profileManager.renameProfile());
            footerPanel.add(rename);

            JButton remove = new JButton("Remove");
            remove.setBackground(Color.DARK_GRAY);
            remove.addActionListener(e -> profileManager.removeProfile());
            footerPanel.add(remove);

            JButton add = new JButton("Add");
            add.setBackground(Color.DARK_GRAY);
            add.addActionListener(e -> profileManager.addProfile());
            footerPanel.add(add);

            JButton reload = new JButton("Reload");
            reload.setBackground(Color.DARK_GRAY);
            reload.addActionListener(e -> reloadEverything());
            footerPanel.add(reload);

            JButton help = new JButton("Help");
            help.setBackground(Color.DARK_GRAY);
            help.addActionListener(e -> VersionManager.showHelpDialog(parentFrame));
            footerPanel.add(help);

            mainPanel.revalidate();
        });

        if (frame != null) {
            frame.setContentPane(mainPanel);

            // Set the size of the window the size of the header panel
            /*Utils.postEvent(LOW_PRIORITY_EVENT, () -> {
                GraphicsConfiguration config = frame.getGraphicsConfiguration();
                GraphicsDevice currentScreen = config.getDevice();
                int screenWidth = currentScreen.getDefaultConfiguration().getBounds().width;
                System.out.println(headerPanel.getWidth());
                if (headerPanel.getWidth() <= screenWidth && frame.getWidth() < headerPanel.getWidth()) {
                    frame.setSize(new Dimension(headerPanel.getWidth(), frame.getHeight()));
                    headerPanel.revalidate();
                    mainPanel.revalidate();
                    parentFrame.revalidate();
                    headerPane.handleScrollbars();
                }
            });*/
        }
        ComponentManager.addStateChangeListener(this);
    }

    public JFrame getParentFrame() {
        return parentFrame;
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public void updateTotalPrice() {
        double total = 0;

        for (Component comp : headerPanel.getComponents()) {
            if (comp instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) comp;
                if (checkBox.isSelected()) {
                    ComponentCategory.CategoryItem item = categoryMap.get(checkBox.getText()).getSelectedItem();
                    if (item == null) continue;
                    try {
                        total += Double.parseDouble(item.getComponent().getPrice());
                    } catch (Exception ignored) {

                    }
                }
            }
        }

        String totalStr = Format.formatCurrency(String.valueOf(total), "", " Ft");
        totalPrice.setVisible(total != 0);
        totalPrice.setText("Total Price: " + totalStr);
    }

    public int addCategory(ComponentCategory category, boolean isEnabled) {
        int index;

        category.addCategoryItemListener((category1, item, index1) -> updateTotalPrice());

        if (!categoryIndexMap.containsKey(category.getDisplayName())) {
            index = addRow(category.getItemComponents(), category.getPreviewPanel(), isEnabled);
            categoryIndexMap.put(category.getDisplayName(), index);
            categoryMap.put(category.getDisplayName(), category);

            JCheckBox checkBox = new JCheckBox(category.getDisplayName());
            checkBox.setSelected(isEnabled);
            checkBox.setForeground(Color.WHITE);
            checkBox.setBackground(Color.DARK_GRAY);
            checkBox.setBorder(BorderFactory.createMatteBorder(0, 8, 0, 8, Color.DARK_GRAY));
            checkBox.addActionListener(this);
            checkBox.setVisible(false);
            headerPanel.add(checkBox);
            headerPanel.revalidate();

            EventQueue.invokeLater(() -> {
                checkBox.setVisible(true);
                mainPanel.revalidate();
                parentFrame.revalidate();
                headerPane.handleScrollbars();
                updateTotalPrice();
            });
        } else {
            index = categoryIndexMap.get(category.getDisplayName());
            clearRow(index);
            setPreviewPanel(index, category.getPreviewPanel());
            addComponents(index, category.getItemComponents());
        }
        return index;
    }

    public void removeCategory(String categoryName) {
        if (categoryIndexMap.containsKey(categoryName)) {
            int index = categoryIndexMap.get(categoryName);
            removeRow(index);
            headerPanel.remove(findCheckBoxByName(categoryName));
            headerPanel.revalidate();

            categoryIndexMap.remove(categoryName);
            updateTotalPrice();
        }
    }

    public void enableCategory(String categoryName) {
        Utils.postEvent(LOW_PRIORITY_EVENT, () -> {
            JCheckBox cb = findCheckBoxByName(categoryName);
            if (cb == null) return;
            if (!cb.isSelected()) actionPerformed(new ActionEvent(cb, 0, cb.getText()));
            cb.setSelected(true);
            updateTotalPrice();
        });
    }

    public void disableCategory(String categoryName) {
        Utils.postEvent(LOW_PRIORITY_EVENT, () -> {
            JCheckBox cb = findCheckBoxByName(categoryName);
            if (cb == null) return;
            if (cb.isSelected()) actionPerformed(new ActionEvent(cb, 0, cb.getText()));
            cb.setSelected(false);
            updateTotalPrice();
        });
    }

    private JCheckBox findCheckBoxByName(String categoryName) {
        for (Component component : headerPanel.getComponents()) {
            if (component instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) component;
                if (checkBox.getText().equals(categoryName)) return checkBox;
            }
        }
        return null;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int index = categoryIndexMap.get(e.getActionCommand());
        toggleRowVisible(index);
        parentFrame.revalidate();
        updateTotalPrice();
    }

    @Override
    public void loaded(String type, String displayName, boolean enabled, List<net.gabor6505.java.pcbuilder.components.Component> affectedComponents, String categoryUrl, int selIndex) {
        addCategory(new ComponentCategory(type, displayName, affectedComponents, categoryUrl, selIndex), enabled);
    }

    @Override
    public void reloaded(String type, String displayName, boolean enabled, List<net.gabor6505.java.pcbuilder.components.Component> affectedComponents, String categoryUrl, int selIndex) {
        addCategory(new ComponentCategory(type, displayName, affectedComponents, categoryUrl, selIndex), enabled);
    }

    @Override
    public void removed(String type) {
        removeCategory(type);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        MainFrame.setHoveredHorizontalScrollPane((JScrollPane) e.getComponent().getParent().getParent().getParent());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        MainFrame.setHoveredHorizontalScrollPane(null);
    }

    // TODO recycle components instead of removing and readding them
    public void reloadEverything() {
        // A new set of images are going to be loaded, so discard any
        // image loading that may still be running to fill old ImageLabels
        ImageLabel.discardBackgroundTasks();

        /*for (ComponentCategory cat : categoryMap.values()) {
            ImageLabel previewImage = cat.getPreview().getImageLabel();
            if (previewImage != null && previewImage.getIcon() != null && previewImage.getIcon() instanceof ImageIcon) {
                System.out.println("Removing preview image!");
                ImageIcon icon = (ImageIcon) previewImage.getIcon();
                icon.getImage().flush();
            }

            for (ComponentCategory.CategoryItem item : cat.getItems()) {
                ImageLabel itemImage = item.getImageLabel();
                if (itemImage != null && itemImage.getIcon() != null && itemImage.getIcon() instanceof ImageIcon) {
                    System.out.println("Removing item image!");
                    ImageIcon icon = (ImageIcon) itemImage.getIcon();
                    icon.getImage().flush();
                }
            }
        }*/

        removeAllRows();
        headerPanel.removeAll();
        mainPanel.revalidate();
        categoryIndexMap.clear();

        //profileManager.reload();
        TypeManager.reload();
        ComponentManager.reload();

        updateTotalPrice();
    }
}
