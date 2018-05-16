package net.gabor6505.java.pcbuilder.elements;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// TODO write documentation
public class ScrollPane2D extends JScrollPane implements MouseWheelListener, ComponentListener {

    private final ScrollablePanel outerPanel;
    private final List<ScrollPanel> innerScrollPanels = new ArrayList<>();

    public ScrollPane2D(int windowWidth, int windowHeight) {
        super();
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setSize(windowWidth, windowHeight);
        setBorder(null);
        //addMouseWheelListener(this);

        // Setup outer scrollable panel
        outerPanel = new ScrollablePanel();
        outerPanel.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));

        // Set outer panel as the outer scroll pane's (this) viewport
        setViewportView(outerPanel);
    }

    public int addRow(List<JComponent> components) {
        // Setup inner scrollable panel
        ScrollablePanel innerPanel = new ScrollablePanel();
        innerPanel.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.NONE);
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));

        // Setup inner scroll pane with inner panel in its viewport
        ScrollPanel innerScrollPanel = new ScrollPanel(new JScrollPane(innerPanel), null);
        innerScrollPanel.getScrollPane().setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        innerScrollPanel.getScrollPane().setWheelScrollingEnabled(false);
        innerScrollPanel.getScrollPane().addComponentListener(this);
        innerScrollPanel.getScrollPane().addMouseWheelListener(this);

        // Fill in the horizontal scrollable panel with the components specified, aligning in X axis
        for (JComponent comp : components) {
            innerPanel.add(comp);
        }

        outerPanel.add(innerScrollPanel);
        innerScrollPanels.add(innerScrollPanel);
        return innerScrollPanels.indexOf(innerScrollPanel);
    }

    public int addRow(JComponent component) {
        int index = addRow();
        addComponent(index, component);
        return index;
    }

    public int addRow() {
        return addRow(new ArrayList<>());
    }

    public void addComponent(int index, JComponent comp) {
        ((JPanel) innerScrollPanels.get(index).getScrollPane().getViewport().getView()).add(comp);
    }

    public void addComponents(int index, List<JComponent> components) {
        JPanel innerPanel = (JPanel) innerScrollPanels.get(index).getScrollPane().getViewport().getView();
        for (JComponent comp : components) {
            innerPanel.add(comp);
        }
    }

    public void clearRow(int index) {
        JPanel innerPanel = (JPanel) innerScrollPanels.get(index).getScrollPane().getViewport().getView();
        innerPanel.removeAll();
    }

    public void removeRow(int index) {
        outerPanel.remove(innerScrollPanels.get(index));
    }

    public void removeAllRows() {
        outerPanel.removeAll();
    }

    public void setPreviewPanel(int index, JPanel panel) {
        innerScrollPanels.get(index).setPreviewPanel(panel);
    }

    // Scroll outer pane when shift is not down, and inner pane when shift is down
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        JScrollPane innerScrollPane = (JScrollPane) e.getComponent();

        if (e.isShiftDown() || !getVerticalScrollBar().isVisible()) {
            //System.out.println("Inner scroll pane needs scrolling");
            if (System.getProperty("os.name").toLowerCase().startsWith("mac os")) {
                innerScrollPane.getHorizontalScrollBar().setValue(innerScrollPane.getHorizontalScrollBar().getValue() + e.getWheelRotation() * 8);
            } else {
                innerScrollPane.setWheelScrollingEnabled(true);
                dispatchEvent(e);
            }
        } else {
            //System.out.println("Outer scroll pane needs scrolling");
            if (System.getProperty("os.name").toLowerCase().startsWith("mac os")) {
                getVerticalScrollBar().setValue(getVerticalScrollBar().getValue() + e.getWheelRotation() * 8);
            } else {
                innerScrollPane.setWheelScrollingEnabled(false);
                dispatchEvent(e);
            }
        }
    }

    // Handle the hiding of scroll bars when they are not needed
    @Override
    public void componentResized(ComponentEvent e) {
        JScrollPane pane = (JScrollPane) e.getComponent();
        ScrollPanel panel = (ScrollPanel) e.getComponent().getParent();
        JScrollPane parentPane = (JScrollPane) e.getComponent().getParent().getParent().getParent().getParent();
        int subtractValue = 18 + panel.getPreviewPanelWidth();

        // Check if vertical scrollbar is not visible
        if (!parentPane.getVerticalScrollBar().isVisible()) {
            subtractValue -= parentPane.getVerticalScrollBar().getWidth();
        }

        // TODO hide horizontal scrollbars after checking if the vertical scrollbar wouldn't be there, they would fit
        // Check if we need the horizontal scrollbar of this horizontal scroll pane
        if (parentPane.getWidth() - subtractValue > pane.getViewport().getView().getWidth()) {
            pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            pane.getHorizontalScrollBar().setVisible(false);
        } else {
            pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    public class ScrollPanel extends JPanel {

        private JPanel previewPanel;
        private final JScrollPane scrollPane;

        public ScrollPanel(JScrollPane scrollPane, JPanel previewPanel) {
            super();
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            this.previewPanel = previewPanel;

            if (this.previewPanel != null) {
                add(this.previewPanel);
            }

            this.scrollPane = scrollPane;
            add(scrollPane);
        }

        public boolean hasPreviewPanel() {
            return previewPanel != null;
        }

        public JPanel getPreviewPanel() {
            return previewPanel;
        }

        public JScrollPane getScrollPane() {
            return scrollPane;
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

        public int getPreviewPanelWidth() {
            if (previewPanel == null) return 0;
            else return previewPanel.getWidth();
        }
    }
}