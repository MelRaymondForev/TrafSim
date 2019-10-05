package com.fifthgen.trafficsim.gui.helpers;

import com.fifthgen.trafficsim.Application;
import com.fifthgen.trafficsim.localization.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class ProgressOverlay extends JDialog implements ActionListener {

    public ProgressOverlay() {
        super(Application.getMainFrame());
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true);
        setLayout(new BorderLayout());
        add(progressBar, BorderLayout.PAGE_START);
        add(ButtonCreator.getJButton("shutdown.png", "shutdown", Messages.getString("ProgressOverlay.quitProgram"), this), BorderLayout.PAGE_END);
        pack();
        setVisible(false);
    }

    public void setVisible(boolean state) {
        if (state) {
            Application.getMainFrame().setEnabled(false);
            Point p = Application.getMainFrame().getLocationOnScreen();
            setLocation((Application.getMainFrame().getBounds().width - getBounds().width) / 2 + p.x, (Application.getMainFrame().getBounds().height - getBounds().height) / 2 + p.y);
        } else Application.getMainFrame().setEnabled(true);

        super.setVisible(state);
    }

    public void actionPerformed(ActionEvent e) {
        System.exit(ABORT);
    }
}