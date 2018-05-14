package net.gabor6505.java.pcbuilder.elements;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

// TODO write documentation
public class ScrollPane2D extends JScrollPane implements MouseWheelListener, ComponentListener {

    private final ScrollablePanel outerPanel;
    private final List<JScrollPane> innerScrollPanes = new ArrayList<>();

    public ScrollPane2D(int windowWidth, int windowHeight) {
        super();
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setSize(windowWidth, windowHeight);
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
        JScrollPane innerScrollPane = new JScrollPane(innerPanel);
        innerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        innerScrollPane.setWheelScrollingEnabled(false);
        innerScrollPane.addComponentListener(this);
        innerScrollPane.addMouseWheelListener(this);

        // Fill in the horizontal scrollable panel with the components specified, aligning in X axis
        for (JComponent comp : components) {
            innerPanel.add(comp);
        }

        outerPanel.add(innerScrollPane);
        innerScrollPanes.add(innerScrollPane);
        return innerScrollPanes.indexOf(innerScrollPane);
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
        ((JPanel) innerScrollPanes.get(index).getViewport().getView()).add(comp);
    }

    public void addComponents(int index, List<JComponent> components) {
        JPanel innerPanel = (JPanel) innerScrollPanes.get(index).getViewport().getView();
        for (JComponent comp : components) {
            innerPanel.add(comp);
        }
    }

    public void clearRow(int index) {
        JPanel innerPanel = (JPanel) innerScrollPanes.get(index).getViewport().getView();
        innerPanel.removeAll();
    }

    public void removeRow(int index) {
        outerPanel.remove(innerScrollPanes.get(index));
    }

    public void removeAllRows() {
        outerPanel.removeAll();
    }

    // Scroll outer pane when shift is not down, and inner pane when shift is down
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        JScrollPane innerScrollPane = (JScrollPane) e.getComponent();
        if (e.isShiftDown()) {
            innerScrollPane.setWheelScrollingEnabled(true);
            innerScrollPane.dispatchEvent(e);
        } else {
            innerScrollPane.setWheelScrollingEnabled(false);
            dispatchEvent(e);
        }
    }

    // Handle the hiding of scroll bars when they are not needed
    @Override
    public void componentResized(ComponentEvent e) {
        JScrollPane pane = (JScrollPane) e.getComponent();
        JScrollPane parentPane = (JScrollPane) e.getComponent().getParent().getParent().getParent();
        int subtractValue = 20;

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
}
