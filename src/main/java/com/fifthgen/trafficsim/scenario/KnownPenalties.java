package com.fifthgen.trafficsim.scenario;

import com.fifthgen.trafficsim.gui.Renderer;
import com.fifthgen.trafficsim.gui.helpers.EventLogWriter;
import com.fifthgen.trafficsim.map.Street;
import com.fifthgen.trafficsim.scenario.events.EventList;
import com.fifthgen.trafficsim.scenario.events.StartBlocking;
import com.fifthgen.trafficsim.scenario.events.StopBlocking;

public class KnownPenalties {

    private static boolean logEvents_ = false;

    private static boolean spamCheck_ = false;

    private final Vehicle vehicle_;

    private Street[] streets_;

    private int[] directions_;

    private int[] penalties_;

    private int[] validUntil_;

    private boolean[] isFake_;

    private String[] penaltyType_;

    private boolean[] routeUpdateNecessary_;

    private int size = 0;

    private int[] x_;

    private int[] y_;

    private int[] lane_;
    private Vehicle[] penaltySourceVehicle_;

    public KnownPenalties(Vehicle vehicle) {
        vehicle_ = vehicle;

        streets_ = new Street[2];
        directions_ = new int[2];
        penalties_ = new int[2];
        validUntil_ = new int[2];
        routeUpdateNecessary_ = new boolean[2];
        isFake_ = new boolean[2];
        penaltyType_ = new String[2];
        x_ = new int[2];
        y_ = new int[2];
        lane_ = new int[2];
        penaltySourceVehicle_ = new Vehicle[2];
    }

    public static boolean isLogEvents_() {
        return logEvents_;
    }

    public static void setLogEvents_(boolean logEvents) {
        logEvents_ = logEvents;
    }

    public static boolean isSpamcheck() {
        return spamCheck_;
    }

    public static void setSpamCheck_(boolean spamCheck_) {
        KnownPenalties.spamCheck_ = spamCheck_;
    }

    public synchronized void updatePenalty(int x, int y, Street street, int lane, int direction, int penalty, int validUntil, boolean isFake, String penaltyType, long ID, Vehicle penaltySourceVehicle, boolean emergencyVehicle, boolean createBlocking) {
        boolean found = false;
        boolean activateIDS = false;
        boolean otherPenaltyValue = false;
        boolean reallySamePenalty = false;

        if (spamCheck_) {

            if (penaltyType.equals("HUANG_EEBL")) {
                vehicle_.getKnownEventSourcesList_().update(penaltySourceVehicle, ID, 0, 0, 0, false);
            }
        }

        for (int i = 0; i < streets_.length; ++i) {
            if (streets_[i] == street && directions_[i] == direction && isFake_[i] == isFake && penaltyType_[i] == penaltyType) {
                found = true;
                if (penalties_[i] != penalty) otherPenaltyValue = true;

                penalties_[i] = penalty;
                validUntil_[i] = validUntil;
                isFake_[i] = isFake;
                penaltyType_[i] = penaltyType;

                if (x_[i] == x && y_[i] == y) reallySamePenalty = true;
                break;
            }
        }

        if (!found) {
            if (size < streets_.length) {
                streets_[size] = street;
                directions_[size] = direction;
                penalties_[size] = penalty;
                validUntil_[size] = validUntil;
                isFake_[size] = isFake;
                penaltyType_[size] = penaltyType;
                x_[size] = x;
                y_[size] = y;
                penaltySourceVehicle_[size] = penaltySourceVehicle;
            } else {

                Street[] newArray = new Street[size + 2];
                System.arraycopy(streets_, 0, newArray, 0, size);
                newArray[size] = street;
                streets_ = newArray;

                int[] newArray2 = new int[size + 2];
                System.arraycopy(directions_, 0, newArray2, 0, size);
                newArray2[size] = direction;
                directions_ = newArray2;

                newArray2 = new int[size + 2];
                System.arraycopy(penalties_, 0, newArray2, 0, size);
                newArray2[size] = penalty;
                penalties_ = newArray2;

                newArray2 = new int[size + 2];
                System.arraycopy(validUntil_, 0, newArray2, 0, size);
                newArray2[size] = validUntil;
                validUntil_ = newArray2;

                newArray2 = new int[size + 2];
                System.arraycopy(x_, 0, newArray2, 0, size);
                newArray2[size] = x;
                x_ = newArray2;

                newArray2 = new int[size + 2];
                System.arraycopy(y_, 0, newArray2, 0, size);
                newArray2[size] = y;
                y_ = newArray2;

                newArray2 = new int[size + 2];
                System.arraycopy(lane_, 0, newArray2, 0, size);
                newArray2[size] = lane;
                lane_ = newArray2;

                String[] newArray4 = new String[size + 2];
                System.arraycopy(penaltyType_, 0, newArray4, 0, size);
                newArray4[size] = penaltyType;
                penaltyType_ = newArray4;

                boolean[] newArray3 = new boolean[size + 2];
                System.arraycopy(newArray3, 0, newArray3, 0, size);
                newArray3[size] = false;
                routeUpdateNecessary_ = newArray3;

                newArray3 = new boolean[size + 2];
                System.arraycopy(isFake_, 0, newArray3, 0, size);
                newArray3[size] = isFake;
                isFake_ = newArray3;

                Vehicle[] newArray5 = new Vehicle[size + 2];
                System.arraycopy(penaltySourceVehicle_, 0, newArray5, 0, size);
                newArray5[size] = penaltySourceVehicle;
                penaltySourceVehicle_ = newArray5;
            }
            ++size;
        }

        if (!found || (otherPenaltyValue && !reallySamePenalty)) {

            boolean ruleActive = false;

            if (logEvents_)
                EventLogWriter.log(Renderer.getInstance().getTimePassed() + "," + penaltyType + "," + x + "," + y + "," + ID + "," + vehicle_.getID());

        }

    }

