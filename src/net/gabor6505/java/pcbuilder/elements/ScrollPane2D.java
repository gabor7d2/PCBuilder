package net.gabor6505.java.pcbuilder.elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// TODO write documentation
// TODO set darker hover color on JScrollBars on MacOS
// TODO reimplement arrow key navigation properly
public class ScrollPane2D extends JScrollPane implements MouseWheelListener, MouseListener, /*KeyListener,*/ ComponentListener {

    private final ScrollablePanel outerPanel;
    private final List<ScrollPanel> innerScrollPanels = new ArrayList<>();

    private JScrollPane hoveredInnerScrollPane = null;

    // Handle scrolling of outer panel
    private final MouseWheelListener outerWheelListener = e -> {
        //System.out.println("Outer scroll pane needs scrolling");
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            getVerticalScrollBar().setValue(getVerticalScrollBar().getValue() + e.getWheelRotation() * 32);
        } else {
            getVerticalScrollBar().setValue(getVerticalScrollBar().getValue() + e.getWheelRotation() * 8);
        }
    };

    public ScrollPane2D(int windowWidth, int windowHeight) {
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setSize(windowWidth, windowHeight);
        setWheelScrollingEnabled(false);
        setBorder(null);

        addMouseWheelListener(outerWheelListener);
        //addKeyListener(this);

        // Setup outer scrollable panel
        outerPanel = new ScrollablePanel();
        outerPanel.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
        outerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 8, 0, Color.LIGHT_GRAY));

        // Set outer panel as the outer scroll pane's (this) viewport
        setViewportView(outerPanel);
        getViewport().setBackground(Color.LIGHT_GRAY);
        setFocusable(true);
        requestFocusInWindow();
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
        innerScrollPanel.getScrollPane().addMouseListener(this);
        innerScrollPanel.getScrollPane().getViewport().setBackground(Color.WHITE);

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

    public int addRow(List<JComponent> components, JPanel previewPanel) {
        int index = addRow(components);
        setPreviewPanel(index, previewPanel);
        return index;
    }

    public int addRow(JComponent component, JPanel previewPanel) {
        int index = addRow(component);
        setPreviewPanel(index, previewPanel);
        return index;
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
    }

    public void setPreviewPanel(int index, JPanel panel) {
        innerScrollPanels.get(index).setPreviewPanel(panel);
    }

    // Handle scrolling of inner and outer panel
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        JScrollPane innerScrollPane = (JScrollPane) e.getComponent();
        if (e.isShiftDown() || !getVerticalScrollBar().isVisible()) {
            //System.out.println("Inner scroll pane needs scrolling");
            innerScrollPane.getHorizontalScrollBar().setValue(innerScrollPane.getHorizontalScrollBar().getValue() + e.getWheelRotation() * 8);
        } else {
            //System.out.println("Outer scroll pane needs scrolling");
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                getVerticalScrollBar().setValue(getVerticalScrollBar().getValue() + e.getWheelRotation() * 32);
            } else {
                getVerticalScrollBar().setValue(getVerticalScrollBar().getValue() + e.getWheelRotation() * 8);
            }
        }

        /*if (e.isShiftDown() || !getVerticalScrollBar().isVisible()) {
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
        }*/
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

    /*@Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (hoveredInnerScrollPane != null) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_RIGHT:
                    hoveredInnerScrollPane.getHorizontalScrollBar().setValue(hoveredInnerScrollPane.getHorizontalScrollBar().getValue() + 32);
                    break;
                case KeyEvent.VK_LEFT:
                    hoveredInnerScrollPane.getHorizontalScrollBar().setValue(hoveredInnerScrollPane.getHorizontalScrollBar().getValue() - 32);
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }*/

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        hoveredInnerScrollPane = (JScrollPane) e.getComponent();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        hoveredInnerScrollPane = null;
    }

    public class ScrollPanel extends JPanel {

        private JPanel previewPanel;
        private final JScrollPane scrollPane;

        public ScrollPanel(JScrollPane scrollPane, JPanel previewPanel) {
            super();
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setBorder(BorderFactory.createMatteBorder(8, 8, 0, 8, Color.LIGHT_GRAY));

            this.previewPanel = previewPanel;

            if (this.previewPanel != null) {
                add(this.previewPanel);
            }

            this.scrollPane = scrollPane;
            this.scrollPane.setBorder(null);
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
