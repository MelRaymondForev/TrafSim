package com.fifthgen.trafficsim.scenario;

import com.fifthgen.trafficsim.gui.Renderer;
import com.fifthgen.trafficsim.map.Map;
import com.fifthgen.trafficsim.map.Node;
import com.fifthgen.trafficsim.map.Region;
import com.fifthgen.trafficsim.map.Street;
import com.fifthgen.trafficsim.scenario.messages.Message;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.HashMap;

public final class RSU {

    private static final Map MAP = Map.getInstance();

    private static final int KNOWN_VEHICLES_TIMEOUT_CHECKINTERVAL = 1000;

    public static ArrayDeque<Vehicle> coloredVehicles = new ArrayDeque<Vehicle>();
    public static RSU lastSender = null;

    public static boolean colorCleared = false;

    private static int counter_ = 1;

    private static int communicationInterval_ = Vehicle.getCommunicationInterval();

    private static int beaconInterval_ = Vehicle.getBeaconInterval();

    private static boolean communicationEnabled_ = Vehicle.getCommunicationEnabled();

    private static boolean beaconsEnabled_ = Vehicle.getBeaconsEnabled();

    private static Region[][] regions_;

    private static boolean beaconMonitorEnabled_ = false;

    private static int beaconMonitorMinX_ = -1;

    private static int beaconMonitorMaxX_ = -1;

    private static int beaconMonitorMinY_ = -1;

    private static int beaconMonitorMaxY_ = -1;

    private static boolean showEncryptedBeaconsInMix_ = false;

    private final long rsuID_;

    private final int x_;

    private final int y_;

    private final int wifiRadius_;

    private final boolean isEncrypted_;

    private final KnownMessages knownMessages_ = new KnownMessages();

    private final KnownVehiclesList knownVehiclesList_ = new KnownVehiclesList();

    private Region region_;

    private int beaconCountdown_;

    private int communicationCountdown_;

    private int knownVehiclesTimeoutCountdown_;

    private Vehicle[] vehicleBehind_;

    private Vehicle[] vehicleFront_;

    private Vehicle[] vehicleToward_;

    public RSU(int x, int y, int radius, boolean isEncrypted) {
        x_ = x;
        y_ = y;
        wifiRadius_ = radius;
        isEncrypted_ = isEncrypted;
        rsuID_ = counter_;
        ++counter_;

        beaconCountdown_ = Math.round(x_) % beaconInterval_;
        communicationCountdown_ = Math.round(x_) % communicationInterval_;
        knownVehiclesTimeoutCountdown_ = Math.round(x_) % KNOWN_VEHICLES_TIMEOUT_CHECKINTERVAL;
    }

    public static void setRegions(Region[][] regions) {
        regions_ = regions;
    }

    public static void setBeaconsEnabled(boolean state) {
        beaconsEnabled_ = state;
    }

    public static void setCommunicationEnabled(boolean state) {
        communicationEnabled_ = state;
    }

    public static void setCommunicationInterval(int communicationInterval) {
        communicationInterval_ = communicationInterval;
    }

    public static void setBeaconInterval(int beaconInterval) {
        beaconInterval_ = beaconInterval;
    }

    public static void setBeaconMonitorZoneEnabled(boolean beaconMonitorEnabled) {
        beaconMonitorEnabled_ = beaconMonitorEnabled;
    }

    public static void setMonitoredMixZoneVariables(int beaconMonitorMinX, int beaconMonitorMaxX, int beaconMonitorMinY, int beaconMonitorMaxY) {
        beaconMonitorMinX_ = beaconMonitorMinX;
        beaconMonitorMaxX_ = beaconMonitorMaxX;
        beaconMonitorMinY_ = beaconMonitorMinY;
        beaconMonitorMaxY_ = beaconMonitorMaxY;
    }

    public static boolean isShowEncryptedBeaconsInMix_() {
        return showEncryptedBeaconsInMix_;
    }

    public static void setShowEncryptedBeaconsInMix_(
            boolean showEncryptedBeaconsInMix_) {
        RSU.showEncryptedBeaconsInMix_ = showEncryptedBeaconsInMix_;
    }

