package net.gabor6505.java.pcbuilder.elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class ComparisonPane extends ScrollPane2D implements ActionListener {

    private final Map<String, Integer> categoryIndexMap = new HashMap<>();
    private final Map<Integer, ComponentCategory> categoryMap = new HashMap<>();

    private final JPanel mainPanel;
    private final JPanel headerPanel;

    public ComparisonPane(int windowWidth, int windowHeight, JFrame frame) {
        super(windowWidth, windowHeight);

        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBackground(Color.DARK_GRAY);
        headerPanel.setBorder(BorderFactory.createMatteBorder(8, 4, 8, 4, Color.DARK_GRAY));

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(this);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        if (frame != null) frame.setContentPane(mainPanel);
    }

    public ComparisonPane(int windowWidth, int windowHeight) {
        this(windowWidth, windowHeight, null);
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public int addCategory(ComponentCategory category) {
        int index;
        if (!categoryIndexMap.containsKey(category.getName())) {
            index = addRow(category.getItemComponents(), category.getPreviewPanel());
            categoryIndexMap.put(category.getName(), index);
            categoryMap.put(index, category);

            JCheckBox checkBox = new JCheckBox(category.getName());
            checkBox.setSelected(true);
            checkBox.setForeground(Color.WHITE);
            checkBox.setBackground(Color.DARK_GRAY);
            checkBox.setBorder(BorderFactory.createMatteBorder(0, 8, 0, 8, Color.DARK_GRAY));
            checkBox.addActionListener(this);
            headerPanel.add(checkBox);
        } else {
            index = categoryIndexMap.get(category.getName());
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

            categoryIndexMap.remove(categoryName);
            categoryMap.remove(index);
        }
    }

    public void enableCategory(String categoryName) {
        JCheckBox cb = findCheckBoxByName(categoryName);
        if (cb == null) return;
        if (!cb.isSelected()) actionPerformed(new ActionEvent(cb, 0, cb.getText()));
        cb.setSelected(true);
    }

    public void disableCategory(String categoryName) {
        JCheckBox cb = findCheckBoxByName(categoryName);
        if (cb == null) return;
        if (cb.isSelected()) actionPerformed(new ActionEvent(cb, 0, cb.getText()));
        cb.setSelected(false);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        int index = categoryIndexMap.get(e.getActionCommand());
        toggleRowVisible(index);
    }
}
