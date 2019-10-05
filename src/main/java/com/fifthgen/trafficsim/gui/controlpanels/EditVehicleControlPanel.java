package com.fifthgen.trafficsim.gui.controlpanels;

import com.fifthgen.trafficsim.Application;
import com.fifthgen.trafficsim.gui.Renderer;
import com.fifthgen.trafficsim.gui.helpers.ButtonCreator;
import com.fifthgen.trafficsim.gui.helpers.TextAreaLabel;
import com.fifthgen.trafficsim.gui.helpers.VehicleType;
import com.fifthgen.trafficsim.gui.helpers.VehicleTypeXML;
import com.fifthgen.trafficsim.localization.Messages;
import com.fifthgen.trafficsim.map.Map;
import com.fifthgen.trafficsim.routing.WayPoint;
import com.fifthgen.trafficsim.scenario.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

public class EditVehicleControlPanel extends JPanel implements ActionListener, MouseListener {

    private static final long serialVersionUID = 1347869556374738481L;

    private static JButton createButton_;

    private static JButton deleteButton_;

    private static JButton scenarioApplyButton_;

    private static ArrayList<JButton> buttonList_ = new ArrayList<JButton>();

    private final JFormattedTextField vehicleLength_;

    private final JFormattedTextField minSpeed_;

    private final JFormattedTextField maxSpeed_;

    private final JFormattedTextField minCommDist_;

    private final JFormattedTextField maxCommDist_;

    private final JFormattedTextField minWait_;

    private final JFormattedTextField maxWait_;

    private final JFormattedTextField minBraking_;

    private final JFormattedTextField maxBraking_;

    private final JFormattedTextField minAcceleration_;

    private final JFormattedTextField maxAcceleration_;

    private final JFormattedTextField minTimeDistance_;

    private final JFormattedTextField maxTimeDistance_;

    private final JFormattedTextField minPoliteness_;

    private final JFormattedTextField maxPoliteness_;

    private final JFormattedTextField wiFi_;

    private final JFormattedTextField emergencyVehicle_;

    private final JFormattedTextField fakingVehicle_;

    private final JFormattedTextField amount_;

    private final JFormattedTextField speedStreetRestriction_;

    private final JFormattedTextField vehiclesDeviatingMaxSpeed_;

    private final JFormattedTextField deviationFromSpeedLimit_;

    private final JPanel colorPreview_;

    private JLabel chooseVehicleTypeLabel_;

    private JComboBox<VehicleType> chooseVehicleType_;

    private JComboBox<String> fakeMessagesTypes_;

