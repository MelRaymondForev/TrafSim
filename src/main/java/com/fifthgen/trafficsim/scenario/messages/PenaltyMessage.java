package com.fifthgen.trafficsim.scenario.messages;

import com.fifthgen.trafficsim.map.Street;
import com.fifthgen.trafficsim.scenario.Vehicle;

public class PenaltyMessage extends Message {

    private final Street penaltyStreet_;

    private final int penaltyDirection_;

    private final int penaltyValue_;

    private final int penaltyValidUntil_;

    private final int x_;

    private final int y_;

    private final int lane_;

    private final boolean createBlocking_;

    private final String penaltyType_;

    private boolean emergencyVehicle_;

    private Vehicle penaltySourceVehicle_ = null;
    private boolean logData_ = false;

    public PenaltyMessage(int x, int y, int destinationX, int destinationY, int destinationRadius, int validUntil, Street penaltyStreet, int lane, int penaltyDirection, int penaltyValue, int penaltyValidUntil, boolean isFake, long ID, Vehicle penaltySourceVehicle, String penaltyType, boolean emergencyVehicle, boolean createBlocking) {
        destinationX_ = destinationX;
        destinationY_ = destinationY;
        x_ = x;
        y_ = y;
        destinationRadius_ = destinationRadius;
        destinationRadiusSquared_ = (long) destinationRadius * destinationRadius;
        validUntil_ = validUntil;
        penaltyStreet_ = penaltyStreet;
        penaltyDirection_ = penaltyDirection;
        penaltyValue_ = penaltyValue;
        penaltyValidUntil_ = penaltyValidUntil;
        isFake_ = isFake;
        ID_ = ID;
        penaltyType_ = penaltyType;
        emergencyVehicle_ = emergencyVehicle;
        lane_ = lane;
        createBlocking_ = createBlocking;
        penaltySourceVehicle_ = penaltySourceVehicle;

        if (logData_ && penaltyType.equals("HUANG_PCN")) {
            penaltySourceVehicle.setLogBeaconsAfterEvent_(true);
            penaltySourceVehicle.setBeaconString_(penaltyType + "," + isFake);
            penaltySourceVehicle.setAmountOfLoggedBeacons_(0);
        }

    }

    public void execute(Vehicle vehicle) {
        vehicle.getKnownPenalties().updatePenalty(x_, y_, penaltyStreet_, lane_, penaltyDirection_, penaltyValue_, penaltyValidUntil_, isFake_, penaltyType_, ID_, penaltySourceVehicle_, emergencyVehicle_, createBlocking_);
    }

}