package com.fifthgen.trafficsim.scenario;

import com.fifthgen.trafficsim.Application;
import com.fifthgen.trafficsim.gui.Renderer;
import com.fifthgen.trafficsim.gui.helpers.*;
import com.fifthgen.trafficsim.localization.Messages;
import com.fifthgen.trafficsim.map.Map;
import com.fifthgen.trafficsim.map.Node;
import com.fifthgen.trafficsim.map.Region;
import com.fifthgen.trafficsim.routing.WayPoint;
import com.fifthgen.trafficsim.scenario.events.Event;
import com.fifthgen.trafficsim.scenario.events.*;
import com.fifthgen.trafficsim.simulation.WorkerThread;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class Scenario {

    private static final Scenario INSTANCE = new Scenario();

    private boolean ready_ = true;

    private String scenarioName = "";

    private Scenario() {
    }

    public static Scenario getInstance() {
        return INSTANCE;
    }

    public void initNewScenario() {
        if (ready_ == true) {
            ready_ = false;
            if (!Renderer.getInstance().isConsoleStart()) Application.getSimulationMaster().stopThread();
            if (!Renderer.getInstance().isConsoleStart())
                Application.getMainControlPanel().getSimulatePanel().setSimulationStop();
            KnownVehiclesList.setTimePassed(0);
            KnownRSUsList.setTimePassed(0);
            Renderer.getInstance().setTimePassed(0);
            Renderer.getInstance().setMarkedVehicle(null);
            Renderer.getInstance().setShowVehicles(false);
            Renderer.getInstance().setShowRSUs(false);
            Renderer.getInstance().setShowMixZones(false);
            Renderer.getInstance().setAttackedVehicle(null);
            Renderer.getInstance().setAttackerVehicle(null);
            Renderer.getInstance().setShowAttackers(false);
            Vehicle.setMaximumCommunicationDistance(0);
            Vehicle.resetGlobalRandomGenerator();
            Vehicle.setMinTravelTimeForRecycling(60000);
            Vehicle.setAttackedVehicleID_(0);
            if (!Renderer.getInstance().isConsoleStart()) MouseClickManager.getInstance().cleanMarkings();
            Region[][] Regions = Map.getInstance().getRegions();
            int Region_max_x = Map.getInstance().getRegionCountX();
            int Region_max_y = Map.getInstance().getRegionCountY();
            int i, j;
            for (i = 0; i < Region_max_x; ++i) {
                for (j = 0; j < Region_max_y; ++j) {
                    Regions[i][j].cleanVehicles();
                }
            }
            EventList.getInstance().clearEvents();
        }
    }

    public void load(File file, boolean zip) {
        scenarioName = file.getName();
        Map.getInstance().clearMixZones();
        Map.getInstance().clearRSUs();
        try {
            if (!Renderer.getInstance().isConsoleStart()) Application.setProgressBar(true);
            initNewScenario();
            String type, penaltyType, fakeMessageType, eventSpotType;
            int x, y, frequency, radius, time, maxSpeed, vehicleLength, maxCommDistance, direction, lanes, braking_rate, acceleration_rate, timeDistance, politeness, speedDeviation, color, mixX, mixY, mixRadius, wifiX, wifiY, wifiRadius;
            boolean tmpBoolean, wifi, emergencyVehicle, tmpAttacker, tmpAttacked, isEncrypted, mixHasRSU, isFake, fakingMessages;
            long seed;
            ArrayDeque<WayPoint> destinations;
            WayPoint tmpWayPoint;
            Vehicle tmpVehicle;
            Node[] tmpNodes;
            Node tmpNode;
            SMInputCursor childCrsr, vehicleCrsr, vehiclesCrsr, mixNodeCrsr, mixNodesCrsr, settingsCrsr, eventCrsr, eventsCrsr, eventSpotCrsr, eventSpotsCrsr, destinationsCrsr, waypointCrsr, rsuCrsr, rsusCrsr, aRsuCrsr, aRsusCrsr;
            XMLInputFactory factory = XMLInputFactory.newInstance();

            factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
            factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
            factory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);

            Vehicle.setEncryptedBeaconsInMix_(false);

            InputStream filestream;
            if (zip) {
                filestream = new ZipInputStream(new FileInputStream(file));
                ((ZipInputStream) filestream).getNextEntry();
            } else filestream = new FileInputStream(file);
            XMLStreamReader sr = factory.createXMLStreamReader(filestream);
            SMInputCursor rootCrsr = SMInputFactory.rootElementCursor(sr);
            rootCrsr.getNext();
            if (rootCrsr.getLocalName().toLowerCase().equals("scenario")) {
                childCrsr = rootCrsr.childElementCursor();
                while (childCrsr.getNext() != null) {
                    if (childCrsr.getLocalName().toLowerCase().equals("settings")) {
                        settingsCrsr = childCrsr.childElementCursor();
                        while (settingsCrsr.getNext() != null) {
                            if (settingsCrsr.getLocalName().toLowerCase().equals("communicationenabled")) {
                                tmpBoolean = settingsCrsr.collectDescendantText(false).equals("true");

                                Vehicle.setCommunicationEnabled(tmpBoolean);
                                RSU.setCommunicationEnabled(tmpBoolean);
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("beaconsenabled")) {
                                tmpBoolean = settingsCrsr.collectDescendantText(false).equals("true");

                                Vehicle.setBeaconsEnabled(tmpBoolean);
                                RSU.setBeaconsEnabled(tmpBoolean);
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("fallbackinmixzonesenabled")) {
                                tmpBoolean = settingsCrsr.collectDescendantText(false).equals("true");

                                Vehicle.setMixZonesFallbackEnabled(tmpBoolean);
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("fallbackinmixzonesfloodingonly")) {
                                tmpBoolean = settingsCrsr.collectDescendantText(false).equals("true");

                                Vehicle.setMixZonesFallbackFloodingOnly(tmpBoolean);
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("globalInfrastructureenabled")) {
                                tmpBoolean = settingsCrsr.collectDescendantText(false).equals("true");


                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("mixzonesenabled")) {
                                tmpBoolean = settingsCrsr.collectDescendantText(false).equals("true");

                                Vehicle.setMixZonesEnabled(tmpBoolean);
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("vehiclerecyclingenabled")) {
                                tmpBoolean = settingsCrsr.collectDescendantText(false).equals("true");

                                Vehicle.setRecyclingEnabled(tmpBoolean);
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("communicationinterval")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));

                                    Vehicle.setCommunicationInterval(tmp);
                                    RSU.setCommunicationInterval(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("beaconsinterval")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));

                                    Vehicle.setBeaconInterval(tmp);
                                    RSU.setBeaconInterval(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("mixzoneradius")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));

                                    Vehicle.setMixZoneRadius(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("autoaddmixzones")) {
                                tmpBoolean = settingsCrsr.collectDescendantText(false).equals("true");
                                Renderer.getInstance().setAutoAddMixZones(tmpBoolean);
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("routingmode")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    if (tmp > 1) tmp = 1;
                                    else if (tmp < 0) tmp = 0;
                                    Vehicle.setRoutingMode(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("mintraveltimeforrecycling")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    Vehicle.setMinTravelTimeForRecycling(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("arsulog")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (Vehicle.isAttackerDataLogged_()) AttackLogWriter.setLogPath(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("arsuloggingenabled")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        Vehicle.setAttackerDataLogged_(true);
                                    } else {
                                        Vehicle.setAttackerDataLogged_(false);
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("arsuencryptedloggingenabled")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        Vehicle.setAttackerEncryptedDataLogged_(true);
                                    } else {
                                        Vehicle.setAttackerEncryptedDataLogged_(false);
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("privacyloggingenabled")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        Vehicle.setPrivacyDataLogged_(true);
                                        PrivacyLogWriter.setLogPath(System.getProperty("user.dir"));

                                    } else {
                                        Vehicle.setPrivacyDataLogged_(false);
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("idslog")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);

                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("idsloggingenabled")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);

                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("eventlog")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (KnownPenalties.isLogEvents_() && !tmp.equals("")) {
                                        EventLogWriter.setLogPath(tmp);
                                        EventLogWriter.log("Time," + "PenaltyType," + "X," + "Y," + "Sender," + "Receiver");
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("eventloggingenabled")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        KnownPenalties.setLogEvents_(true);
                                    } else {
                                        KnownPenalties.setLogEvents_(false);
                                    }
                                } catch (Exception e) {
                                }

                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("showencryptedcomminmix")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        RSU.setShowEncryptedBeaconsInMix_(true);
                                    } else {
                                        RSU.setShowEncryptedBeaconsInMix_(false);
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("silentperiodsenabled")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        Vehicle.setSilentPeriodsOn(true);
                                    } else {
                                        Vehicle.setSilentPeriodsOn(false);
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("silentperiodduration")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    Vehicle.setTIME_OF_SILENT_PERIODS(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("silentperiodfrequency")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    Vehicle.setTIME_BETWEEN_SILENT_PERIODS(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("slowenabled")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        Vehicle.setSlowOn(true);
                                    } else {
                                        Vehicle.setSlowOn(false);
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("slowtimetochangepseudonym")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    Vehicle.setTIME_TO_PSEUDONYM_CHANGE(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("slowspeedlimit")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    Vehicle.setSLOW_SPEED_LIMIT(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("idsactivated")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        Vehicle.setIdsActivated(true);
                                    } else {
                                        Vehicle.setIdsActivated(false);
                                    }

                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("advancedidsrules")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("activerules")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);

                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("beaconslogged")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    KnownVehicle.setAmountOfSavedBeacons(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("fakemessageinterval")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    Vehicle.setFakeMessagesInterval_(tmp);
                                } catch (Exception e) {
                                }
                            }   else if (settingsCrsr.getLocalName().toLowerCase().equals("evabeaconthreshold")) {
                                try {
                                    double tmp = Double.parseDouble(settingsCrsr.collectDescendantText(false));
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("evamessagedelay")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    Vehicle.setMaxEVAMessageDelay_(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("spamdetection")) {
                                try {
                                    String tmp = settingsCrsr.collectDescendantText(false);
                                    if (tmp.equals("true")) {
                                        KnownEventSource.setSpamCheck_(true);
                                        KnownPenalties.setSpamCheck_(true);
                                    } else {
                                        KnownEventSource.setSpamCheck_(false);
                                        KnownPenalties.setSpamCheck_(false);
                                    }
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("spammessagethreshold")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    KnownEventSource.setSpammingThreshold_(tmp);
                                } catch (Exception e) {
                                }
                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("spamtimethreshold")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    KnownEventSource.setSpammingTimeThreshold_(tmp);
                                } catch (Exception e) {
                                }

                            } else if (settingsCrsr.getLocalName().toLowerCase().equals("trafficmodel")) {
                                try {
                                    int tmp = Integer.parseInt(settingsCrsr.collectDescendantText(false));
                                    WorkerThread.setSimulationMode_(tmp);
                                } catch (Exception e) {
                                }

                            }
                        }
                    } else if (childCrsr.getLocalName().toLowerCase().equals("vehicles")) {
                        vehiclesCrsr = childCrsr.childElementCursor();
                        while (vehiclesCrsr.getNext() != null) {
                            if (vehiclesCrsr.getLocalName().toLowerCase().equals("vehicle")) {
                                maxCommDistance = 10000;
                                vehicleLength = 2500;
                                maxSpeed = 10000;
                                wifi = true;
                                emergencyVehicle = false;
                                braking_rate = 100;
                                acceleration_rate = 200;
                                timeDistance = 100;
                                politeness = 50;
                                speedDeviation = 0;
                                color = 0;
                                destinations = new ArrayDeque<WayPoint>(1);
                                vehicleCrsr = vehiclesCrsr.childElementCursor();
                                tmpAttacker = false;
                                tmpAttacked = false;
                                fakingMessages = false;
                                fakeMessageType = "";
                                while (vehicleCrsr.getNext() != null) {
                                    if (vehicleCrsr.getLocalName().toLowerCase().equals("vehiclelength")) {
                                        try {
                                            vehicleLength = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("maxspeed")) {
                                        try {
                                            maxSpeed = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("maxcommdist")) {
                                        try {
                                            maxCommDistance = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("wifi")) {
                                        try {
                                            wifi = Boolean.parseBoolean(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("emergencyvehicle")) {
                                        try {
                                            emergencyVehicle = Boolean.parseBoolean(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("braking_rate")) {
                                        try {
                                            braking_rate = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("acceleration_rate")) {
                                        try {
                                            acceleration_rate = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("timedistance")) {
                                        try {
                                            timeDistance = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("politeness")) {
                                        try {
                                            politeness = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("speeddeviation")) {
                                        try {
                                            speedDeviation = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("color")) {
                                        try {
                                            color = Integer.parseInt(vehicleCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("isattacker")) {
                                        try {
                                            if (vehicleCrsr.collectDescendantText(false).equals("true"))
                                                tmpAttacker = true;
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("isattacked")) {
                                        try {
                                            if (vehicleCrsr.collectDescendantText(false).equals("true"))
                                                tmpAttacked = true;
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("isfakingmessages")) {
                                        try {
                                            if (vehicleCrsr.collectDescendantText(false).equals("true"))
                                                fakingMessages = true;
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("fakingmessagetype")) {
                                        try {
                                            fakeMessageType = vehicleCrsr.collectDescendantText(false);
                                        } catch (Exception e) {
                                        }
                                    } else if (vehicleCrsr.getLocalName().toLowerCase().equals("destinations")) {
                                        destinationsCrsr = vehicleCrsr.childElementCursor();
                                        while (destinationsCrsr.getNext() != null) {
                                            if (destinationsCrsr.getLocalName().toLowerCase().equals("waypoint")) {
                                                x = -1;
                                                y = -1;
                                                time = -1;
                                                waypointCrsr = destinationsCrsr.childElementCursor();
                                                while (waypointCrsr.getNext() != null) {
                                                    if (waypointCrsr.getLocalName().toLowerCase().equals("x")) {
                                                        try {
                                                            x = Integer.parseInt(waypointCrsr.collectDescendantText(false));
                                                        } catch (Exception e) {
                                                        }
                                                    } else if (waypointCrsr.getLocalName().toLowerCase().equals("y")) {
                                                        try {
                                                            y = Integer.parseInt(waypointCrsr.collectDescendantText(false));
                                                        } catch (Exception e) {
                                                        }
                                                    } else if (waypointCrsr.getLocalName().toLowerCase().equals("wait")) {
                                                        try {
                                                            time = Integer.parseInt(waypointCrsr.collectDescendantText(false));
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                }
                                                try {
                                                    tmpWayPoint = new WayPoint(x, y, time);
                                                    destinations.add(tmpWayPoint);
                                                } catch (ParseException e) {
                                                }
                                            }
                                        }
                                    }
                                }
                                if (maxCommDistance != -1 && maxSpeed != -1 && destinations.size() > 1) {
                                    try {
                                        tmpVehicle = new Vehicle(destinations, vehicleLength, maxSpeed, maxCommDistance, braking_rate, acceleration_rate, timeDistance, politeness, speedDeviation, new Color(color));
                                        Map.getInstance().addVehicle(tmpVehicle);
                                        if (tmpAttacker) Renderer.getInstance().setAttackerVehicle(tmpVehicle);
                                        if (tmpAttacked) {
                                            Renderer.getInstance().setAttackedVehicle(tmpVehicle);
                                            Vehicle.setAttackedVehicleID_(tmpVehicle.getID());
                                        }
                                        tmpAttacker = false;
                                        tmpAttacked = false;
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                    } else if (childCrsr.getLocalName().toLowerCase().equals("mixzones")) {
                        mixNodesCrsr = childCrsr.childElementCursor();
                        int maxMixRadius = 0;
                        while (mixNodesCrsr.getNext() != null) {
                            if (mixNodesCrsr.getLocalName().toLowerCase().equals("mixnode")) {
                                mixX = -1;
                                mixY = -1;
                                mixRadius = -1;
                                mixHasRSU = false;

                                mixNodeCrsr = mixNodesCrsr.childElementCursor();
                                while (mixNodeCrsr.getNext() != null) {
                                    if (mixNodeCrsr.getLocalName().toLowerCase().equals("x")) {
                                        try {
                                            mixX = Integer.parseInt(mixNodeCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (mixNodeCrsr.getLocalName().toLowerCase().equals("y")) {
                                        try {
                                            mixY = Integer.parseInt(mixNodeCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (mixNodeCrsr.getLocalName().toLowerCase().equals("radius")) {
                                        try {
                                            mixRadius = Integer.parseInt(mixNodeCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (mixNodeCrsr.getLocalName().toLowerCase().equals("hasrsu")) {
                                        try {
                                            if (mixNodeCrsr.collectDescendantText(false).equals("true"))
                                                mixHasRSU = true;
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                if (mixRadius > 0) {
                                    try {
                                        int Region_cnt_x = Map.getInstance().getRegionCountX();
                                        int Region_cnt_y = Map.getInstance().getRegionCountY();
                                        Region[][] Regions = Map.getInstance().getRegions();
                                        for (int i = 0; i < Region_cnt_x; ++i) {
                                            for (int j = 0; j < Region_cnt_y; ++j) {
                                                tmpNodes = Regions[i][j].getNodes();
                                                for (int k = 0; k < tmpNodes.length; k++) {
                                                    tmpNode = tmpNodes[k];
                                                    if (tmpNode.getX() == mixX && tmpNode.getY() == mixY) {
                                                        if (mixHasRSU) Vehicle.setEncryptedBeaconsInMix_(true);
                                                        Regions[i][j].addMixZone(tmpNode, mixRadius);
                                                        if (maxMixRadius < mixRadius) maxMixRadius = mixRadius;
                                                        Vehicle.setEncryptedBeaconsInMix_(false);
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                        Vehicle.setMaxMixZoneRadius(maxMixRadius);
                    } else if (childCrsr.getLocalName().toLowerCase().equals("rsus")) {
                        rsusCrsr = childCrsr.childElementCursor();
                        while (rsusCrsr.getNext() != null) {
                            if (rsusCrsr.getLocalName().toLowerCase().equals("rsu")) {
                                wifiX = -1;
                                wifiY = -1;
                                wifiRadius = -1;
                                isEncrypted = false;
                                rsuCrsr = rsusCrsr.childElementCursor();
                                while (rsuCrsr.getNext() != null) {
                                    if (rsuCrsr.getLocalName().toLowerCase().equals("x")) {
                                        try {
                                            wifiX = Integer.parseInt(rsuCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (rsuCrsr.getLocalName().toLowerCase().equals("y")) {
                                        try {
                                            wifiY = Integer.parseInt(rsuCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (rsuCrsr.getLocalName().toLowerCase().equals("radius")) {
                                        try {
                                            wifiRadius = Integer.parseInt(rsuCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (rsuCrsr.getLocalName().toLowerCase().equals("isencrypted")) {
                                        try {
                                            if (rsuCrsr.collectDescendantText(false).equals("true")) isEncrypted = true;
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                try {
                                    if (!isEncrypted)
                                        Map.getInstance().addRSU(new RSU(wifiX, wifiY, wifiRadius, false));
                                } catch (Exception e) {
                                }
                            }
                        }
                    } else if (childCrsr.getLocalName().toLowerCase().equals("arsus")) {
                        aRsusCrsr = childCrsr.childElementCursor();
                        while (aRsusCrsr.getNext() != null) {
                            if (aRsusCrsr.getLocalName().toLowerCase().equals("arsu")) {
                                wifiX = -1;
                                wifiY = -1;
                                wifiRadius = -1;

                                aRsuCrsr = aRsusCrsr.childElementCursor();
                                while (aRsuCrsr.getNext() != null) {
                                    if (aRsuCrsr.getLocalName().toLowerCase().equals("x")) {
                                        try {
                                            wifiX = Integer.parseInt(aRsuCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (aRsuCrsr.getLocalName().toLowerCase().equals("y")) {
                                        try {
                                            wifiY = Integer.parseInt(aRsuCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (aRsuCrsr.getLocalName().toLowerCase().equals("radius")) {
                                        try {
                                            wifiRadius = Integer.parseInt(aRsuCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                try {
                                } catch (Exception e) {
                                }
                            }
                        }
                    } else if (childCrsr.getLocalName().toLowerCase().equals("events")) {
                        eventsCrsr = childCrsr.childElementCursor();
                        while (eventsCrsr.getNext() != null) {
                            if (eventsCrsr.getLocalName().toLowerCase().equals("event")) {
                                time = -1;
                                x = -1;
                                y = -1;
                                direction = 0;
                                lanes = Integer.MAX_VALUE;
                                type = "";
                                penaltyType = "";
                                isFake = false;
                                eventCrsr = eventsCrsr.childElementCursor();
                                while (eventCrsr.getNext() != null) {
                                    if (eventCrsr.getLocalName().toLowerCase().equals("time")) {
                                        try {
                                            time = Integer.parseInt(eventCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventCrsr.getLocalName().toLowerCase().equals("type")) {
                                        type = eventCrsr.collectDescendantText(false).toLowerCase();
                                    } else if (eventCrsr.getLocalName().toLowerCase().equals("x")) {
                                        try {
                                            x = Integer.parseInt(eventCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventCrsr.getLocalName().toLowerCase().equals("y")) {
                                        try {
                                            y = Integer.parseInt(eventCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventCrsr.getLocalName().toLowerCase().equals("direction")) {
                                        try {
                                            direction = Integer.parseInt(eventCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventCrsr.getLocalName().toLowerCase().equals("lanes")) {
                                        try {
                                            lanes = Integer.parseInt(eventCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventCrsr.getLocalName().toLowerCase().equals("penaltytype")) {
                                        try {
                                            penaltyType = eventCrsr.collectDescendantText(false);
                                        } catch (Exception e) {
                                        }
                                    } else if (eventCrsr.getLocalName().toLowerCase().equals("isfake")) {
                                        try {
                                            if (eventCrsr.collectDescendantText(false).equals("true")) isFake = true;
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                if (time != -1) {
                                    if (type.equals("startblocking") && x != -1 && y != -1) {
                                        try {
                                            EventList.getInstance().addEvent(new StartBlocking(time, x, y, direction, lanes, isFake, penaltyType));
                                        } catch (Exception e) {
                                        }
                                    } else if (type.equals("stopblocking") && x != -1 && y != -1) {
                                        try {
                                            EventList.getInstance().addEvent(new StopBlocking(time, x, y));
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                            }
                        }
                    } else if (childCrsr.getLocalName().toLowerCase().equals("eventspots")) {
                        eventSpotsCrsr = childCrsr.childElementCursor();
                        while (eventSpotsCrsr.getNext() != null) {
                            if (eventSpotsCrsr.getLocalName().toLowerCase().equals("eventspot")) {

                                x = -1;
                                y = -1;
                                seed = -1;
                                frequency = -1;
                                radius = -1;
                                eventSpotType = "";

                                eventSpotCrsr = eventSpotsCrsr.childElementCursor();
                                while (eventSpotCrsr.getNext() != null) {
                                    if (eventSpotCrsr.getLocalName().toLowerCase().equals("x")) {
                                        try {
                                            x = Integer.parseInt(eventSpotCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventSpotCrsr.getLocalName().toLowerCase().equals("y")) {
                                        try {
                                            y = Integer.parseInt(eventSpotCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventSpotCrsr.getLocalName().toLowerCase().equals("seed")) {
                                        try {
                                            seed = Long.parseLong(eventSpotCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventSpotCrsr.getLocalName().toLowerCase().equals("frequency")) {
                                        try {
                                            frequency = Integer.parseInt(eventSpotCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventSpotCrsr.getLocalName().toLowerCase().equals("radius")) {
                                        try {
                                            radius = Integer.parseInt(eventSpotCrsr.collectDescendantText(false));
                                        } catch (Exception e) {
                                        }
                                    } else if (eventSpotCrsr.getLocalName().toLowerCase().equals("eventspottype")) {
                                        try {
                                            eventSpotType = eventSpotCrsr.collectDescendantText(false);
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                if (frequency != -1) {
                                    try {
                                        EventSpotList.getInstance().addEventSpot(new EventSpot(x, y, frequency, radius, eventSpotType, seed));
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        if (!Renderer.getInstance().isConsoleStart()) Application.setProgressBar(false);
        ready_ = true;
        Renderer.getInstance().ReRender(false, false);
    }

    public void save(File file, boolean zip) {
        try {
            Application.setProgressBar(true);
            int i, j, k;
            Vehicle[] vehiclesArray;
            Node[] mixZoneArray;
            ArrayDeque<WayPoint> destinations;
            Iterator<WayPoint> wayPointIterator;
            Vehicle vehicle;
            Node mixNode;
            WayPoint wayPoint;
            RSU rsu;
            RSU[] rsuArray;
            Event event;
            SMOutputElement level1, level2, level3;

            int Region_cnt_x = Map.getInstance().getRegionCountX();
            int Region_cnt_y = Map.getInstance().getRegionCountY();
            Region[][] Regions = Map.getInstance().getRegions();

            OutputStream filestream;
            if (zip) {
                filestream = new ZipOutputStream(new FileOutputStream(file + ".zip"));
                ((ZipOutputStream) filestream).putNextEntry(new ZipEntry(file.getName()));
            } else filestream = new FileOutputStream(file);
            XMLStreamWriter xw = XMLOutputFactory.newInstance().createXMLStreamWriter(filestream);
            SMOutputDocument doc = SMOutputFactory.createOutputDocument(xw);
            doc.setIndentation("\n\t\t\t\t\t\t\t\t", 2, 1);
            doc.addComment("Generated on " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()));

            SMOutputElement root = doc.addElement("Scenario");
            SMOutputElement settings = root.addElement("Settings");
            settings.addElement("CommunicationEnabled").addValue(Vehicle.getCommunicationEnabled());
            settings.addElement("BeaconsEnabled").addValue(Vehicle.getBeaconsEnabled());
            settings.addElement("GlobalInfrastructureEnabled").addValue(true);
            settings.addElement("CommunicationInterval").addValue(Vehicle.getCommunicationInterval());
            settings.addElement("BeaconsInterval").addValue(Vehicle.getBeaconInterval());
            settings.addElement("MixZonesEnabled").addValue(Vehicle.getMixZonesEnabled());
            settings.addElement("MixZoneRadius").addValue(Vehicle.getMixZoneRadius());
            settings.addElement("AutoAddMixZones").addValue(Renderer.getInstance().isAutoAddMixZones());
            settings.addElement("RoutingMode").addValue(Vehicle.getRoutingMode());
            settings.addElement("VehicleRecyclingEnabled").addValue(Vehicle.getRecyclingEnabled());
            settings.addElement("FallBackInMixZonesEnabled").addValue(Vehicle.getMixZonesFallbackEnabled());
            settings.addElement("FallBackInMixZonesFloodingOnly").addValue(Vehicle.getMixZonesFallbackFloodingOnly());
            settings.addElement("MinTravelTimeForRecycling").addValue(Vehicle.getMinTravelTimeForRecycling());

            settings.addElement("ARSULoggingEnabled").addValue(Vehicle.isAttackerDataLogged_());
            settings.addElement("ARSULog").addCharacters(AttackLogWriter.getLogPath());
            settings.addElement("ARSUEncryptedLoggingEnabled").addValue(Vehicle.isAttackerEncryptedDataLogged_());

            settings.addElement("privacyLoggingEnabled").addValue(Vehicle.isPrivacyDataLogged_());

            settings.addElement("IDSLog").addCharacters(IDSLogWriter.getLogPath());

            settings.addElement("EventLoggingEnabled").addValue(KnownPenalties.isLogEvents_());
            settings.addElement("EventLog").addCharacters(EventLogWriter.getLogPath());

            settings.addElement("SilentPeriodsEnabled").addValue(Vehicle.isSilentPeriodsOn());
            settings.addElement("SilentPeriodDuration").addValue(Vehicle.getTIME_OF_SILENT_PERIODS());
            settings.addElement("SilentPeriodFrequency").addValue(Vehicle.getTIME_BETWEEN_SILENT_PERIODS());

            settings.addElement("SlowEnabled").addValue(Vehicle.isSlowOn());
            settings.addElement("SlowTimeToChangePseudonym").addValue(Vehicle.getTIME_TO_PSEUDONYM_CHANGE());
            settings.addElement("SlowSpeedLimit").addValue(Vehicle.getSLOW_SPEED_LIMIT());

            settings.addElement("idsActivated").addValue(Vehicle.isIdsActivated());
            settings.addElement("beaconsLogged").addValue(KnownVehicle.getAmountOfSavedBeacons_());
            settings.addElement("fakeMessageInterval").addValue(Vehicle.getFakeMessagesInterval_());

            settings.addElement("SpamDetection").addValue(KnownEventSource.isSpamcheck());
            settings.addElement("SpamMessageThreshold").addCharacters(KnownEventSource.getSpammingthreshold() + "");
            settings.addElement("SpamTimeThreshold").addCharacters(KnownEventSource.getSpammingtimethreshold() + "");

            settings.addElement("TrafficModel").addValue(WorkerThread.getSimulationMode_());

            String activatedRules = "";

            settings.addElement("activeRules").addCharacters(activatedRules);

            if (RSU.isShowEncryptedBeaconsInMix_()) settings.addElement("showEncryptedCommInMix").addCharacters("true");
            else settings.addElement("showEncryptedCommInMix").addCharacters("false");
            SMOutputElement vehicles = root.addElement("Vehicles");

            for (i = 0; i < Region_cnt_x; ++i) {
                for (j = 0; j < Region_cnt_y; ++j) {
                    vehiclesArray = Regions[i][j].getVehicleArray();
                    for (k = 0; k < vehiclesArray.length; ++k) {
                        vehicle = vehiclesArray[k];
                        level1 = vehicles.addElement("Vehicle");
                        level1.addElement("VehicleLength").addValue(vehicle.getVehicleLength());
                        level1.addElement("MaxSpeed").addValue(vehicle.getMaxSpeed());
                        level1.addElement("MaxCommDist").addValue(vehicle.getMaxCommDistance());
                        level1.addElement("Wifi").addValue(vehicle.isWiFiEnabled());
                        level1.addElement("emergencyVehicle").addValue(vehicle.isEmergencyVehicle());
                        level1.addElement("braking_rate").addValue(vehicle.getBrakingRate());
                        level1.addElement("acceleration_rate").addValue(vehicle.getAccelerationRate());
                        level1.addElement("timeDistance").addValue(vehicle.getTimeDistance());
                        level1.addElement("politeness").addValue(vehicle.getPoliteness());
                        level1.addElement("speeddeviation").addValue(vehicle.getSpeedDeviation_());
                        level1.addElement("Color").addValue(vehicle.getColor().getRGB());
                        level1.addElement("isFakingMessages").addValue(vehicle.isFakingMessages());
                        level1.addElement("fakingMessageType").addCharacters(vehicle.getFakeMessageType());
                        if (Renderer.getInstance().getAttackerVehicle() == vehicle)
                            level1.addElement("isAttacker").addValue(true);
                        else level1.addElement("isAttacker").addValue(false);
                        if (Renderer.getInstance().getAttackedVehicle() == vehicle)
                            level1.addElement("isAttacked").addValue(true);
                        else level1.addElement("isAttacked").addValue(false);
                        level2 = level1.addElement("Destinations");

                        level3 = level2.addElement("WayPoint");
                        level3.addElement("x").addValue(vehicle.getX());
                        level3.addElement("y").addValue(vehicle.getY());
                        level3.addElement("wait").addValue(vehicle.getWaittime());
                        destinations = vehicle.getDestinations();
                        wayPointIterator = destinations.iterator();
                        while (wayPointIterator.hasNext()) {
                            wayPoint = wayPointIterator.next();
                            level3 = level2.addElement("WayPoint");
                            level3.addElement("x").addValue(wayPoint.getX());
                            level3.addElement("y").addValue(wayPoint.getY());
                            level3.addElement("wait").addValue(wayPoint.getWaittime());
                        }

                    }
                }
            }

            SMOutputElement mixZones = root.addElement("MixZones");

            for (i = 0; i < Region_cnt_x; ++i) {
                for (j = 0; j < Region_cnt_y; ++j) {
                    mixZoneArray = Regions[i][j].getMixZoneNodes();
                    for (k = 0; k < mixZoneArray.length; ++k) {
                        mixNode = mixZoneArray[k];
                        level1 = mixZones.addElement("MixNode");
                        level1.addElement("x").addValue(mixNode.getX());
                        level1.addElement("y").addValue(mixNode.getY());
                        level1.addElement("radius").addValue(mixNode.getMixZoneRadius());
                        if (mixNode.getEncryptedRSU_() != null)
                            level1.addElement("hasRSU").addValue(true);
                        else level1.addElement("hasRSU").addValue(false);

                    }
                }
            }

            SMOutputElement rsus = root.addElement("RSUs");

            for (i = 0; i < Region_cnt_x; ++i) {
                for (j = 0; j < Region_cnt_y; ++j) {
                    rsuArray = Regions[i][j].getRSUs();
                    for (k = 0; k < rsuArray.length; ++k) {
                        rsu = rsuArray[k];
                        if (!rsu.isEncrypted_()) {
                            level1 = rsus.addElement("RSU");
                            level1.addElement("x").addValue(rsu.getX());
                            level1.addElement("y").addValue(rsu.getY());
                            level1.addElement("radius").addValue(rsu.getWifiRadius());
                            level1.addElement("isEncrypted").addValue(rsu.isEncrypted_());
                        }
                    }
                }
            }

            SMOutputElement arsus = root.addElement("ARSUs");

            SMOutputElement events = root.addElement("Events");
            Iterator<Event> eventIterator = EventList.getInstance().getIterator();
            while (eventIterator.hasNext()) {
                event = eventIterator.next();
                level1 = events.addElement("Event");
                level1.addElement("Time").addValue(event.getTime());
                if (event.getClass() == StartBlocking.class) {
                    level1.addElement("Type").addCharacters("startBlocking");
                    level1.addElement("x").addValue(((StartBlocking) event).getX());
                    level1.addElement("y").addValue(((StartBlocking) event).getY());
                    level1.addElement("Direction").addValue(((StartBlocking) event).getAffectedDirection());
                    level1.addElement("Lanes").addValue(((StartBlocking) event).getAffectedLanes());
                    level1.addElement("isFake").addValue(((StartBlocking) event).isFake_());
                    level1.addElement("PenaltyType").addCharacters(((StartBlocking) event).getPenaltyType_());
                } else if (event.getClass() == StopBlocking.class) {
                    level1.addElement("Type").addCharacters("stopBlocking");
                    level1.addElement("x").addValue(((StopBlocking) event).getX());
                    level1.addElement("y").addValue(((StopBlocking) event).getY());
                }
            }

            SMOutputElement eventspots = root.addElement("EventSpots");

            EventSpot tmpSpot = EventSpotList.getInstance().getHead_();
            while (tmpSpot != null) {
                level1 = eventspots.addElement("EventSpot");
                level1.addElement("x").addValue(tmpSpot.getX_());
                level1.addElement("y").addValue(tmpSpot.getY_());
                level1.addElement("seed").addValue(tmpSpot.getSeed_());
                level1.addElement("frequency").addValue(tmpSpot.getFrequency_());
                level1.addElement("radius").addValue(tmpSpot.getRadius_());
                level1.addElement("eventSpotType").addCharacters(tmpSpot.getEventSpotType_());
                tmpSpot = tmpSpot.getNext_();
            }

            doc.closeRoot();
            xw.close();
            filestream.close();
        } catch (Exception e) {

        }
        Application.setProgressBar(false);
    }

    public boolean getReadyState() {
        return ready_;
    }

    public void setReadyState(boolean ready) {
        ready_ = ready;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }
}