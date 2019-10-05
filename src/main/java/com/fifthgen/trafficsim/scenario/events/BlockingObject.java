package com.fifthgen.trafficsim.scenario.events;

import com.fifthgen.trafficsim.map.Street;
import com.fifthgen.trafficsim.scenario.LaneObject;
import com.fifthgen.trafficsim.scenario.Vehicle;

public class BlockingObject extends LaneObject {

    private final String penaltyType_;
    private int timestamp_;

    public BlockingObject(int lane, boolean direction, Street street, double position, String penaltyType) {
        curLane_ = lane;
        curDirection_ = direction;
        curStreet_ = street;
        penaltyType_ = penaltyType;
        if (curLane_ < 1) curLane_ = 1;
        else if (curLane_ > curStreet_.getLanesCount()) curLane_ = curStreet_.getLanesCount();
        curPosition_ = position;
        curStreet_.addLaneObject(this, curDirection_);
    }

    public BlockingObject(int lane, boolean direction, Street street, double position, String penaltyType, int timestamp, int x, int y) {
        curLane_ = lane;
        curDirection_ = direction;
        curStreet_ = street;
        penaltyType_ = penaltyType;
        if (curLane_ < 1) curLane_ = 1;
        else if (curLane_ > curStreet_.getLanesCount()) curLane_ = curStreet_.getLanesCount();
        curPosition_ = position;
        curStreet_.addLaneObject(this, curDirection_);
        timestamp_ = timestamp;
        curX_ = x;
        curY_ = y;
    }

    public void removeFromLane() {
        curStreet_.delLaneObject(this, curDirection_);
    }

    public boolean removeFromLane(Vehicle tmp, int timePassed) {
        if (timePassed > timestamp_) {
            curStreet_.delLaneObject(this, curDirection_);
            return true;
        } else return false;
    }

    public String getPenaltyType_() {
        return penaltyType_;
    }
}