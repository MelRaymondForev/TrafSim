package com.fifthgen.trafficsim.gui.controlpanels;

import com.fifthgen.trafficsim.gui.Renderer;
import com.fifthgen.trafficsim.gui.helpers.ButtonCreator;
import com.fifthgen.trafficsim.gui.helpers.TextAreaLabel;
import com.fifthgen.trafficsim.gui.helpers.VehicleType;
import com.fifthgen.trafficsim.gui.helpers.VehicleTypeXML;
import com.fifthgen.trafficsim.localization.Messages;
import com.fifthgen.trafficsim.map.Map;
import com.fifthgen.trafficsim.map.Region;
import com.fifthgen.trafficsim.routing.WayPoint;
import com.fifthgen.trafficsim.scenario.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayDeque;

public class EditOneVehicleControlPanel extends JPanel implements ActionListener, MouseListener {

    private static final long serialVersionUID = 8669978113870090221L;

    private final JFormattedTextField speed_;

    private final JFormattedTextField commDist_;

    private final JFormattedTextField wait_;

    private final JFormattedTextField brakingRate_;

    private final JFormattedTextField accelerationRate_;

    private final JFormattedTextField timeDistance_;

    private final JFormattedTextField politeness_;

    private final JFormattedTextField deviationFromSpeedLimit_;

    private final JFormattedTextField vehicleLength_;

    private final JPanel colorPreview_;

    private final JSpinner waypointAmount_;

    private final JLabel waypointAmountLabel_;

    private final JSpinner vehicleAmount_;

    private final JLabel vehicleAmountLabel_;

    JRadioButton addItem_;

    JRadioButton editItem_;

    JRadioButton deleteItem_;

    TextAreaLabel addNote_;

    TextAreaLabel saveNote_;

    TextAreaLabel deleteNote_;

    JPanel space_;

    private JLabel chooseVehicleTypeLabel_;

    private JComboBox<VehicleType> chooseVehicleType_;

    private JComboBox<Vehicle> chooseVehicle_;

    private ArrayDeque<WayPoint> destinations = null;

    private JButton createVehicle_;

    private JButton deleteVehicle_;

    private JButton deleteAllVehicles_;

