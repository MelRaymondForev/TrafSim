package com.fifthgen.trafficsim.routing.astar;

import com.fifthgen.trafficsim.localization.Messages;

public final class AStarNode implements Comparable<Object> {

    private final com.fifthgen.trafficsim.map.Node realNode_;

    private boolean inOpenList_ = false;

    private boolean inClosedList_ = false;

    private double f_;

    private double g_;

    private AStarNode predecessor_ = null;

    private int counter_ = 0;

    public AStarNode(com.fifthgen.trafficsim.map.Node realNode, double f, int counter) {
        counter_ = counter;
        realNode_ = realNode;
        f_ = f;
    }

    public AStarNode(com.fifthgen.trafficsim.map.Node realNode, int counter) {
        counter_ = counter;
        realNode_ = realNode;
        f_ = 0;
    }

    public void reset(int counter) {
        counter_ = counter;
        f_ = 0;
        inOpenList_ = false;
        inClosedList_ = false;
    }

    public int getCounter() {
        return counter_;
    }

    public boolean isInClosedList() {
        return inClosedList_;
    }

    public void setInClosedList(boolean state) {
        inClosedList_ = state;
    }

    public boolean isInOpenList() {
        return inOpenList_;
    }

    public void setInOpenList(boolean state) {
        inOpenList_ = state;
    }

    public double getF() {
        return f_;
    }

    public void setF(double f) {
        f_ = f;
    }

    public double getG() {
        return g_;
    }

    public void setG(double g) {
        g_ = g;
    }

    public AStarNode getPredecessor() {
        return predecessor_;
    }

    public void setPredecessor(AStarNode predecessor) {
        predecessor_ = predecessor;
    }

    public com.fifthgen.trafficsim.map.Node getRealNode() {
        return realNode_;
    }

    public int compareTo(Object other) {
        if (this == other) return 0;
        AStarNode otherAStarNode = (AStarNode) other;
        if (f_ > otherAStarNode.getF()) return 1;
        else if (f_ < otherAStarNode.getF()) return -1;
        else {
            if (this.hashCode() < other.hashCode()) return -1;
            else if (this.hashCode() > other.hashCode()) return 1;
            else {
                if (realNode_.getX() > otherAStarNode.getRealNode().getX()) return -1;
                else if (realNode_.getX() < otherAStarNode.getRealNode().getX()) return 1;
                else {
                    if (realNode_.getY() > otherAStarNode.getRealNode().getY()) return -1;
                    else if (realNode_.getY() < otherAStarNode.getRealNode().getY()) return 1;
                    else {
                        return 0;
                    }
                }
            }
        }
    }
}