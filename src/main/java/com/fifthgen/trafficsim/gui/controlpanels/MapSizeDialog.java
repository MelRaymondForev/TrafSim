package com.fifthgen.trafficsim.gui.controlpanels;

import com.fifthgen.trafficsim.Application;
import com.fifthgen.trafficsim.gui.helpers.ButtonCreator;
import com.fifthgen.trafficsim.localization.Messages;
import com.fifthgen.trafficsim.map.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public final class MapSizeDialog extends JDialog implements ActionListener {

    private final JFormattedTextField widthTextField_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private final JFormattedTextField heightTextField_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private final JFormattedTextField regionWidthTextField_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private final JFormattedTextField regionHeightTextField_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private final CyclicBarrier barrier_;
    private int mapWidth_;
    private int mapHeight_;
    private int regionWidth_;
    private int regionHeight_;

    public MapSizeDialog(int mapWidth, int mapHeight, int regionWidth, int regionHeight, CyclicBarrier barrier) {
        super(Application.getMainFrame(), Messages.getString("MapSize.title"), true);

        mapWidth_ = 5000;
        mapHeight_ = 5000;
        regionWidth_ = 5000;
        regionHeight_ = 5000;
        barrier_ = barrier;
        setUndecorated(true);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setModal(true);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.insets = new Insets(5, 5, 5, 5);
        c.gridwidth = 2;

        add(new JLabel(Messages.getString("MapSize.mapCreatedWith")), c);
        c.gridwidth = 1;

        ++c.gridy;
        add(new JLabel(Messages.getString("MapSize.mapWidth")), c);
        c.gridx = 1;
        widthTextField_.setValue(mapWidth);
        add(widthTextField_, c);
        c.gridx = 0;

        ++c.gridy;
        add(new JLabel(Messages.getString("MapSize.mapHeight")), c);
        c.gridx = 1;
        heightTextField_.setValue(mapHeight);
        add(heightTextField_, c);
        c.gridx = 0;

        ++c.gridy;
        add(new JLabel(Messages.getString("MapSize.regionWidth")), c);
        c.gridx = 1;
        regionWidthTextField_.setValue(regionWidth);
        add(regionWidthTextField_, c);
        c.gridx = 0;

        ++c.gridy;
        add(new JLabel(Messages.getString("MapSize.regionHeight")), c);
        c.gridx = 1;
        regionHeightTextField_.setValue(regionHeight);
        add(regionHeightTextField_, c);
        c.gridx = 0;

        ++c.gridy;
        c.fill = GridBagConstraints.NONE;

        add(ButtonCreator.getJButton("ok.png", "OK", Messages.getString("MapSize.OK"), this), c);
        pack();
        setLocationRelativeTo(Application.getMainFrame());
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        setVisible(false);
        mapWidth_ = Math.max(mapWidth_, ((Number) widthTextField_.getValue()).intValue());
        mapHeight_ = Math.max(mapHeight_, ((Number) heightTextField_.getValue()).intValue());
        regionWidth_ = Math.max(1000, ((Number) regionWidthTextField_.getValue()).intValue());
        regionHeight_ = Math.max(1000, ((Number) regionHeightTextField_.getValue()).intValue());
        Map.getInstance().initNewMap(mapWidth_, mapHeight_, regionWidth_, regionHeight_);
        try {
            barrier_.await(2, TimeUnit.SECONDS);
        } catch (Exception e2) {
        }
        dispose();
    }
}