    public EditVehicleControlPanel() {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
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
        JLabel jLabel1 = new JLabel();
        add(jLabel1, c);
        minSpeed_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        minSpeed_.setPreferredSize(new Dimension(60, 20));
        minSpeed_.setVisible(false);
        c.gridx = 1;
        add(minSpeed_, c);
        c.gridx = 2;

        c.gridheight = 2;
        JButton button = new JButton();
        button.setActionCommand("speed");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        maxSpeed_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        maxSpeed_.setPreferredSize(new Dimension(60, 20));
        maxSpeed_.setVisible(false);
        c.gridx = 1;
        add(maxSpeed_, c);

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        minCommDist_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        minCommDist_.setPreferredSize(new Dimension(60, 20));
        minCommDist_.setVisible(false);
        c.gridx = 1;
        add(minCommDist_, c);

        c.gridx = 2;
        c.gridheight = 2;
        button = new JButton();
        button.setActionCommand("communication distance");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        maxCommDist_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        maxCommDist_.setPreferredSize(new Dimension(60, 20));
        maxCommDist_.setVisible(false);
        c.gridx = 1;
        add(maxCommDist_, c);

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        minWait_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        minWait_.setPreferredSize(new Dimension(60, 20));
        minWait_.setVisible(false);
        c.gridx = 1;
        add(minWait_, c);

        c.gridx = 2;
        c.gridheight = 2;
        button = new JButton();
        button.setActionCommand("wait time");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        maxWait_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        maxWait_.setPreferredSize(new Dimension(60, 20));
        maxWait_.setVisible(false);
        c.gridx = 1;
        add(maxWait_, c);

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        minBraking_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        minBraking_.setPreferredSize(new Dimension(60, 20));
        minBraking_.setVisible(false);
        c.gridx = 1;
        add(minBraking_, c);

        c.gridx = 2;
        c.gridheight = 2;
        button = new JButton();
        button.setActionCommand("braking rate");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        maxBraking_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        maxBraking_.setPreferredSize(new Dimension(60, 20));
        maxBraking_.setVisible(false);
        c.gridx = 1;
        add(maxBraking_, c);

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        minAcceleration_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        minAcceleration_.setPreferredSize(new Dimension(60, 20));
        minAcceleration_.setVisible(false);
        c.gridx = 1;
        add(minAcceleration_, c);

        c.gridx = 2;
        c.gridheight = 2;
        button = new JButton();
        button.setActionCommand("acceleration");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        maxAcceleration_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        maxAcceleration_.setPreferredSize(new Dimension(60, 20));
        maxAcceleration_.setVisible(false);
        c.gridx = 1;
        add(maxAcceleration_, c);

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        minTimeDistance_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        minTimeDistance_.setPreferredSize(new Dimension(60, 20));
        minTimeDistance_.setVisible(false);
        c.gridx = 1;
        add(minTimeDistance_, c);

        c.gridx = 2;
        c.gridheight = 2;
        button = new JButton();
        button.setActionCommand("time distance");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        maxTimeDistance_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        maxTimeDistance_.setPreferredSize(new Dimension(60, 20));
        maxTimeDistance_.setVisible(false);
        c.gridx = 1;
        add(maxTimeDistance_, c);

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        minPoliteness_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        minPoliteness_.setPreferredSize(new Dimension(60, 20));
        minPoliteness_.setVisible(false);
        c.gridx = 1;
        add(minPoliteness_, c);

        c.gridx = 2;
        c.gridheight = 2;
        button = new JButton();
        button.setActionCommand("politeness");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        maxPoliteness_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        maxPoliteness_.setPreferredSize(new Dimension(60, 20));
        maxPoliteness_.setVisible(false);
        c.gridx = 1;
        add(maxPoliteness_, c);

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        vehiclesDeviatingMaxSpeed_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        vehiclesDeviatingMaxSpeed_.setPreferredSize(new Dimension(60, 20));
        vehiclesDeviatingMaxSpeed_.setVisible(false);
        c.gridx = 1;
        add(vehiclesDeviatingMaxSpeed_, c);

        c.gridx = 2;
        c.gridheight = 1;
        button = new JButton();
        button.setActionCommand("vehicles deviating speed");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        deviationFromSpeedLimit_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        deviationFromSpeedLimit_.setPreferredSize(new Dimension(60, 20));
        deviationFromSpeedLimit_.setVisible(false);
        c.gridx = 1;
        add(deviationFromSpeedLimit_, c);

        c.gridx = 2;
        c.gridheight = 1;
        button = new JButton();
        button.setActionCommand("speed deviation");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        vehicleLength_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        vehicleLength_.setPreferredSize(new Dimension(60, 20));
        vehicleLength_.setVisible(false);
        c.gridx = 1;
        add(vehicleLength_, c);

        c.gridx = 2;
        c.gridheight = 1;
        button = new JButton();
        button.setActionCommand("length");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        wiFi_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        wiFi_.setPreferredSize(new Dimension(60, 20));
        wiFi_.setValue(100);
        wiFi_.setVisible(false);
        c.gridx = 1;
        add(wiFi_, c);

        c.gridx = 2;
        c.gridheight = 1;
        button = new JButton();
        button.setActionCommand("wifi amount");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        emergencyVehicle_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        emergencyVehicle_.setPreferredSize(new Dimension(60, 20));
        emergencyVehicle_.setValue(0);
        emergencyVehicle_.setVisible(false);
        c.gridx = 1;
        add(emergencyVehicle_, c);

        c.gridx = 2;
        c.gridheight = 1;
        button = new JButton();
        button.setActionCommand("emergency amount");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        fakingVehicle_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        fakingVehicle_.setPreferredSize(new Dimension(60, 20));
        fakingVehicle_.setValue(0);
        fakingVehicle_.setVisible(false);
        c.gridx = 1;
        add(fakingVehicle_, c);

        c.gridx = 2;
        c.gridheight = 1;
        button = new JButton();
        button.setActionCommand("faking amount");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;

        c.gridx = 0;
        jLabel1 = new JLabel();
        add(jLabel1, c);
        fakeMessagesTypes_ = new JComboBox<String>();
        fakeMessagesTypes_.setName("fakeMessagesTypes");

        fakeMessagesTypes_.addItem(Messages.getString("EditVehicleControlPanel.all"));
        fakeMessagesTypes_.setVisible(false);
        c.gridx = 1;
        add(fakeMessagesTypes_, c);

        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditVehicleControlPanel.amount"));
        ++c.gridy;
        add(jLabel1, c);
        amount_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        amount_.setPreferredSize(new Dimension(60, 20));
        amount_.setValue(100);

        c.gridx = 1;
        add(amount_, c);

        c.gridx = 2;
        c.gridheight = 1;
        button = new JButton();
        button.setActionCommand("amount");
        button.addActionListener(this);
        button.setVisible(false);
        buttonList_.add(button);
        add(button, c);
        c.gridheight = 1;

        c.gridx = 0;
        jLabel1 = new JLabel(Messages.getString("EditOneVehicleControlPanel.color"));
        ++c.gridy;
        add(jLabel1, c);
        colorPreview_ = new JPanel();
        colorPreview_.setBackground(Color.black);

        colorPreview_.setSize(10, 10);
        colorPreview_.addMouseListener(this);
        c.gridx = 1;
        add(colorPreview_, c);

        c.gridx = 0;
        jLabel1 = new JLabel();
        ++c.gridy;
        add(jLabel1, c);
        speedStreetRestriction_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        speedStreetRestriction_.setPreferredSize(new Dimension(60, 20));
        speedStreetRestriction_.setValue(80);
        speedStreetRestriction_.setVisible(false);
        c.gridx = 1;
        add(speedStreetRestriction_, c);

        c.gridx = 0;
        c.gridwidth = 2;
        ++c.gridy;
        createButton_ = ButtonCreator.getJButton("randomVehicles.png", "createRandom",
                Messages.getString("EditVehicleControlPanel.createRandom"), this);
        add(createButton_, c);

        c.gridx = 0;
        c.gridwidth = 2;
        ++c.gridy;
        deleteButton_ = ButtonCreator.getJButton("deleteAllVehicles.png", "clearVehicles",
                Messages.getString("EditVehicleControlPanel.btnClearVehicles"), this);
        add(deleteButton_, c);

        c.gridx = 0;
        scenarioApplyButton_ = new JButton(Messages.getString("EditVehicleControlPanel.apply"));
        scenarioApplyButton_.setActionCommand("applyToScenarioCreator");
        scenarioApplyButton_.addActionListener(this);
        add(scenarioApplyButton_, c);
        c.gridheight = 1;

        TextAreaLabel jlabel1 = new TextAreaLabel(Messages.getString("EditVehicleControlPanel.note"));
        ++c.gridy;
        c.gridx = 0;
        c.gridwidth = 2;
        add(jlabel1, c);

        c.weighty = 1.0;
        ++c.gridy;
        JPanel space = new JPanel();
        space.setOpaque(false);
        add(space, c);
        actionPerformed(new ActionEvent(chooseVehicleType_, 0, "comboBoxChanged"));
    }

