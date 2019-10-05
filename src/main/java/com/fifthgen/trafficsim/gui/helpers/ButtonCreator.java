
package com.fifthgen.trafficsim.gui.helpers;

import com.fifthgen.trafficsim.localization.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;

public final class ButtonCreator {

    public static JButton getJButton(String imageName, String command, String altString, ActionListener listener) {
        JButton button;

        if (imageName.equals("")) {
            button = new JButton(altString);
            button.setPreferredSize(new Dimension(42, 42));
        } else {
            URL url = ClassLoader.getSystemResource(imageName);
            if (url != null) {
                button = new JButton(new ImageIcon(url));
            } else {
                button = new JButton(altString);
                button.setPreferredSize(new Dimension(42, 42));
            }
        }
        button.setFocusPainted(false);
        button.setToolTipText(altString);
        button.setActionCommand(command);
        button.addActionListener(listener);
        return button;
    }

    public static JButton getJButton(String imageName, String command, String altString, boolean resize, ActionListener listener) {
        JButton button;

        if (imageName.equals("")) {
            button = new JButton(altString);
            button.setPreferredSize(new Dimension(42, 42));
        } else {
            URL url = ClassLoader.getSystemResource(imageName);
            if (url != null) {
                ImageIcon tmpImg = new ImageIcon(url);
                button = new JButton(tmpImg);
                button.setPreferredSize(new Dimension(tmpImg.getIconWidth(), tmpImg.getIconHeight()));
                button.setMaximumSize(new Dimension(tmpImg.getIconWidth(), tmpImg.getIconHeight()));
            } else {
                button = new JButton(altString);
                button.setPreferredSize(new Dimension(42, 42));
            }
        }
        button.setFocusPainted(false);
        button.setToolTipText(altString);
        button.setActionCommand(command);
        button.addActionListener(listener);
        return button;
    }
}