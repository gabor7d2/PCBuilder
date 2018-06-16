package net.gabor6505.java.pcbuilder.elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class AutoScrollPane extends JScrollPane implements ComponentListener, SwingConstants {

    private final int orientation;
    private final JPanel contentPanel;

    public AutoScrollPane(int orientation, LayoutManager layout) {
        this.orientation = orientation;

        if (orientation == HORIZONTAL) {
            setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        }
        setBorder(null);
        addComponentListener(this);

        contentPanel = new JPanel();
        contentPanel.setLayout(layout);
        setViewportView(contentPanel);
        revalidate();
    }

    public AutoScrollPane(int orientation, boolean useBoxLayout) {
        this(orientation, new FlowLayout());
        if (!useBoxLayout) return;
        contentPanel.setLayout(new BoxLayout(contentPanel, orientation == HORIZONTAL ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS));
    }

    public AutoScrollPane(int orientation) {
        this(orientation, new FlowLayout());
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    // Handle the hiding of scroll bars when they are not needed
    @Override
    public void componentResized(ComponentEvent e) {
        JScrollPane pane = (JScrollPane) e.getComponent();

        if (orientation == HORIZONTAL) {
            if (pane.getSize().width >= pane.getPreferredSize().width) {
                pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                pane.getHorizontalScrollBar().setVisible(false);
            } else {
                pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            }
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