    public EditOneVehicleControlPanel() {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;

        ButtonGroup group = new ButtonGroup();
        addItem_ = new JRadioButton(Messages.getString("EditOneVehicleControlPanel.add"));
        addItem_.setActionCommand("add");
        addItem_.addActionListener(this);
        addItem_.setSelected(true);
        group.add(addItem_);
        ++c.gridy;
        add(addItem_, c);

        editItem_ = new JRadioButton(Messages.getString("EditOneVehicleControlPanel.edit"));
        editItem_.setActionCommand("edit");
        editItem_.addActionListener(this);
        //group.add(editItem_);
        //++c.gridy;
       // add(editItem_, c);

        deleteItem_ = new JRadioButton(Messages.getString("EditOneVehicleControlPanel.delete"));
        deleteItem_.setActionCommand("delete");
        deleteItem_.setSelected(true);
        deleteItem_.addActionListener(this);
        group.add(deleteItem_);
        ++c.gridy;
        add(deleteItem_, c);

        c.gridwidth = 1;
        c.insets = new Insets(5, 5, 5, 5);

        chooseVehicleTypeLabel_ = new JLabel(Messages.getString("EditOneVehicleControlPanel.selectVehicleType"));
        ++c.gridy;
        add(chooseVehicleTypeLabel_, c);
        chooseVehicleType_ = new JComboBox<VehicleType>();
        chooseVehicleType_.setName("chooseVehicleType");

        refreshVehicleTypes();

        chooseVehicleType_.addActionListener(this);
        c.gridx = 1;
        add(chooseVehicleType_, c);

        c.gridx = 0;
        JLabel label = new JLabel(Messages.getString("EditOneVehicleControlPanel.speed"));
        ++c.gridy;
        add(label, c);
        speed_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        speed_.setValue(100);
        getSpeed().setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(getSpeed(), c);

        c.gridx = 0;
        label = new JLabel(Messages.getString("EditOneVehicleControlPanel.commDistance"));
        ++c.gridy;
        add(label, c);
        commDist_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        commDist_.setValue(100);
        getCommDist().setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(getCommDist(), c);

        c.gridx = 0;
        label = new JLabel(Messages.getString("EditOneVehicleControlPanel.waittime"));
        ++c.gridy;
        add(label, c);
        wait_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        wait_.setValue(10);
        getWait().setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(getWait(), c);

        c.gridx = 0;
        label = new JLabel(Messages.getString("EditOneVehicleControlPanel.brakingRate"));
        ++c.gridy;
        add(label, c);
        brakingRate_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        brakingRate_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(brakingRate_, c);

        c.gridx = 0;
        label = new JLabel(Messages.getString("EditOneVehicleControlPanel.accelerationRate"));
        ++c.gridy;
        add(label, c);
        accelerationRate_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        accelerationRate_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(accelerationRate_, c);

        c.gridx = 0;
        label = new JLabel(Messages.getString("EditOneVehicleControlPanel.timeDistance"));
        ++c.gridy;
        add(label, c);
        timeDistance_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        timeDistance_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(timeDistance_, c);

        c.gridx = 0;
        label = new JLabel(Messages.getString("EditOneVehicleControlPanel.politeness"));
        ++c.gridy;
        add(label, c);
        politeness_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        politeness_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(politeness_, c);

        c.gridx = 0;
        label = new JLabel(Messages.getString("EditVehicleControlPanel.deviationFromSpeedLimit"));
        ++c.gridy;
        add(label, c);
        deviationFromSpeedLimit_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        deviationFromSpeedLimit_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(deviationFromSpeedLimit_, c);

        c.gridx = 0;
        label = new JLabel(Messages.getString("EditOneVehicleControlPanel.vehicleLength"));
        ++c.gridy;
        add(label, c);
        vehicleLength_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        vehicleLength_.setPreferredSize(new Dimension(60, 20));
        c.gridx = 1;
        add(vehicleLength_, c);

        c.gridx = 0;
        label = new JLabel(Messages.getString("EditOneVehicleControlPanel.color"));
        ++c.gridy;
        add(label, c);
        colorPreview_ = new JPanel();
        getColorPreview().setBackground(Color.black);
        getColorPreview().setSize(10, 10);
        getColorPreview().addMouseListener(this);
        c.gridx = 1;
        add(getColorPreview(), c);

        c.gridx = 0;
        waypointAmountLabel_ = new JLabel(Messages.getString("EditOneVehicleControlPanel.waypointAmount"));
        ++c.gridy;
        add(waypointAmountLabel_, c);
        waypointAmount_ = new JSpinner();
        waypointAmount_.setValue(2);
        c.gridx = 1;
        add(waypointAmount_, c);

        c.gridx = 0;
        vehicleAmountLabel_ = new JLabel(Messages.getString("EditOneVehicleControlPanel.vehicleAmount"));
        ++c.gridy;
        add(vehicleAmountLabel_, c);
        vehicleAmount_ = new JSpinner();
        vehicleAmount_.setValue(1);
        c.gridx = 1;
        add(vehicleAmount_, c);

        c.gridx = 0;
        c.gridwidth = 2;
        ++c.gridy;
        createVehicle_ = ButtonCreator.getJButton("oneVehicle.png", "vehicleAction",
                Messages.getString("EditOneVehicleControlPanel.vehicleAction"), this);
        add(createVehicle_, c);

        c.gridx = 0;
        ++c.gridy;
        deleteVehicle_ = ButtonCreator.getJButton("deleteVehicles.png", "deleteVehicle",
                Messages.getString("EditOneVehicleControlPanel.deleteVehicle"), this);
        add(deleteVehicle_, c);
        deleteVehicle_.setVisible(false);

        c.gridx = 0;
        c.gridwidth = 2;
        ++c.gridy;
        deleteAllVehicles_ = ButtonCreator.getJButton("deleteAll.png", "clearVehicles",
                Messages.getString("EditOneVehicleControlPanel.btnClearVehicles"), this);
        add(deleteAllVehicles_, c);

        addNote_ = new TextAreaLabel(Messages.getString("EditOneVehicleControlPanel.noteAdd"));
        ++c.gridy;
        c.gridx = 0;
        add(addNote_, c);

        saveNote_ = new TextAreaLabel(Messages.getString("EditOneVehicleControlPanel.noteSave"));
        ++c.gridy;
        c.gridx = 0;
        add(saveNote_, c);
        saveNote_.setVisible(false);

        deleteNote_ = new TextAreaLabel(Messages.getString("EditOneVehicleControlPanel.noteDelete"));
        ++c.gridy;
        c.gridx = 0;
        add(deleteNote_, c);
        deleteNote_.setVisible(false);

        c.weighty = 1.0;
        ++c.gridy;
        space_ = new JPanel();
        space_.setOpaque(false);
        add(space_, c);

        actionPerformed(new ActionEvent(chooseVehicleType_, 0, "comboBoxChanged"));
    }