    public static void activateSelectPropertiesMode(boolean mode) {
        for (JButton b : buttonList_) b.setVisible(mode);

    }

    public static void activateSelectAllPropertiesMode(boolean mode) {
        createButton_.setVisible(!mode);
        deleteButton_.setVisible(!mode);
        scenarioApplyButton_.setVisible(mode);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("createRandom".equals(command)) {
            Renderer.getInstance().setShowVehicles(true);
            Runnable job = new Runnable() {
                public void run() {
                    int i, j, k, l = 0;
                    Application.setProgressBar(true);
                    int maxX = Map.getInstance().getMapWidth();
                    int maxY = Map.getInstance().getMapHeight();
                    int minSpeedValue = (int) Math.round(((Number) minSpeed_.getValue()).intValue() * 100000.0 / 3600);
                    int maxSpeedValue = (int) Math.round(((Number) maxSpeed_.getValue()).intValue() * 100000.0 / 3600);
                    int minCommDistValue = ((Number) minCommDist_.getValue()).intValue() * 100;
                    int maxCommDistValue = ((Number) maxCommDist_.getValue()).intValue() * 100;
                    int minWaitValue = ((Number) minWait_.getValue()).intValue();
                    int maxWaitValue = ((Number) maxWait_.getValue()).intValue();
                    int minBrakingValue = ((Number) minBraking_.getValue()).intValue();
                    int maxBrakingValue = ((Number) maxBraking_.getValue()).intValue();
                    int minAccelerationValue = ((Number) minAcceleration_.getValue()).intValue();
                    int maxAccelerationValue = ((Number) maxAcceleration_.getValue()).intValue();
                    int minTimeDistance = ((Number) minTimeDistance_.getValue()).intValue();
                    int maxTimeDistance = ((Number) maxTimeDistance_.getValue()).intValue();
                    int minPoliteness = ((Number) minPoliteness_.getValue()).intValue();
                    int maxPoliteness = ((Number) maxPoliteness_.getValue()).intValue();
                    int vehiclesDeviatingMaxSpeed = ((Number) vehiclesDeviatingMaxSpeed_.getValue()).intValue();
                    int deviationFromSpeedLimit = ((Number) deviationFromSpeedLimit_.getValue()).intValue();

                    int speedDeviation = 0;
                    int wiFiValue = ((Number) wiFi_.getValue()).intValue();
                    int emergencyValue = ((Number) emergencyVehicle_.getValue()).intValue();
                    int speedRestriction = (int) Math.round(((Number) speedStreetRestriction_.getValue()).intValue() * 100000.0 / 3600);
                    int vehiclesFaking = ((Number) fakingVehicle_.getValue()).intValue();

                    if (wiFiValue < 0) {
                        wiFiValue = 0;
                        wiFi_.setValue(0);
                    } else if (wiFiValue > 100) {
                        wiFiValue = 100;
                        wiFi_.setValue(100);
                    }
                    if (emergencyValue < 0) {
                        emergencyValue = 0;
                        emergencyVehicle_.setValue(0);
                    } else if (emergencyValue > 100) {
                        emergencyValue = 100;
                        emergencyVehicle_.setValue(100);
                    }

                    if (vehiclesFaking < 0) {
                        vehiclesFaking = 0;
                        fakingVehicle_.setValue(0);
                    } else if (vehiclesFaking > 100) {
                        vehiclesFaking = 100;
                        fakingVehicle_.setValue(100);
                    }

                    int amountValue = ((Number) amount_.getValue()).intValue();

                    ArrayDeque<WayPoint> destinations = null;
                    Vehicle tmpVehicle;
                    Random random = new Random();
                    int tmpRandom = -1;

                    for (i = 0; i < amountValue; ) {
                        j = 0;
                        k = 0;
                        ++l;
                        destinations = new ArrayDeque<WayPoint>(2);
                        while (j < 2 && k < 20) {
                            try {
                                ++k;
                                WayPoint tmpWayPoint = new WayPoint(random.nextInt(maxX), random.nextInt(maxY),
                                        getRandomRange(minWaitValue, maxWaitValue, random));
                                if (tmpWayPoint.getStreet().getSpeed() <= speedRestriction) {
                                    destinations.add(tmpWayPoint);
                                    ++j;
                                }
                            } catch (Exception e) {
                            }
                        }
                        if (k < 20) {
                            try {
                                tmpRandom = getRandomRange(1, 100, random);
                                if (tmpRandom <= vehiclesDeviatingMaxSpeed)
                                    speedDeviation = getRandomRange(-deviationFromSpeedLimit, deviationFromSpeedLimit, random);
                                else speedDeviation = 0;
                                tmpVehicle = new Vehicle(destinations, ((Number) vehicleLength_.getValue()).intValue(),
                                        getRandomRange(minSpeedValue, maxSpeedValue, random),
                                        getRandomRange(minCommDistValue, maxCommDistValue, random),
                                        getRandomRange(minBrakingValue, maxBrakingValue, random),
                                        getRandomRange(minAccelerationValue, maxAccelerationValue, random),
                                        getRandomRange(minTimeDistance, maxTimeDistance, random),
                                        getRandomRange(minPoliteness, maxPoliteness, random),
                                        (int) Math.round(speedDeviation * 100000.0 / 3600),
                                        colorPreview_.getBackground());
                                Map.getInstance().addVehicle(tmpVehicle);
                                ++i;
                            } catch (Exception e) {
                            }
                        }
                        if (l > amountValue * 4) break;
                    }
                    int errorLevel = 2;
                    if (i < amountValue) errorLevel = 6;
                    Application.setProgressBar(false);
                    Renderer.getInstance().ReRender(false, false);

                }
            };
            new Thread(job).start();

        } else if ("comboBoxChanged".equals(command)) {
            if (((Component) e.getSource()).getName().equals("chooseVehicleType")) {
                VehicleType tmpVehicleType = (VehicleType) chooseVehicleType_.getSelectedItem();

                if (tmpVehicleType != null) {
                    maxSpeed_.setValue((int) Math.round(tmpVehicleType.getMaxSpeed() / (100000.0 / 3600)));
                    vehicleLength_.setValue(tmpVehicleType.getVehicleLength());
                    maxCommDist_.setValue(Math.round(tmpVehicleType.getMaxCommDist() / 100));
                    maxWait_.setValue(tmpVehicleType.getMaxWaittime());
                    maxBraking_.setValue(tmpVehicleType.getMaxBrakingRate());
                    maxAcceleration_.setValue(tmpVehicleType.getMaxAccelerationRate());
                    maxTimeDistance_.setValue(tmpVehicleType.getMaxTimeDistance());
                    maxPoliteness_.setValue(tmpVehicleType.getMaxPoliteness());
                    minSpeed_.setValue((int) Math.round(tmpVehicleType.getMinSpeed() / (100000.0 / 3600)));
                    minCommDist_.setValue(Math.round(tmpVehicleType.getMinCommDist() / 100));
                    minWait_.setValue(tmpVehicleType.getMinWaittime());
                    minBraking_.setValue(tmpVehicleType.getMinBrakingRate());
                    minAcceleration_.setValue(tmpVehicleType.getMinAccelerationRate());
                    minTimeDistance_.setValue(tmpVehicleType.getMinTimeDistance());
                    minPoliteness_.setValue(tmpVehicleType.getMinPoliteness());
                    vehiclesDeviatingMaxSpeed_.setValue(tmpVehicleType.getVehiclesDeviatingMaxSpeed_());
                    deviationFromSpeedLimit_.setValue((int) Math.round(tmpVehicleType.getDeviationFromSpeedLimit_() / (100000.0 / 3600)));
                    colorPreview_.setBackground(new Color(tmpVehicleType.getColor()));
                }
            } else if (((Component) e.getSource()).getName().equals("fakeMessagesTypes")) {

            }
        } else if ("clearVehicles".equals(command)) {
            if (JOptionPane.showConfirmDialog(null,
                    Messages.getString("EditVehicleControlPanel.msgBoxClearAll"),
                    "", JOptionPane.YES_NO_OPTION) == 0) {
                Map.getInstance().clearVehicles();
                Renderer.getInstance().ReRender(true, false);
            }
        } else if ("applyToScenarioCreator".equals(command)) {
            int minSpeedValue = (int) Math.round(((Number) minSpeed_.getValue()).intValue() * 100000.0 / 3600);
            int maxSpeedValue = (int) Math.round(((Number) maxSpeed_.getValue()).intValue() * 100000.0 / 3600);
            int minCommDistValue = ((Number) minCommDist_.getValue()).intValue() * 100;
            int maxCommDistValue = ((Number) maxCommDist_.getValue()).intValue() * 100;
            int minWaitValue = ((Number) minWait_.getValue()).intValue();
            int maxWaitValue = ((Number) maxWait_.getValue()).intValue();
            int minBrakingValue = ((Number) minBraking_.getValue()).intValue();
            int maxBrakingValue = ((Number) maxBraking_.getValue()).intValue();
            int minAccelerationValue = ((Number) minAcceleration_.getValue()).intValue();
            int maxAccelerationValue = ((Number) maxAcceleration_.getValue()).intValue();
            int minTimeDistance = ((Number) minTimeDistance_.getValue()).intValue();
            int maxTimeDistance = ((Number) maxTimeDistance_.getValue()).intValue();
            int minPoliteness = ((Number) minPoliteness_.getValue()).intValue();
            int maxPoliteness = ((Number) maxPoliteness_.getValue()).intValue();
            int vehiclesDeviatingMaxSpeed = ((Number) vehiclesDeviatingMaxSpeed_.getValue()).intValue();
            int deviationFromSpeedLimit = ((Number) deviationFromSpeedLimit_.getValue()).intValue();

            int wiFiValue = ((Number) wiFi_.getValue()).intValue();
            int emergencyValue = ((Number) emergencyVehicle_.getValue()).intValue();
            int speedRestriction = (int) Math.round(((Number) speedStreetRestriction_.getValue()).intValue() * 100000.0 / 3600);
            int vehiclesFaking = ((Number) fakingVehicle_.getValue()).intValue();

            if (wiFiValue < 0) {
                wiFiValue = 0;
                wiFi_.setValue(0);
            } else if (wiFiValue > 100) {
                wiFiValue = 100;
                wiFi_.setValue(100);
            }
            if (emergencyValue < 0) {
                emergencyValue = 0;
                emergencyVehicle_.setValue(0);
            } else if (emergencyValue > 100) {
                emergencyValue = 100;
                emergencyVehicle_.setValue(100);
            }

            if (vehiclesFaking < 0) {
                vehiclesFaking = 0;
                fakingVehicle_.setValue(0);
            } else if (vehiclesFaking > 100) {
                vehiclesFaking = 100;
                fakingVehicle_.setValue(100);
            }

            int amountValue = ((Number) amount_.getValue()).intValue();
        }
    }

    private int getRandomRange(int min, int max, Random random) {
        if (min == max) return min;
        else {
            if (max < min) {
                int tmp = max;
                max = min;
                min = tmp;
            }
            return (random.nextInt(max - min + 1) + min);
        }
    }

    public void refreshVehicleTypes() {
        chooseVehicleType_.removeActionListener(this);
        chooseVehicleType_.removeAllItems();
        VehicleTypeXML xml = new VehicleTypeXML(null);
        for (VehicleType type : xml.getVehicleTypes()) {
            chooseVehicleType_.addItem(type);
        }
        chooseVehicleType_.addActionListener(this);
    }

    public void mouseClicked(MouseEvent e) {

        Color color = JColorChooser.showDialog(this,
                Messages.getString("EditOneVehicleControlPanel.color"), colorPreview_.getBackground());

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