    public void sendMessages() {
        communicationCountdown_ += communicationInterval_;

        Message[] messages = knownMessages_.getForwardMessages();
        int messageSize = knownMessages_.getSize();

        int i, j, k, size, MapMinX, MapMinY, MapMaxX, MapMaxY, RegionMinX, RegionMinY, RegionMaxX, RegionMaxY;
        Vehicle[] vehicles = null;
        Vehicle vehicle = null;

        long tmp = x_ - wifiRadius_;
        if (tmp < 0) MapMinX = 0;
        else if (tmp < Integer.MAX_VALUE) MapMinX = (int) tmp;
        else MapMinX = Integer.MAX_VALUE;

        tmp = x_ + (long) wifiRadius_;
        if (tmp < 0) MapMaxX = 0;
        else if (tmp < Integer.MAX_VALUE) MapMaxX = (int) tmp;
        else MapMaxX = Integer.MAX_VALUE;

        tmp = y_ - wifiRadius_;
        if (tmp < 0) MapMinY = 0;
        else if (tmp < Integer.MAX_VALUE) MapMinY = (int) tmp;
        else MapMinY = Integer.MAX_VALUE;

        tmp = y_ + (long) wifiRadius_;
        if (tmp < 0) MapMaxY = 0;
        else if (tmp < Integer.MAX_VALUE) MapMaxY = (int) tmp;
        else MapMaxY = Integer.MAX_VALUE;

        Region tmpregion = MAP.getRegionOfPoint(MapMinX, MapMinY);
        RegionMinX = tmpregion.getX();
        RegionMinY = tmpregion.getY();

        tmpregion = MAP.getRegionOfPoint(MapMaxX, MapMaxY);
        RegionMaxX = tmpregion.getX();
        RegionMaxY = tmpregion.getY();
        long maxCommDistanceSquared = (long) wifiRadius_ * wifiRadius_;
        long dx, dy;

        int sendCount = 0;

        for (i = RegionMinX; i <= RegionMaxX; ++i) {
            for (j = RegionMinY; j <= RegionMaxY; ++j) {
                vehicles = regions_[i][j].getVehicleArray();
                size = vehicles.length;
                for (k = 0; k < size; ++k) {
                    vehicle = vehicles[k];

                    if (vehicle.isWiFiEnabled() && vehicle.isActive() && vehicle.getX() >= MapMinX && vehicle.getX() <= MapMaxX && vehicle.getY() >= MapMinY && vehicle.getY() <= MapMaxY) {
                        dx = vehicle.getX() - x_;
                        dy = vehicle.getY() - y_;
                        ++sendCount;
                        if ((dx * dx + dy * dy) <= maxCommDistanceSquared) {
                            for (int l = messageSize - 1; l > -1; --l) {
                                vehicle.receiveMessage(x_, y_, messages[l]);
                            }
                        }
                    }
                }
            }
        }
        if (sendCount > 0) knownMessages_.deleteForwardMessage(i, true);
    }

