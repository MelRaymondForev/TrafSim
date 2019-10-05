package com.fifthgen.trafficsim.routing;

import com.fifthgen.trafficsim.localization.Messages;
import com.fifthgen.trafficsim.map.MapHelper;
import com.fifthgen.trafficsim.map.Street;

import java.text.ParseException;

public final class WayPoint {

    private final int x_;

    private final int y_;

    private final Street street_;

    private final double positionOnStreet_;

    private int waitTime_;

    public WayPoint(int x, int y, int waitTime) throws ParseException {

        int[] nearestpoint = new int[2];
        street_ = MapHelper.findNearestStreet(x, y, 10000, new double[1], nearestpoint);
        if (street_ != null) {
            x_ = nearestpoint[0];
            y_ = nearestpoint[1];

            long tmp1 = street_.getStartNode().getX() - x_;
            long tmp2 = street_.getStartNode().getY() - y_;
            positionOnStreet_ = Math.sqrt(tmp1 * tmp1 + tmp2 * tmp2);
            waitTime_ = waitTime;
        } else throw new ParseException(Messages.getString("WayPoint.snappingFailed"), 0);
    }

    public double getPositionOnStreet() {
        return positionOnStreet_;
    }

    public Street getStreet() {
        return street_;
    }

    public int getWaittime() {
        return waitTime_;
    }

    public void setWaittime(int waitTime) {
        waitTime_ = waitTime;
    }

    public int getX() {
        return x_;
    }

    public int getY() {
        return y_;
    }
}