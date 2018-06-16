package net.gabor6505.java.pcbuilder.elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// TODO write documentation
public class ScrollPane2D extends JScrollPane implements MouseWheelListener, MouseListener {

    private final ScrollablePanel outerPanel;
    private final List<ScrollPanel> innerScrollPanels = new ArrayList<>();

    // Inner panel's wheel listener
    private final MouseWheelListener innerWheelListener = e -> {
        JScrollPane innerScrollPane = (JScrollPane) e.getComponent();
        int wheelRotation;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            wheelRotation = e.getWheelRotation() * 32;
        } else {
            wheelRotation = e.getWheelRotation() * 8;
        }

        if ((e.isShiftDown() || !getVerticalScrollBar().isVisible()) && innerScrollPane.getHorizontalScrollBar().isVisible()) {
            innerScrollPane.getHorizontalScrollBar().setValue(innerScrollPane.getHorizontalScrollBar().getValue() + wheelRotation);
        } else {
            getVerticalScrollBar().setValue(getVerticalScrollBar().getValue() + wheelRotation);
        }
    };

    public ScrollPane2D(int windowWidth, int windowHeight) {
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setSize(windowWidth, windowHeight);
        setWheelScrollingEnabled(false);
        addMouseWheelListener(this);
        setBorder(null);

        // Setup outer scrollable panel
        outerPanel = new ScrollablePanel();
        outerPanel.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
        outerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 8, 0, Color.LIGHT_GRAY));

        // Set outer panel as the outer scroll pane's (this) viewport
        setViewportView(outerPanel);
        getViewport().setBackground(Color.LIGHT_GRAY);
        revalidate();
    }

    public int addRow(List<JComponent> components, boolean isEnabled) {
        // Setup inner scroll pane
        ScrollPanel innerScrollPanel = new ScrollPanel(null);
        innerScrollPanel.getScrollPane().setWheelScrollingEnabled(false);
        innerScrollPanel.getScrollPane().addMouseWheelListener(innerWheelListener);

        innerScrollPanel.getScrollPane().getViewport().setBackground(Color.WHITE);
        innerScrollPanel.getContentPanel().setBackground(Color.WHITE);

        // Fill in the horizontal scrollable panel with the components specified, aligning in X axis
        for (JComponent comp : components) {
            comp.addMouseListener(this);
            innerScrollPanel.getContentPanel().add(comp);
        }

        innerScrollPanel.setVisible(false);

        outerPanel.add(innerScrollPanel);
        innerScrollPanels.add(innerScrollPanel);

        if (isEnabled) {
            EventQueue.invokeLater(() -> innerScrollPanel.setVisible(true));
        }

        return innerScrollPanels.indexOf(innerScrollPanel);
    }

    public int addRow(List<JComponent> components) {
        return addRow(components, true);
    }

    public int addRow(JComponent component) {
        int index = addRow();
        addComponent(index, component);
        return index;
    }

    public int addRow() {
        return addRow(new ArrayList<>());
    }

    public int addRow(List<JComponent> components, JPanel previewPanel, boolean isEnabled) {
        int index = addRow(components, isEnabled);
        setPreviewPanel(index, previewPanel);
        return index;
    }

    public int addRow(List<JComponent> components, JPanel previewPanel) {
        return addRow(components, previewPanel, true);
    }

    public int addRow(JComponent component, JPanel previewPanel) {
        int index = addRow(component);
        setPreviewPanel(index, previewPanel);
        return index;
    }

    public void addComponent(int index, JComponent comp) {
        ((JPanel) innerScrollPanels.get(index).getScrollPane().getViewport().getView()).add(comp);
        innerScrollPanels.get(index).getScrollPane().getViewport().getView().revalidate();
    }

    public void addComponents(int index, List<JComponent> components) {
        JPanel innerPanel = (JPanel) innerScrollPanels.get(index).getScrollPane().getViewport().getView();
        for (JComponent comp : components) {
            innerPanel.add(comp);
        }
        innerScrollPanels.get(index).getScrollPane().getViewport().getView().revalidate();
    }

    public void clearRow(int index) {
        JPanel innerPanel = (JPanel) innerScrollPanels.get(index).getScrollPane().getViewport().getView();
        innerPanel.removeAll();
    }

    public void removeRow(int index) {
        outerPanel.remove(innerScrollPanels.get(index));
        innerScrollPanels.remove(index);
        outerPanel.revalidate();
    }

    public void setRowVisible(int index, boolean visible) {
        outerPanel.getComponent(index).setVisible(visible);
    }

    public boolean isRowVisible(int index) {
        return outerPanel.getComponent(index).isVisible();
    }

    public void toggleRowVisible(int index) {
        setRowVisible(index, !isRowVisible(index));
    }

    public void removeAllRows() {
        outerPanel.removeAll();
        innerScrollPanels.clear();
    }

    public void setPreviewPanel(int index, JPanel panel) {
        innerScrollPanels.get(index).setPreviewPanel(panel);
    }

    // Outer panel's wheel listener
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            getVerticalScrollBar().setValue(getVerticalScrollBar().getValue() + e.getWheelRotation() * 32);
        } else {
            getVerticalScrollBar().setValue(getVerticalScrollBar().getValue() + e.getWheelRotation() * 8);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Called when the mouse entered one of the horizontal scrollPane's contentPanel's one of the JPanels
     *
     * Override this to do something with the component that the cursor entered into
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Called when the mouse exited one of the horizontal scrollPane's contentPanel's one of the JPanels
     *
     * Override this to do something with the component that the cursor exited from
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }

    public class ScrollPanel extends JPanel {

        private JPanel previewPanel;
        private final AutoScrollPane scrollPane;

        public ScrollPanel(JPanel previewPanel) {
            super();
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setBorder(BorderFactory.createMatteBorder(8, 8, 0, 8, Color.LIGHT_GRAY));

            this.previewPanel = previewPanel;

            if (this.previewPanel != null) {
                add(this.previewPanel);
            }

            this.scrollPane = new AutoScrollPane(SwingConstants.HORIZONTAL, true);
            this.scrollPane.setBorder(null);
            add(scrollPane);
        }

        public boolean hasPreviewPanel() {
            return previewPanel != null;
        }

        public JPanel getPreviewPanel() {
            return previewPanel;
        }

        public AutoScrollPane getScrollPane() {
            return scrollPane;
        }

        public JPanel getContentPanel() {
            return scrollPane.getContentPanel();
        }

        public void setPreviewPanel(JPanel previewPanel) {
            this.previewPanel = previewPanel;
            removeAll();
            add(previewPanel);
            add(scrollPane);
        }

        public void removePreviewPanel() {
            this.previewPanel = null;
            removeAll();
            add(scrollPane);
        }
    }
}