    public void sendBeacons() {
        beaconCountdown_ += beaconInterval_;

        int i, j, k, size, MapMinX, MapMinY, MapMaxX, MapMaxY, RegionMinX, RegionMinY, RegionMaxX, RegionMaxY;
        Vehicle[] vehicles = null;
        Vehicle vehicle = null;

        long tmp = x_ - wifiRadius_;
        if (tmp < 0) MapMinX = 0;
        else if (tmp < Integer.MAX_VALUE) MapMinX = (int) tmp;
        else MapMinX = Integer.MAX_VALUE;

        tmp = x_ + (long) wifiRadius_;
        if (tmp < 0) MapMaxX = 0;
        else if (tmp < Integer.MAX_VALUE) MapMaxX = (int) tmp;
        else MapMaxX = Integer.MAX_VALUE;

        tmp = y_ - wifiRadius_;
        if (tmp < 0) MapMinY = 0;
        else if (tmp < Integer.MAX_VALUE) MapMinY = (int) tmp;
        else MapMinY = Integer.MAX_VALUE;

        tmp = y_ + (long) wifiRadius_;
        if (tmp < 0) MapMaxY = 0;
        else if (tmp < Integer.MAX_VALUE) MapMaxY = (int) tmp;
        else MapMaxY = Integer.MAX_VALUE;

        Region tmpregion = MAP.getRegionOfPoint(MapMinX, MapMinY);
        RegionMinX = tmpregion.getX();
        RegionMinY = tmpregion.getY();

        tmpregion = MAP.getRegionOfPoint(MapMaxX, MapMaxY);
        RegionMaxX = tmpregion.getX();
        RegionMaxY = tmpregion.getY();
        long maxCommDistanceSquared = (long) wifiRadius_ * wifiRadius_;
        long dx, dy;

        for (i = RegionMinX; i <= RegionMaxX; ++i) {
            for (j = RegionMinY; j <= RegionMaxY; ++j) {
                vehicles = regions_[i][j].getVehicleArray();
                size = vehicles.length;
                for (k = 0; k < size; ++k) {
                    vehicle = vehicles[k];

                    if (vehicle.isWiFiEnabled() && vehicle.isActive() && vehicle.getX() >= MapMinX && vehicle.getX() <= MapMaxX && vehicle.getY() >= MapMinY && vehicle.getY() <= MapMaxY) {
                        dx = vehicle.getX() - x_;
                        dy = vehicle.getY() - y_;
                        if ((dx * dx + dy * dy) <= maxCommDistanceSquared) {
                            vehicle.getKnownRSUsList().updateRSU(this, rsuID_, x_, y_, isEncrypted_);
                        }
                    }
                }
            }
        }
    }

