package com.fifthgen.trafficsim.gui.helpers;

import com.fifthgen.trafficsim.scenario.events.Event;

import javax.swing.*;
import java.awt.*;

public final class EventJListRenderer extends JPanel implements ListCellRenderer {

    private static final long serialVersionUID = -4716099862947417497L;

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel pane = new JPanel();
        JLabel line1 = new JLabel();
        JLabel line2 = new JLabel();
        Event event = (Event) value;
        line1.setText(event.getTime() + " ms");
        line2.setText(event.getText());

        line1.setForeground(new Color(0, 0, 0));
        line2.setForeground(event.getTextColor());

        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.add(line1);
        pane.add(line2);

        if (isSelected) {
            pane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
            pane.setBackground(new Color(230, 230, 230));
        } else pane.setBackground(super.getBackground());
        return pane;
    }
}