    public void receiveMouseEvent(int x, int y) {

        if (editItem_.isSelected() || deleteItem_.isSelected()) {

            chooseVehicle_.removeActionListener(this);
            chooseVehicle_.removeAllItems();
            chooseVehicle_.setVisible(false);
            Renderer.getInstance().setMarkedVehicle(null);

            Region[][] Regions = Map.getInstance().getRegions();
            int Region_max_x = Map.getInstance().getRegionCountX();
            int Region_max_y = Map.getInstance().getRegionCountY();
            int i, j;
            for (i = 0; i < Region_max_x; ++i) {
                for (j = 0; j < Region_max_y; ++j) {
                    Vehicle[] vehiclesArray = Regions[i][j].getVehicleArray();
                    for (int k = 0; k < vehiclesArray.length; ++k) {
                        Vehicle vehicle = vehiclesArray[k];

                        if (editItem_.isSelected()) {
                            if (vehicle.getX() > (x - 300) && vehicle.getX() < (x + 300) && vehicle.getY() > (y - 300) &&
                                    vehicle.getY() < (y + 300)) {
                                chooseVehicle_.addItem(vehicle);
                                chooseVehicle_.setVisible(true);
                            }
                        }

                        if (vehicle.getX() > (x - 100) && vehicle.getX() < (x + 100) && vehicle.getY() > (y - 100) &&
                                vehicle.getY() < (y + 100)) {
                            Renderer.getInstance().setMarkedVehicle(vehicle);

                            if (editItem_.isSelected()) {
                                speed_.setValue((int) Math.round(vehicle.getMaxSpeed() / (100000.0 / 3600)));
                                vehicleLength_.setValue(vehicle.getVehicleLength());
                                commDist_.setValue(Math.round(vehicle.getMaxCommDistance() / 100));
                                wait_.setValue(vehicle.getWaittime());
                                colorPreview_.setBackground(vehicle.getColor());
                                brakingRate_.setValue(vehicle.getBrakingRate());
                                accelerationRate_.setValue(vehicle.getAccelerationRate());
                                timeDistance_.setValue(vehicle.getTimeDistance());
                                politeness_.setValue(vehicle.getPoliteness());
                                deviationFromSpeedLimit_.setValue((int) Math.round(vehicle.getSpeedDeviation_() / (100000.0 / 3600)));
                                chooseVehicle_.setSelectedItem(vehicle);
                            } else {
                                Map.getInstance().delVehicle(Renderer.getInstance().getMarkedVehicle());
                                if (Renderer.getInstance().getMarkedVehicle().equals(Renderer.getInstance().getAttackedVehicle()))
                                    Renderer.getInstance().setAttackedVehicle(null);
                                if (Renderer.getInstance().getMarkedVehicle().equals(Renderer.getInstance().getAttackerVehicle()))
                                    Renderer.getInstance().setAttackerVehicle(null);
                                Renderer.getInstance().setMarkedVehicle(null);

                            }

                        }

                    }
                }

                if (Renderer.getInstance().getMarkedVehicle() == null && chooseVehicle_.getItemCount() != 0) {
                    Renderer.getInstance().setMarkedVehicle(chooseVehicle_.getItemAt(0));

                    actionPerformed(new ActionEvent(chooseVehicle_, 0, "comboBoxChanged"));
                }
            }
            chooseVehicle_.addActionListener(this);
            Renderer.getInstance().ReRender(false, false);
        } else if (destinations != null) {
            WayPoint tmpWayPoint;
            try {
                tmpWayPoint = new WayPoint(x, y, ((Number) wait_.getValue()).intValue());
                destinations.add(tmpWayPoint);

                if (destinations.size() == ((Number) waypointAmount_.getValue()).intValue()) addVehicle();
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null,
                        Messages.getString("EditOneVehicleControlPanel.MsgBoxCreateWaypointError"),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

        }
    }

    private void addVehicle() {
        Vehicle tmpVehicle;
        int timeBetween = 0;

        if (((Number) vehicleAmount_.getValue()).intValue() > 1)
            timeBetween = Integer.parseInt(JOptionPane.showInputDialog(Messages.getString("EditOneVehicleControlPanel.MsgBoxVehicleAmount")));
        try {
            for (int i = 0; i < ((Number) vehicleAmount_.getValue()).intValue(); i++) {
                destinations.peekFirst().setWaittime(i * timeBetween * 1000 + ((Number) wait_.getValue()).intValue());
                tmpVehicle = new Vehicle(destinations, ((Number) vehicleLength_.getValue()).intValue(),
                        (int) Math.round(((Number) speed_.getValue()).intValue() * 100000.0 / 3600),
                        ((Number) commDist_.getValue()).intValue() * 100,
                        ((Number) brakingRate_.getValue()).intValue(),
                        ((Number) accelerationRate_.getValue()).intValue(),
                        ((Number) timeDistance_.getValue()).intValue(),
                        ((Number) politeness_.getValue()).intValue(),
                        (int) Math.round(((Number) deviationFromSpeedLimit_.getValue()).intValue() * 100000.0 / 3600),
                        getColorPreview().getBackground());
                Map.getInstance().addVehicle(tmpVehicle);
                Renderer.getInstance().setMarkedVehicle(tmpVehicle);
            }

            destinations = null;
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null,
                    Messages.getString("EditOneVehicleControlPanel.MsgBoxCreateVehicleError"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        Renderer.getInstance().ReRender(true, false);

        addNote_.setForeground(Color.black);
        addNote_.setText(Messages.getString("EditOneVehicleControlPanel.noteAdd"));
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ("add".equals(command)) {
            destinations = null;
            Renderer.getInstance().setMarkedVehicle(null);
            setGuiElements("add");
            actionPerformed(new ActionEvent(chooseVehicleType_, 0, "comboBoxChanged"));
            Renderer.getInstance().ReRender(false, false);
        }

        if ("edit".equals(command)) {
            Renderer.getInstance().setMarkedVehicle(null);
            setGuiElements("save");
            Renderer.getInstance().ReRender(false, false);

            addNote_.setForeground(Color.black);
            addNote_.setText(Messages.getString("EditOneVehicleControlPanel.noteAdd"));
        }

        if ("delete".equals(command)) {
            Renderer.getInstance().setMarkedVehicle(null);
            setGuiElements("delete");
            Renderer.getInstance().ReRender(false, false);

            addNote_.setForeground(Color.black);
            addNote_.setText(Messages.getString("EditOneVehicleControlPanel.noteAdd"));
        }

        if ("vehicleAction".equals(command) && addItem_.isSelected()) {

            if (((Number) waypointAmount_.getValue()).intValue() > 1) {
                addNote_.setForeground(Color.red);
                addNote_.setText(Messages.getString("EditOneVehicleControlPanel.MsgCreateVehicle"));
                destinations = new ArrayDeque<WayPoint>(((Number) waypointAmount_.getValue()).intValue());
            } else {
                JOptionPane.showMessageDialog(null,
                        Messages.getString("EditOneVehicleControlPanel.MsgBoxCreateVehicleWaypointAmountError"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if ("vehicleAction".equals(command) && editItem_.isSelected()) {
            Renderer.getInstance().setShowVehicles(true);

            Vehicle tmpVehicle = Renderer.getInstance().getMarkedVehicle();

            if (tmpVehicle != null && !getSpeed().getValue().equals("") && !getCommDist().getValue().equals("") && !getWait().getValue().equals("")) {
                tmpVehicle.setMaxSpeed((int) Math.round(((Number) getSpeed().getValue()).intValue() * 100000.0 / 3600));
                tmpVehicle.setVehicleLength(((Number) vehicleLength_.getValue()).intValue());
                tmpVehicle.setMaxCommDistance(((Number) getCommDist().getValue()).intValue() * 100);
                tmpVehicle.setCurWaitTime(((Number) getWait().getValue()).intValue());
                tmpVehicle.setAccelerationRate(((Number) getAccelerationRate().getValue()).intValue());
                tmpVehicle.setTimeDistance(((Number) timeDistance_.getValue()).intValue());
                tmpVehicle.setPoliteness(((Number) politeness_.getValue()).intValue());
                tmpVehicle.setSpeedDeviation_((int) Math.round(((Number) deviationFromSpeedLimit_.getValue()).intValue() * 100000.0 / 3600));
                tmpVehicle.setBrakingRate(((Number) getBrakingRate().getValue()).intValue());
                tmpVehicle.setColor(colorPreview_.getBackground());

                JOptionPane.showMessageDialog(null,
                        Messages.getString("EditOneVehicleControlPanel.MsgBoxSavedText"),
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        Messages.getString("EditOneVehicleControlPanel.MsgBoxNOTSavedText"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if ("deleteVehicle".equals(command)) {

            if (Renderer.getInstance().getMarkedVehicle() != null) {
                if (chooseVehicle_.getItemCount() > 1) {
                    Map.getInstance().delVehicle(Renderer.getInstance().getMarkedVehicle());
                    chooseVehicle_.removeItem(Renderer.getInstance().getMarkedVehicle());
                    Renderer.getInstance().setMarkedVehicle((Vehicle) chooseVehicle_.getSelectedItem());
                } else {
                    chooseVehicle_.removeActionListener(this);
                    chooseVehicle_.removeAllItems();
                    chooseVehicle_.setVisible(false);
                    Map.getInstance().delVehicle(Renderer.getInstance().getMarkedVehicle());
                    Renderer.getInstance().setMarkedVehicle(null);
                    chooseVehicle_.addActionListener(this);
                }

                Renderer.getInstance().ReRender(false, false);
            } else {
                JOptionPane.showMessageDialog(null,
                        Messages.getString("EditOneVehicleControlPanel.MsgBoxDeleteVehicle"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if ("comboBoxChanged".equals(command)) {

            if (((Component) e.getSource()).getName().equals("chooseVehicle")) {
                Vehicle tmpVehicle = (Vehicle) chooseVehicle_.getSelectedItem();
                Renderer.getInstance().setMarkedVehicle(tmpVehicle);

                if (tmpVehicle != null) {
                    getSpeed().setValue((int) Math.round(tmpVehicle.getMaxSpeed() / (100000.0 / 3600)));
                    vehicleLength_.setValue(tmpVehicle.getVehicleLength());
                    commDist_.setValue(Math.round(tmpVehicle.getMaxCommDistance() / 100));
                    wait_.setValue(tmpVehicle.getWaittime());
                    brakingRate_.setValue(tmpVehicle.getBrakingRate());
                    accelerationRate_.setValue(tmpVehicle.getAccelerationRate());
                    timeDistance_.setValue(tmpVehicle.getTimeDistance());
                    politeness_.setValue(tmpVehicle.getPoliteness());
                    deviationFromSpeedLimit_.setValue((int) Math.round(tmpVehicle.getSpeedDeviation_() / (100000.0 / 3600)));
                    colorPreview_.setBackground(tmpVehicle.getColor());
                    Renderer.getInstance().ReRender(false, false);
                }
            } else if (((Component) e.getSource()).getName().equals("chooseVehicleType")) {
                VehicleType tmpVehicleType = (VehicleType) chooseVehicleType_.getSelectedItem();

                if (tmpVehicleType != null) {
                    speed_.setValue((int) Math.round((tmpVehicleType.getMaxSpeed() / (100000.0 / 3600) + tmpVehicleType.getMinSpeed() / (100000.0 / 3600)) / 2));
                    vehicleLength_.setValue(tmpVehicleType.getVehicleLength());
                    commDist_.setValue(Math.round((tmpVehicleType.getMaxCommDist() / 100 + tmpVehicleType.getMinCommDist() / 100) / 2));
                    wait_.setValue(Math.round((tmpVehicleType.getMaxWaittime() + tmpVehicleType.getMinWaittime()) / 2));
                    brakingRate_.setValue((Math.round(tmpVehicleType.getMaxBrakingRate() + tmpVehicleType.getMinBrakingRate()) / 2));
                    accelerationRate_.setValue(Math.round((tmpVehicleType.getMaxAccelerationRate() + tmpVehicleType.getMinAccelerationRate()) / 2));
                    timeDistance_.setValue(Math.round((tmpVehicleType.getMaxTimeDistance() + tmpVehicleType.getMinTimeDistance()) / 2));
                    politeness_.setValue(Math.round((tmpVehicleType.getMaxPoliteness() + tmpVehicleType.getMinPoliteness()) / 2));
                    deviationFromSpeedLimit_.setValue((int) Math.round(tmpVehicleType.getDeviationFromSpeedLimit_() / (100000.0 / 3600)));
                    colorPreview_.setBackground(new Color(tmpVehicleType.getColor()));
                }

            }
        } else if ("clearVehicles".equals(command)) {
            if (JOptionPane.showConfirmDialog(null,
                    Messages.getString("EditOneVehicleControlPanel.msgBoxClearAll"),
                    "", JOptionPane.YES_NO_OPTION) == 0) {
                Map.getInstance().clearVehicles();
                Renderer.getInstance().ReRender(true, false);
            }
        }
    }

    public void refreshVehicleTypes() {
        chooseVehicleType_.removeActionListener(this);
        chooseVehicleType_.removeAllItems();
        VehicleTypeXML xml = new VehicleTypeXML(null);
        for (VehicleType type : xml.getVehicleTypes()) {
            chooseVehicleType_.addItem(type);
        }
        if (chooseVehicleType_.getItemCount() > 0) {
            chooseVehicleType_.setVisible(true);
            chooseVehicleTypeLabel_.setVisible(true);
        }
        chooseVehicleType_.addActionListener(this);
    }

    public void setGuiElements(String command) {
        for (Object o : this.getComponents()) {
            ((Component) o).setVisible(true);
        }
        if (command.equals("add")) {
            if (chooseVehicleType_.getItemCount() < 1) {
                chooseVehicleType_.setVisible(false);
                chooseVehicleTypeLabel_.setVisible(false);
            }
            chooseVehicle_.setVisible(false);
            saveNote_.setVisible(false);
            deleteNote_.setVisible(false);
            deleteVehicle_.setVisible(false);
        } else if (command.equals("save")) {
            chooseVehicle_.setVisible(false);
            chooseVehicleType_.setVisible(false);
            chooseVehicleTypeLabel_.setVisible(false);
            waypointAmount_.setVisible(false);
            waypointAmountLabel_.setVisible(false);
            vehicleAmount_.setVisible(false);
            vehicleAmountLabel_.setVisible(false);
            addNote_.setVisible(false);
            deleteNote_.setVisible(false);
        } else if (command.equals("delete")) {
            for (Object o : this.getComponents()) {
                ((Component) o).setVisible(false);
            }
            addItem_.setVisible(true);
            editItem_.setVisible(true);
            deleteItem_.setVisible(true);
            deleteNote_.setVisible(true);
            deleteAllVehicles_.setVisible(true);
            space_.setVisible(true);
        }
    }

    public JFormattedTextField getWait() {
        return wait_;
    }

    public JFormattedTextField getSpeed() {
        return speed_;
    }

    public JFormattedTextField getCommDist() {
        return commDist_;
    }

    public JPanel getColorPreview() {
        return colorPreview_;
    }

    public JFormattedTextField getBrakingRate() {
        return brakingRate_;
    }

    public JFormattedTextField getAccelerationRate() {
        return accelerationRate_;
    }

    public TextAreaLabel getAddNote() {
        return addNote_;
    }

    public void mouseClicked(MouseEvent e) {
        Color color = JColorChooser.showDialog(this, Messages.getString("EditOneVehicleControlPanel.color"), colorPreview_.getBackground());

        if (color == null) colorPreview_.setBackground(Color.black);
        else colorPreview_.setBackground(color);
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

}