    public void sendEncryptedBeacons() {
        beaconCountdown_ += beaconInterval_;

        if (lastSender != null && this.equals(lastSender)) {
            for (Vehicle v : coloredVehicles) v.setColor(Color.black);
            coloredVehicles.clear();
        }

        long[] maxCommDistanceSquaredFront = new long[6];
        long[] maxCommDistanceSquaredBehind = new long[6];
        long[] maxCommDistanceSquaredToward = new long[6];

        for (int l = 0; l < maxCommDistanceSquaredFront.length; l++) {
            maxCommDistanceSquaredFront[l] = 1000000000;
            maxCommDistanceSquaredBehind[l] = 1000000000;
            maxCommDistanceSquaredToward[l] = 1000000000;
        }

        vehicleBehind_ = new Vehicle[6];
        vehicleFront_ = new Vehicle[6];
        vehicleToward_ = new Vehicle[6];

        long tmpCommDistanceSquared = 0;

        long dx, dy;

        Node nodeJunction;
        Node nodeFront;
        Node nodeBehind;
        Node tmpNode;

        long distanceSenderToNodeFront, senderDxFront, senderDyFront, distanceSenderToNodeBehind, senderDxBehind, senderDyBehind, distanceRecipientToNodeFront, recipientDxFront, recipientDyFront, distanceRecipientToNodeBehind, recipientDxBehind, recipientDyBehind, distanceRecipientToNodeJunction, nodeJunctionDx, nodeJunctionDy, tmpDx, tmpDy, dxMix = 0, dyMix = 0;

        KnownVehicle[] senderHeads = knownVehiclesList_.getFirstKnownVehicle();
        Vehicle senderVehicle = null;
        KnownVehicle senderNext;

        KnownVehicle[] recipientHeads = knownVehiclesList_.getFirstKnownVehicle();
        Vehicle recipientVehicle = null;
        KnownVehicle recipientNext;

        long radiusSquared = 0;
        int mixRadius;

        HashMap<String, Vehicle> tmpVehicles = new HashMap<String, Vehicle>();

        for (int j = 0; j < senderHeads.length; ++j) {
            senderNext = senderHeads[j];
            while (senderNext != null) {
                senderVehicle = senderNext.getVehicle();

                tmpVehicles.clear();

                for (int l = 0; l < maxCommDistanceSquaredFront.length; l++) {
                    maxCommDistanceSquaredFront[l] = 1000000000;
                    maxCommDistanceSquaredBehind[l] = 1000000000;
                    maxCommDistanceSquaredToward[l] = 1000000000;
                    vehicleBehind_[l] = null;
                    vehicleFront_[l] = null;
                    vehicleToward_[l] = null;
                }

                if (senderVehicle.curDirection_) {
                    nodeFront = senderVehicle.curStreet_.getEndNode();
                    nodeBehind = senderVehicle.curStreet_.getStartNode();
                } else {
                    nodeFront = senderVehicle.curStreet_.getStartNode();
                    nodeBehind = senderVehicle.curStreet_.getEndNode();
                }

                nodeJunction = nodeFront;
                boolean junctionFound = false;
                Street[] tmpStreets = senderVehicle.getRouteStreets();

                if (senderVehicle.getCurMixNode_() != null) {
                    mixRadius = senderVehicle.getCurMixNode_().getMixZoneRadius();

                    radiusSquared = mixRadius * mixRadius;

                    dxMix = senderVehicle.getCurMixNode_().getX() - senderVehicle.getX();
                    dyMix = senderVehicle.getCurMixNode_().getY() - senderVehicle.getY();
                }

                for (int i = senderVehicle.getRoutePosition(); i < tmpStreets.length; i++) {
                    if (radiusSquared < (dxMix * dxMix + dyMix * dyMix)) break;

                    if (nodeJunction.getCrossingStreetsCount() > 2) {
                        junctionFound = true;
                        break;
                    }
                    if (senderVehicle.curDirection_) {
                        nodeJunction = tmpStreets[i].getEndNode();
                    } else {
                        nodeJunction = tmpStreets[i].getStartNode();
                    }
                }

                if (!junctionFound) {
                    nodeJunction = null;
                }

                senderDxFront = senderVehicle.getX() - nodeFront.getX();
                senderDyFront = senderVehicle.getY() - nodeFront.getY();

                distanceSenderToNodeFront = senderDxFront * senderDxFront + senderDyFront * senderDyFront;

                senderDxBehind = senderVehicle.getX() - nodeBehind.getX();
                senderDyBehind = senderVehicle.getY() - nodeBehind.getY();

                distanceSenderToNodeBehind = senderDxBehind * senderDxBehind + senderDyBehind * senderDyBehind;

                for (int i = 0; i < recipientHeads.length; ++i) {

                    recipientNext = recipientHeads[i];
                    while (recipientNext != null) {

                        recipientVehicle = recipientNext.getVehicle();

                        if (!recipientVehicle.equals(senderVehicle)) {

                            recipientDxFront = recipientVehicle.getX() - nodeFront.getX();
                            recipientDyFront = recipientVehicle.getY() - nodeFront.getY();

                            distanceRecipientToNodeFront = recipientDxFront * recipientDxFront + recipientDyFront * recipientDyFront;

                            dx = senderVehicle.getX() - recipientVehicle.getX();
                            dy = senderVehicle.getY() - recipientVehicle.getY();

                            tmpCommDistanceSquared = dx * dx + dy * dy;

                            recipientDxBehind = recipientVehicle.getX() - nodeBehind.getX();
                            recipientDyBehind = recipientVehicle.getY() - nodeBehind.getY();

                            distanceRecipientToNodeBehind = recipientDxBehind * recipientDxBehind + recipientDyBehind * recipientDyBehind;

                            if (senderVehicle.curStreet_.getName().equals(recipientVehicle.curStreet_.getName())) {

                                if (senderVehicle.curDirection_ == recipientVehicle.curDirection_) {

                                    if (distanceSenderToNodeFront < distanceRecipientToNodeFront &&
                                            tmpCommDistanceSquared < distanceRecipientToNodeFront) {

                                        if (tmpCommDistanceSquared <= maxCommDistanceSquaredBehind[recipientVehicle.curLane_]) {
                                            maxCommDistanceSquaredBehind[recipientVehicle.curLane_] = tmpCommDistanceSquared;
                                            vehicleBehind_[recipientVehicle.curLane_] = recipientVehicle;
                                        }
                                    } else if (distanceSenderToNodeBehind < distanceRecipientToNodeBehind &&
                                            tmpCommDistanceSquared < distanceRecipientToNodeBehind) {

                                        if (tmpCommDistanceSquared <= maxCommDistanceSquaredFront[recipientVehicle.curLane_]) {
                                            maxCommDistanceSquaredFront[recipientVehicle.curLane_] = tmpCommDistanceSquared;
                                            vehicleFront_[recipientVehicle.curLane_] = recipientVehicle;
                                        }
                                    }
                                } else {

                                    if (distanceSenderToNodeBehind < distanceRecipientToNodeBehind &&
                                            tmpCommDistanceSquared < distanceRecipientToNodeBehind) {

                                        if (tmpCommDistanceSquared <= maxCommDistanceSquaredToward[recipientVehicle.curLane_]) {
                                            maxCommDistanceSquaredToward[recipientVehicle.curLane_] = tmpCommDistanceSquared;
                                            vehicleToward_[recipientVehicle.curLane_] = recipientVehicle;
                                        }
                                    }
                                }
                            }

                            if (junctionFound) {

                                nodeJunctionDx = nodeJunction.getX() - recipientVehicle.getX();
                                nodeJunctionDy = nodeJunction.getY() - recipientVehicle.getY();

                                distanceRecipientToNodeJunction = nodeJunctionDx * nodeJunctionDx + nodeJunctionDy * nodeJunctionDy;

                                tmpStreets = recipientVehicle.getRouteStreets();
                                tmpNode = null;
                                Boolean willPassJunction = false;
                                for (int p = recipientVehicle.getRoutePosition(); p < tmpStreets.length; p++) {
                                    if (recipientVehicle.curDirection_) {
                                        tmpNode = tmpStreets[p].getEndNode();
                                    } else {
                                        tmpNode = tmpStreets[p].getStartNode();
                                    }
                                    if (tmpNode.equals(nodeJunction)) willPassJunction = true;
                                }

                                boolean isCrossingStreet = false;
                                for (Street s : nodeJunction.getCrossingStreets()) {
                                    if (s.getName().equals(recipientVehicle.getCurStreet().getName()))
                                        isCrossingStreet = true;
                                }

                                if (isCrossingStreet) {

                                    if (willPassJunction && !(senderVehicle.getCurStreet().getName() + senderVehicle.curDirection_).equals(recipientVehicle.getCurStreet().getName() + recipientVehicle.curDirection_) &&
                                            (distanceSenderToNodeBehind < distanceRecipientToNodeBehind && tmpCommDistanceSquared < distanceRecipientToNodeBehind)) {
                                        if (tmpVehicles.containsKey(recipientVehicle.getCurStreet().getName() + recipientVehicle.curLane_ + recipientVehicle.curDirection_)) {
                                            tmpDx = nodeJunction.getX() - tmpVehicles.get(recipientVehicle.getCurStreet().getName() + recipientVehicle.curLane_ + recipientVehicle.curDirection_).getX();
                                            tmpDy = nodeJunction.getY() - tmpVehicles.get(recipientVehicle.getCurStreet().getName() + recipientVehicle.curLane_ + recipientVehicle.curDirection_).getY();
                                            if ((tmpDx * tmpDx + tmpDy * tmpDy) > distanceRecipientToNodeJunction) {
                                                tmpVehicles.put(recipientVehicle.getCurStreet().getName() + recipientVehicle.curLane_ + recipientVehicle.curDirection_, recipientVehicle);
                                            }
                                        } else {
                                            tmpVehicles.put(recipientVehicle.getCurStreet().getName() + recipientVehicle.curLane_ + recipientVehicle.curDirection_, recipientVehicle);
                                        }
                                    }
                                }
                            }
                        }

                        recipientNext = recipientNext.getNext();
                    }
                }

                for (int k = 0; k < vehicleBehind_.length; k++) {
                    if (vehicleBehind_[k] != null) {
                        vehicleBehind_[k].getKnownVehiclesList().updateVehicle(senderVehicle, senderVehicle.getID(), senderVehicle.getX(), senderVehicle.getY(), senderVehicle.getCurSpeed(), rsuID_, true, false);
                        if (senderVehicle.equals(Renderer.getInstance().getMarkedVehicle()) && showEncryptedBeaconsInMix_) {
                            coloredVehicles.add(vehicleBehind_[k]);
                            vehicleBehind_[k].setColor(Color.red);
                            lastSender = this;
                        }
                    }
                    if (vehicleFront_[k] != null) {
                        vehicleFront_[k].getKnownVehiclesList().updateVehicle(senderVehicle, senderVehicle.getID(), senderVehicle.getX(), senderVehicle.getY(), senderVehicle.getCurSpeed(), rsuID_, true, false);
                        if (senderVehicle.equals(Renderer.getInstance().getMarkedVehicle()) && showEncryptedBeaconsInMix_) {
                            coloredVehicles.add(vehicleFront_[k]);
                            vehicleFront_[k].setColor(Color.red);
                            lastSender = this;
                        }
                    }
                    if (vehicleToward_[k] != null) {
                        vehicleToward_[k].getKnownVehiclesList().updateVehicle(senderVehicle, senderVehicle.getID(), senderVehicle.getX(), senderVehicle.getY(), senderVehicle.getCurSpeed(), rsuID_, true, false);
                        if (senderVehicle.equals(Renderer.getInstance().getMarkedVehicle()) && showEncryptedBeaconsInMix_) {
                            coloredVehicles.add(vehicleToward_[k]);
                            vehicleToward_[k].setColor(Color.red);
                            lastSender = this;
                        }
                    }
                }
                for (Vehicle v : tmpVehicles.values()) {
                    v.getKnownVehiclesList().updateVehicle(senderVehicle, senderVehicle.getID(), senderVehicle.getX(), senderVehicle.getY(), senderVehicle.getCurSpeed(), rsuID_, true, false);
                    if (senderVehicle.equals(Renderer.getInstance().getMarkedVehicle()) && showEncryptedBeaconsInMix_) {
                        coloredVehicles.add(v);
                        v.setColor(Color.red);
                        lastSender = this;
                    }
                }
                senderNext = senderNext.getNext();
            }
        }
    }

