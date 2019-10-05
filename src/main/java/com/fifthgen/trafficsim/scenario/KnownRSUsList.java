package com.fifthgen.trafficsim.scenario;

import com.fifthgen.trafficsim.gui.Renderer;
import com.fifthgen.trafficsim.gui.helpers.AttackLogWriter;

public class KnownRSUsList {

    private static final int VALID_TIME = 2000;

    private static final int HASH_SIZE = 16;

    private static int timePassed_ = 0;

    private KnownRSU[] head_ = new KnownRSU[HASH_SIZE];

    private int size_ = 0;

    public KnownRSUsList() {
        for (int i = 0; i < HASH_SIZE; ++i) {
            head_[i] = null;
        }
    }

    public static void setTimePassed(int time) {
        timePassed_ = time;
    }

    public synchronized void updateRSU(RSU rsu, long ID, int x, int y, boolean isEncrypted) {
        boolean found = false;
        int hash = (int) (ID % HASH_SIZE);
        if (hash < 0) hash = -hash;
        KnownRSU next = head_[hash];
        while (next != null) {
            if (next.getID() == ID) {
                next.setX(x);
                next.setY(y);
                next.setEncrypted(isEncrypted);
                next.setLastUpdate(timePassed_ + VALID_TIME);
                found = true;
                break;
            }
            next = next.getNext();
        }

        if (!found) {
            next = new KnownRSU(rsu, ID, x, y, isEncrypted, timePassed_);
            next.setNext(head_[hash]);
            next.setPrevious(null);
            if (head_[hash] != null) head_[hash].setPrevious(next);
            head_[hash] = next;
            ++size_;
        }

        AttackLogWriter.log(Renderer.getInstance().getTimePassed() + ":Any RSU Communication:" + rsu.getRSUID() + ":Any-Vehicle Data:" + Long.toHexString(ID) + ":" + x + ":" + y + ":" + isEncrypted);
    }

    public void checkOutdatedRSUs() {
        int timeout = timePassed_ - VALID_TIME;
        KnownRSU next;
        for (int i = 0; i < HASH_SIZE; ++i) {
            next = head_[i];
            while (next != null) {
                if (next.getLastUpdate() < timeout) {
                    if (next.getNext() != null) next.getNext().setPrevious(next.getPrevious());
                    if (next.getPrevious() != null) next.getPrevious().setNext(next.getNext());
                    else {
                        head_[i] = next.getNext();
                    }
                    --size_;
                }
                next = next.getNext();
            }
        }
    }

    public RSU findNearestRSU(int rsuX, int rsuY, int destX, int destY, int maxDistance) {
        double tmpDistance, bestDistance;
        long dx = rsuX - destX;
        long dy = rsuY - destY;
        long maxDistanceSquared = (long) maxDistance * maxDistance;
        bestDistance = dx * dx + dy * dy;
        KnownRSU bestKnownRSU = null;
        KnownRSU next;
        for (int i = 0; i < HASH_SIZE; ++i) {
            next = head_[i];
            while (next != null) {
                dx = next.getX() - destX;
                dy = next.getY() - destY;
                tmpDistance = dx * dx + dy * dy;
                if (tmpDistance < bestDistance && !next.isEncrypted()) {
                    dx = next.getX() - rsuX;
                    dy = next.getY() - rsuY;
                    if ((dx * dx + dy * dy) < maxDistanceSquared) {
                        bestDistance = tmpDistance;
                        bestKnownRSU = next;
                    }
                }
                next = next.getNext();
            }
        }
        if (bestKnownRSU != null) return bestKnownRSU.getRSU();
        else return null;
    }

    public KnownRSU[] getFirstKnownRSU() {
        return head_;
    }

    public int getSize() {
        return size_;
    }

    public void clear() {
        head_ = new KnownRSU[HASH_SIZE];
        size_ = 0;
    }
}