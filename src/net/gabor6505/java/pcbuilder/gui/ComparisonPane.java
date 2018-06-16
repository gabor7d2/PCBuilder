package net.gabor6505.java.pcbuilder.gui;

import net.gabor6505.java.pcbuilder.components.ComponentManager;
import net.gabor6505.java.pcbuilder.components.StateChangeListener;
import net.gabor6505.java.pcbuilder.elements.AutoScrollPane;
import net.gabor6505.java.pcbuilder.elements.ComponentCategory;
import net.gabor6505.java.pcbuilder.elements.ScrollPane2D;
import net.gabor6505.java.pcbuilder.types.TypeManager;
import net.gabor6505.java.pcbuilder.utils.Utils;
import sun.awt.PeerEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComparisonPane extends ScrollPane2D implements ActionListener, StateChangeListener {

    private final Map<String, Integer> categoryIndexMap = new HashMap<>();

    private final JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel footerPanel;

    private ProfileManager profileManager;

    private final JFrame parentFrame;

    public ComparisonPane(int windowWidth, int windowHeight, JFrame frame) {
        super(windowWidth, windowHeight);
        parentFrame = frame;
        mainPanel = new JPanel(new BorderLayout());

        EventQueue.invokeLater(() -> {
            MainFrame.setHoveredVerticalScrollPane(this);
            mainPanel.add(this);

            AutoScrollPane headerPane = new AutoScrollPane(SwingConstants.HORIZONTAL, true);
            headerPane.getViewport().setBackground(Color.DARK_GRAY);
            headerPanel = headerPane.getContentPanel();
            headerPanel.setBackground(Color.DARK_GRAY);
            headerPanel.setBorder(BorderFactory.createMatteBorder(8, 4, 8, 4, Color.DARK_GRAY));
            mainPanel.add(headerPane, BorderLayout.NORTH);

            AutoScrollPane footerPane = new AutoScrollPane(SwingConstants.HORIZONTAL, true);
            footerPane.getViewport().setBackground(Color.DARK_GRAY);
            footerPanel = footerPane.getContentPanel();
            footerPanel.setBackground(Color.DARK_GRAY);
            footerPanel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.DARK_GRAY));
            mainPanel.add(footerPane, BorderLayout.SOUTH);

            profileManager = new ProfileManager(this);
            footerPanel.add(profileManager);

            JButton rename = new JButton("Rename");
            rename.addActionListener(e -> profileManager.renameProfile());
            footerPanel.add(rename);

            JButton remove = new JButton("Remove");
            remove.addActionListener(e -> profileManager.removeProfile());
            footerPanel.add(remove);

            JButton add = new JButton("Add");
            add.addActionListener(e -> profileManager.addProfile());
            footerPanel.add(add);

            JButton reload = new JButton("Reload");
            reload.addActionListener(e -> reloadEverything());
            footerPanel.add(reload);

            mainPanel.revalidate();
        });

        if (frame != null) frame.setContentPane(mainPanel);
        ComponentManager.addStateChangeListener(this);
    }

    public JFrame getParentFrame() {
        return parentFrame;
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public int addCategory(ComponentCategory category, boolean isEnabled) {
        int index;
        if (!categoryIndexMap.containsKey(category.getDisplayName())) {
            index = addRow(category.getItemComponents(), category.getPreviewPanel(), isEnabled);
            categoryIndexMap.put(category.getDisplayName(), index);

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
            });
        } else {
            index = categoryIndexMap.get(category.getDisplayName());
            clearRow(index);
            setPreviewPanel(index, category.getPreviewPanel());
            addComponents(index, category.getItemComponents());
        }
        return index;
    }

    public int addCategory(ComponentCategory category) {
        return addCategory(category, true);
    }

    public void removeCategory(String categoryName) {
        if (categoryIndexMap.containsKey(categoryName)) {
            int index = categoryIndexMap.get(categoryName);
            removeRow(index);
            headerPanel.remove(findCheckBoxByName(categoryName));
            headerPanel.revalidate();

            categoryIndexMap.remove(categoryName);
        }
    }

    public void enableCategory(String categoryName) {
        Utils.postEvent(PeerEvent.LOW_PRIORITY_EVENT, () -> {
            JCheckBox cb = findCheckBoxByName(categoryName);
            if (cb == null) return;
            if (!cb.isSelected()) actionPerformed(new ActionEvent(cb, 0, cb.getText()));
            cb.setSelected(true);
        });
    }

    public void disableCategory(String categoryName) {
        Utils.postEvent(PeerEvent.LOW_PRIORITY_EVENT, () -> {
            JCheckBox cb = findCheckBoxByName(categoryName);
            if (cb == null) return;
            if (cb.isSelected()) actionPerformed(new ActionEvent(cb, 0, cb.getText()));
            cb.setSelected(false);
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
    }

    @Override
    public void loaded(String type, String displayName, boolean enabled, List<net.gabor6505.java.pcbuilder.components.Component> affectedComponents) {
        addCategory(new ComponentCategory(type, displayName, affectedComponents), enabled);
    }

    @Override
    public void reloaded(String type, String displayName, boolean enabled, List<net.gabor6505.java.pcbuilder.components.Component> affectedComponents) {
        addCategory(new ComponentCategory(type, displayName, affectedComponents), enabled);
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

    public void reloadEverything() {
        removeAllRows();
        headerPanel.removeAll();
        categoryIndexMap.clear();

        //profileManager.reload();
        TypeManager.reload();
        ComponentManager.reload();
    }
}