    public final void receiveMessage(int sourceX, int sourceY, Message message) {

        message.setFloodingMode(true);

        knownMessages_.addMessage(message, false, true);
    }

    public void cleanup(int timePerStep) {

        if (communicationEnabled_) {
            if (knownMessages_.hasNewMessages()) knownMessages_.processMessages();
            communicationCountdown_ -= timePerStep;
            if (communicationCountdown_ < 1) knownMessages_.checkOutdatedMessages(true);

            if (beaconsEnabled_) beaconCountdown_ -= timePerStep;
        }
        if (beaconsEnabled_) {
            beaconCountdown_ -= timePerStep;

            if (knownVehiclesTimeoutCountdown_ < 1) {
                knownVehiclesList_.checkOutdatedVehicles();
                knownVehiclesTimeoutCountdown_ += KNOWN_VEHICLES_TIMEOUT_CHECKINTERVAL;
            } else knownVehiclesTimeoutCountdown_ -= timePerStep;
        }
    }

    public long getRSUID() {
        return rsuID_;
    }

    public int getX() {
        return x_;
    }

    public int getY() {
        return y_;
    }

    public Region getRegion() {
        return region_;
    }

    public void setRegion(Region region) {
        region_ = region;
    }

    public int getWifiRadius() {
        return wifiRadius_;
    }

    public int getBeaconCountdown() {
        return beaconCountdown_;
    }

    public int getCommunicationCountdown() {
        return communicationCountdown_;
    }

    public boolean isEncrypted_() {
        return isEncrypted_;
    }

    public KnownVehiclesList getKnownVehiclesList_() {
        return knownVehiclesList_;
    }
}