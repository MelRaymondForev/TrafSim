package com.fifthgen.trafficsim.scenario;

public class KnownRSU {

    private final RSU rsu_;

    private final long ID_;

    protected KnownRSU previous_;

    protected KnownRSU next_;

    private int x_;

    private int y_;

    private boolean isEncrypted_;

    private int lastUpdate_;

    public KnownRSU(RSU rsu, long ID, int x, int y, boolean isEncrypted, int time) {
        rsu_ = rsu;
        ID_ = ID;
        x_ = x;
        y_ = y;
        isEncrypted_ = isEncrypted;
        lastUpdate_ = time;
    }

    public int getX() {
        return x_;
    }

    public void setX(int x) {
        x_ = x;
    }

    public int getY() {
        return y_;
    }

    public void setY(int y) {
        y_ = y;
    }

    public long getID() {
        return ID_;
    }

    public RSU getRSU() {
        return rsu_;
    }

    public int getLastUpdate() {
        return lastUpdate_;
    }

    public void setLastUpdate(int time) {
        lastUpdate_ = time;
    }

    public KnownRSU getNext() {
        return next_;
    }

    public void setNext(KnownRSU next) {
        next_ = next;
    }

    public KnownRSU getPrevious() {
        return previous_;
    }

    public void setPrevious(KnownRSU previous) {
        previous_ = previous;
    }

    public boolean isEncrypted() {
        return isEncrypted_;
    }

    public void setEncrypted(boolean isEncrypted) {
        this.isEncrypted_ = isEncrypted;
    }
}