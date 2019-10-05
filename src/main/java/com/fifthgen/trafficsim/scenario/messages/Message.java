package com.fifthgen.trafficsim.scenario.messages;

import com.fifthgen.trafficsim.gui.Renderer;
import com.fifthgen.trafficsim.scenario.Vehicle;

public abstract class Message {

    private static final Renderer renderer_ = Renderer.getInstance();

    protected int destinationX_;

    protected int destinationY_;

    protected int destinationRadius_;

    protected long destinationRadiusSquared_;

    protected int validUntil_;

    protected boolean isFake_;

    protected long ID_;

    private boolean floodingMode_ = false;

    public boolean isValid() {
        return renderer_.getTimePassed() < validUntil_;
    }

    public int getDestinationX_() {
        return destinationX_;
    }

    public int getDestinationY_() {
        return destinationY_;
    }

    public boolean getFloodingMode() {
        return floodingMode_;
    }

    public void setFloodingMode(boolean mode) {
        floodingMode_ = mode;
    }

    public int getDestinationRadius() {
        return destinationRadius_;
    }

    public long getDestinationRadiusSquared() {
        return destinationRadiusSquared_;
    }

    public abstract void execute(Vehicle vehicle);

    public boolean isFake_() {
        return isFake_;
    }

    public void setFake_(boolean isFake_) {
        this.isFake_ = isFake_;
    }

}