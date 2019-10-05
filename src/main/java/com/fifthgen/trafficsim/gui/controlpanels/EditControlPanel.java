package com.fifthgen.trafficsim.gui.controlpanels;

import com.fifthgen.trafficsim.Application;
import com.fifthgen.trafficsim.gui.Renderer;
import com.fifthgen.trafficsim.gui.helpers.ButtonCreator;
import com.fifthgen.trafficsim.localization.Messages;
import com.fifthgen.trafficsim.map.OSM.OSMLoader;
import com.fifthgen.trafficsim.scenario.messages.Message;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class EditControlPanel extends JPanel implements ActionListener {

    private final JComboBox<String> editChoice_;
    private final JPanel editPanel_;
    private final JPanel editCardPanel_;
    private final JTabbedPane tabbedPane_;
    private final EditVehicleControlPanel editVehiclePanel_ = new EditVehicleControlPanel();
    private final EditOneVehicleControlPanel editOneVehiclePanel_ = new EditOneVehicleControlPanel();
    private final EditTrafficLightsControlPanel editTrafficLightsPanel_ = new EditTrafficLightsControlPanel();
    private boolean editMode_ = true;

    public EditControlPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 1;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.insets = new Insets(5, 5, 5, 5);

        add(ButtonCreator.getJButton("importOSM.png", "importOSM",
                Messages.getString("EditControlPanel.importOSM"), this), c);
        c.gridx = 1;

        ++c.gridy;

        editPanel_ = new JPanel();
        editPanel_.setLayout(new BorderLayout(0, 5));
        String[] choices = {Messages.getString("EditControlPanel.trafficLights"),
                Messages.getString("EditControlPanel.vehicles")};

        editChoice_ = new JComboBox<String>(choices);
        editChoice_.setSelectedIndex(0);
        editChoice_.setMaximumRowCount(100);
        editChoice_.addActionListener(this);
        editPanel_.add(editChoice_, BorderLayout.PAGE_START);
        editCardPanel_ = new JPanel(new CardLayout());
        editCardPanel_.setOpaque(false);
        editCardPanel_.add(editTrafficLightsPanel_, "trafficLights");
        editTrafficLightsPanel_.setOpaque(false);

        tabbedPane_ = new JTabbedPane();
        tabbedPane_.setOpaque(false);
        editVehiclePanel_.setOpaque(false);
        editOneVehiclePanel_.setOpaque(false);
        tabbedPane_.add(Messages.getString("EditVehiclesControlPanel.vehicle1"), editVehiclePanel_);
        tabbedPane_.add(Messages.getString("EditVehiclesControlPanel.vehicle2"), editOneVehiclePanel_);
        editCardPanel_.add(tabbedPane_, "vehicles");
        editPanel_.add(editCardPanel_, BorderLayout.PAGE_END);

        editPanel_.setOpaque(false);

        ++c.gridy;
        add(editPanel_, c);

        c.weighty = 1.0;
        ++c.gridy;

        JPanel pane = new JPanel();
        pane.setOpaque(false);
        add(pane, c);

        init();
    }

    private void init() {
        if (Renderer.getInstance().getTimePassed() > 0) {

        } else {
            editMode_ = true;
            if (editChoice_.getSelectedItem().equals(Messages.getString("EditControlPanel.vehicles"))) {
                Renderer.getInstance().setShowVehicles(true);
                Renderer.getInstance().ReRender(true, false);
            } else if (editChoice_.getSelectedItem().equals(Messages.getString("EditControlPanel.trafficLights"))) {
                Renderer.getInstance().setHighlightNodes(true);
                Renderer.getInstance().ReRender(false, false);
            }
            editPanel_.setVisible(true);
        }
    }

    public void receiveMouseEvent(int x, int y) {
        String item = (String) editChoice_.getSelectedItem();
        if (item.equals(Messages.getString("EditControlPanel.vehicles")) &&
                tabbedPane_.getTitleAt(tabbedPane_.getSelectedIndex()).equals(Messages.getString("EditVehiclesControlPanel.vehicle2"))) {
            editOneVehiclePanel_.receiveMouseEvent(x, y);
        } else if (item.equals(Messages.getString("EditControlPanel.trafficLights"))) {
            editTrafficLightsPanel_.receiveMouseEvent(x, y);
        }
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        if ("importOSM".equals(command)) {
            Application.getMainControlPanel().changeFileChooser(true, true, true);
            int returnVal = Application.getMainControlPanel().getFileChooser().showOpenDialog(Application.getMainFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Runnable job = () -> OSMLoader.getInstance().loadOSM(Application.getMainControlPanel().getFileChooser().getSelectedFile());
                new Thread(job).start();
            }
        } else if ("comboBoxChanged".equals(command)) {
            String item = (String) editChoice_.getSelectedItem();
            CardLayout cl = (CardLayout) (editCardPanel_.getLayout());

            Renderer.getInstance().setMarkedVehicle(null);
            Renderer.getInstance().ReRender(true, false);
            if (Messages.getString("EditControlPanel.vehicles").equals(item)) {
                cl.show(editCardPanel_, "vehicles");
                Renderer.getInstance().setHighlightNodes(false);
                Renderer.getInstance().setShowAllBlockings(false);
                Renderer.getInstance().setShowVehicles(true);
                Renderer.getInstance().ReRender(true, false);

                editOneVehiclePanel_.getAddNote().setForeground(Color.black);
                editOneVehiclePanel_.getAddNote().setText(Messages.getString("EditOneVehicleControlPanel.noteAdd"));
                stateChanged(null);
            } else if (Messages.getString("EditControlPanel.trafficLights").equals(item)) {
                cl.show(editCardPanel_, "trafficLights");
                Renderer.getInstance().setHighlightNodes(true);
                Renderer.getInstance().ReRender(true, false);
            }
        }
    }

    public void stateChanged(ChangeEvent arg0) {
        Renderer.getInstance().setMarkedVehicle(null);

        if (editChoice_.getSelectedItem() != null)
            if (editChoice_.getSelectedItem().toString().equals(Messages.getString("EditControlPanel.vehicles")) &&
                    tabbedPane_.getTitleAt(tabbedPane_.getSelectedIndex()).equals(Messages.getString("EditVehiclesControlPanel.vehicle2"))) {
                Renderer.getInstance().setMarkedVehicle(null);
                Renderer.getInstance().setShowVehicles(true);
            }

        Renderer.getInstance().ReRender(false, false);
    }
}