package net.gabor6505.java.pcbuilder.elements;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ButtonGroup extends javax.swing.ButtonGroup {

    private final boolean selectFirst;

    private final List<AbstractButton> buttons = new ArrayList<>();
    private final List<ButtonGroupListener> listeners = new ArrayList<>();

    public ButtonGroup() {
        selectFirst = false;
    }

    public ButtonGroup(boolean selectFirstButton) {
        selectFirst = selectFirstButton;
    }

    @Override
    public void add(AbstractButton button) {
        if (button == null) return;
        super.add(button);
        buttons.add(button);

        if (buttons.indexOf(button) == 0 && selectFirst) {
            button.setSelected(true);
        }
    }

    @Override
    public void remove(AbstractButton button) {
        if (buttons.indexOf(button) < 0) return;
        super.remove(button);
        buttons.remove(button);
    }

    public AbstractButton getSelected() {
        for (AbstractButton button : buttons) {
            if (button.isSelected()) return button;
        }
        return null;
    }

    public int getSelectedIndex() {
        AbstractButton button = getSelected();
        if (button == null) return -1;
        else return buttons.indexOf(button);
    }

    public void setSelectedIndex(int index) {
        if (buttons.size() <= index) return;

        AbstractButton button = buttons.get(index);
        button.setSelected(true);
    }

    @Override
    public void setSelected(ButtonModel m, boolean b) {
        super.setSelected(m, b);

        AbstractButton currentlySelected = getSelected();
        if (currentlySelected == null) return;

        for (ButtonGroupListener l : listeners) {
            l.buttonSelected(currentlySelected, buttons.indexOf(currentlySelected));
        }
    }

    public void addButtonGroupListener(ButtonGroupListener listener) {
        listeners.add(listener);
    }

    public void removeButtonGroupListener(ButtonGroupListener listener) {
        listeners.remove(listener);
    }

    public interface ButtonGroupListener {
        void buttonSelected(AbstractButton button, int index);
    }
}
