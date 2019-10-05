package com.fifthgen.trafficsim.gui.controlpanels;

import com.fifthgen.trafficsim.Application;
import com.fifthgen.trafficsim.gui.Renderer;
import com.fifthgen.trafficsim.gui.helpers.ButtonCreator;
import com.fifthgen.trafficsim.gui.helpers.ReRenderManager;
import com.fifthgen.trafficsim.localization.Messages;
import com.fifthgen.trafficsim.map.Map;
import com.fifthgen.trafficsim.scenario.Scenario;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;

public final class SimulateControlPanel extends JPanel implements ActionListener, ChangeListener, ItemListener {

    private final JSlider zoomSlider_;
    private final JPanel startStopJPanel_;
    private boolean dontReRenderZoom_ = false;
    private int mode_ = 0;

    public SimulateControlPanel() {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.insets = new Insets(5, 5, 5, 5);

        JLabel jLabel1 = new JLabel("<html><b>" + Messages.getString("SimulateControlPanel.mapControl") + "</b></html>");
        ++c.gridy;
        add(jLabel1, c);

        JPanel panning = new JPanel();
        panning.setLayout(new GridBagLayout());
        GridBagConstraints c1 = new GridBagConstraints();
        c1.weightx = 0.5;
        c1.gridx = 1;
        c1.gridy = 0;
        c1.gridheight = 1;
        panning.add(ButtonCreator.getJButton("up.png", "up",
                Messages.getString("SimulateControlPanel.upButton"), this), c1);
        c1.gridx = 0;
        c1.gridy = 1;
        panning.add(ButtonCreator.getJButton("left.png", "left",
                Messages.getString("SimulateControlPanel.leftButton"), this), c1);
        c1.gridx = 2;
        c1.gridy = 1;
        panning.add(ButtonCreator.getJButton("right.png", "right",
                Messages.getString("SimulateControlPanel.rightButton"), this), c1);
        c1.gridx = 1;
        c1.gridy = 2;
        panning.add(ButtonCreator.getJButton("down.png", "down",
                Messages.getString("SimulateControlPanel.downButton"), this), c1);
        ++c.gridy;
        add(panning, c);

        jLabel1 = new JLabel("<html><b>" + Messages.getString("SimulateControlPanel.zoom") + "</b></html>");
        ++c.gridy;
        add(jLabel1, c);

        zoomSlider_ = getZoomSlider();
        ++c.gridy;
        add(zoomSlider_, c);

        jLabel1 = new JLabel("<html><b>" + Messages.getString("SimulateControlPanel.simulation") + "</b></html>");
        ++c.gridy;
        add(jLabel1, c);

        startStopJPanel_ = new JPanel(new CardLayout());
        startStopJPanel_.add(ButtonCreator.getJButton("start.png", "start",
                Messages.getString("SimulateControlPanel.start"), this), "start");
        startStopJPanel_.add(ButtonCreator.getJButton("pause.png", "pause",
                Messages.getString("SimulateControlPanel.pause"), this), "pause");
        ++c.gridy;
        c.gridwidth = 1;
        add(startStopJPanel_, c);

        c.gridx = 1;
        add(ButtonCreator.getJButton("onestep.png", "onestep",
                Messages.getString("SimulateControlPanel.onestep"), this), c);
    }

    public void setSimulationStop() {
        CardLayout cl = (CardLayout) (startStopJPanel_.getLayout());
        cl.show(startStopJPanel_, "start");
    }

    public void setZoomValue(int zoom) {
        dontReRenderZoom_ = true;
        zoomSlider_.setValue(zoom);
    }

    private JSlider getZoomSlider() {
        JSlider slider = new JSlider(-75, 212, 1);
        Hashtable<Integer, JLabel> ht = new Hashtable<Integer, JLabel>();

        ht.put(-75, new JLabel("3km"));
        ht.put(-20, new JLabel("1km"));
        ht.put(45, new JLabel("200m"));
        ht.put(96, new JLabel("100m"));
        ht.put(157, new JLabel("30m"));
        ht.put(212, new JLabel("10m"));
        slider.setLabelTable(ht);
        slider.setPaintLabels(true);
        slider.setMinorTickSpacing(10);
        slider.setMajorTickSpacing(40);

        slider.addChangeListener(this);
        return slider;
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ("up".equals(command)) {
            Renderer.getInstance().pan('u');
            ReRenderManager.getInstance().doReRender();
        } else if ("down".equals(command)) {
            Renderer.getInstance().pan('d');
            ReRenderManager.getInstance().doReRender();
        } else if ("left".equals(command)) {
            Renderer.getInstance().pan('l');
            ReRenderManager.getInstance().doReRender();
        } else if ("right".equals(command)) {
            Renderer.getInstance().pan('r');
            ReRenderManager.getInstance().doReRender();
        } else if ("loadmap".equals(command)) {
            Application.getMainControlPanel().changeFileChooser(true, true, false);
            int returnVal = Application.getMainControlPanel().getFileChooser().showOpenDialog(Application.getMainFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Runnable job = () -> Map.getInstance().load(Application.getMainControlPanel().getFileChooser().getSelectedFile(), false);
                new Thread(job).start();
            }
        } else if ("loadscenario".equals(command)) {
            Application.getMainControlPanel().changeFileChooser(true, true, false);
            int returnVal = Application.getMainControlPanel().getFileChooser().showOpenDialog(Application.getMainFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Runnable job = () -> Scenario.getInstance().load(Application.getMainControlPanel().getFileChooser().getSelectedFile(), false);
                new Thread(job).start();
            }
        } else if ("pause".equals(command)) {
            CardLayout cl = (CardLayout) (startStopJPanel_.getLayout());
            cl.show(startStopJPanel_, "start");
            Runnable job = () -> Application.getSimulationMaster().stopThread();
            new Thread(job).start();
        } else if ("start".equals(command)) {

            CardLayout cl = (CardLayout) (startStopJPanel_.getLayout());
            cl.show(startStopJPanel_, "pause");
            Application.getSimulationMaster().startThread();
        } else if ("onestep".equals(command)) {
            Runnable job = () -> Application.getSimulationMaster().doOneStep();

            new Thread(job).start();
        }
    }

    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            int value = source.getValue();
            double scale = Math.exp(value / 50.0) / 1000;
            if (dontReRenderZoom_) dontReRenderZoom_ = false;
            else {
                Renderer.getInstance().setMapZoom(scale);
                ReRenderManager.getInstance().doReRender();
            }
        }
    }

    public void itemStateChanged(ItemEvent e) {
        boolean state;
        state = e.getStateChange() == ItemEvent.SELECTED;

        ReRenderManager.getInstance().doReRender();
    }

    public void startSimulation() {
        CardLayout cl = (CardLayout) (startStopJPanel_.getLayout());
        cl.show(startStopJPanel_, "pause");
        Application.getSimulationMaster().startThread();
    }

    public void stopSimulation() {
        CardLayout cl = (CardLayout) (startStopJPanel_.getLayout());
        cl.show(startStopJPanel_, "start");

        Runnable job = () -> Application.getSimulationMaster().stopThread();

        new Thread(job).start();
    }

    public void toggleSimulationStatus() {
        if (Application.getSimulationMaster().isSimulationRunning()) stopSimulation();
        else startSimulation();
    }
}