    public void checkValidUntil() {
        int timeout = Renderer.getInstance().getTimePassed();
        boolean updateRoute = false;
        for (int i = size - 1; i > -1; --i) {
            if (validUntil_[i] < timeout) {

                if (routeUpdateNecessary_[i]) updateRoute = true;

                --size;

                System.arraycopy(streets_, i + 1, streets_, i, size - i);
                System.arraycopy(directions_, i + 1, directions_, i, size - i);
                System.arraycopy(penalties_, i + 1, penalties_, i, size - i);
                System.arraycopy(validUntil_, i + 1, validUntil_, i, size - i);
                System.arraycopy(routeUpdateNecessary_, i + 1, routeUpdateNecessary_, i, size - i);
                System.arraycopy(isFake_, i + 1, isFake_, i, size - i);
                System.arraycopy(penaltyType_, i + 1, penaltyType_, i, size - i);
                System.arraycopy(x_, i + 1, x_, i, size - i);
                System.arraycopy(y_, i + 1, y_, i, size - i);
                System.arraycopy(lane_, i + 1, lane_, i, size - i);
                System.arraycopy(penaltySourceVehicle_, i + 1, penaltySourceVehicle_, i, size - i);
            }
        }

        if (updateRoute) {
            vehicle_.calculateRoute(true, true);
        }

    }

    public boolean hasToMoveOutOfTheWay(Vehicle emergencyVehicle) {

        Street[] routeStreets = emergencyVehicle.getRouteStreets();
        boolean[] routeDirections = emergencyVehicle.getRouteDirections();
        int i = emergencyVehicle.getRoutePosition();

        Street curStreet = vehicle_.getCurStreet();
        boolean curDirection = vehicle_.getCurDirection();

        for (; i < emergencyVehicle.getRoutePosition() + 5; ++i) {
            if (routeStreets.length == i) break;
            if (routeStreets[i] == curStreet && routeDirections[i] == curDirection) return true;
        }

        return false;
    }

    public Street[] getStreets() {
        return streets_;
    }

    public int[] getDirections() {
        return directions_;
    }

    public int[] getPenalties() {
        return penalties_;
    }

    public int getSize() {
        return size;
    }

    public boolean[] getIsFake_() {
        return isFake_;
    }

    public void setIsFake_(boolean[] isFake_) {
        this.isFake_ = isFake_;
    }

    public String[] getPenaltyType_() {
        return penaltyType_;
    }

    public void setPenaltyType_(String[] penaltyType_) {
        this.penaltyType_ = penaltyType_;
    }

    public Vehicle getVehicle_() {
        return vehicle_;
    }

    public Vehicle[] getPenaltySourceVehicle_() {
        return penaltySourceVehicle_;
    }

    public void setPenaltySourceVehicle_(Vehicle[] penaltySourceVehicle_) {
        this.penaltySourceVehicle_ = penaltySourceVehicle_;
    }

    public void clear() {
        streets_ = new Street[2];
        directions_ = new int[2];
        penalties_ = new int[2];
        validUntil_ = new int[2];
        routeUpdateNecessary_ = new boolean[2];
        isFake_ = new boolean[2];
        penaltyType_ = new String[2];
        size = 0;
        x_ = new int[2];
        y_ = new int[2];
        lane_ = new int[2];
        penaltySourceVehicle_ = new Vehicle[2];
    }
}