package com.fifthgen.trafficsim.routing.astar;

import java.util.Arrays;

public final class Queue {

    private AStarNode[] queue_;

    private int size_ = 0;

    public Queue() {
        queue_ = new AStarNode[100];
    }

    public void add(AStarNode AStarNode) {
        int i = size_;
        if (i >= queue_.length) {
            if (i + 1 < 0) throw new OutOfMemoryError();
            int oldCapacity = queue_.length;

            int newCapacity = ((oldCapacity < 64) ? ((oldCapacity + 1) * 2) : ((oldCapacity / 2) * 3));
            if (newCapacity < 0) newCapacity = Integer.MAX_VALUE;
            if (newCapacity < i + 1) newCapacity = i + 1;
            queue_ = Arrays.copyOf(queue_, newCapacity);
        }
        size_ = i + 1;
        if (i == 0) queue_[0] = AStarNode;
        else {

            AStarNode e;
            int parent;
            while (i > 0) {
                parent = (i - 1) >>> 1;
                e = queue_[parent];
                if (AStarNode.getF() >= e.getF()) break;
                queue_[i] = e;
                i = parent;
            }
            queue_[i] = AStarNode;
        }
    }

    public void signalDecreasedF(AStarNode AStarNode) {
        for (int i = 0; i < size_; ++i) {
            if (AStarNode == queue_[i]) {

                AStarNode e;
                int parent;
                while (i > 0) {
                    parent = (i - 1) >>> 1;
                    e = queue_[parent];
                    if (AStarNode.getF() >= e.getF()) break;
                    queue_[i] = e;
                    i = parent;
                }
                queue_[i] = AStarNode;
                break;
            }
        }
    }

    public boolean isEmpty() {
        return (size_ == 0);
    }

    public AStarNode poll() {
        if (size_ == 0) return null;
        int s = --size_;
        AStarNode result = queue_[0];
        AStarNode AStarNode = queue_[s];
        queue_[s] = null;
        if (s != 0) {

            int pos = 0;
            int half = size_ >>> 1;
            AStarNode c;
            int child;
            while (pos < half) {
                child = (pos << 1) + 1;
                c = queue_[child];
                int right = child + 1;
                if (right < size_ && c.getF() > queue_[right].getF()) c = queue_[child = right];
                if (AStarNode.getF() <= c.getF()) break;
                queue_[pos] = c;
                pos = child;
            }
            queue_[pos] = AStarNode;
        }
        return result;
    }

    public void remove(AStarNode AStarNode) {
        for (int i = 0; i < size_; ++i) {
            if (AStarNode == queue_[i]) {
                int s = --size_;
                if (s == i) queue_[i] = null;
                else {
                    AStarNode moved = queue_[s];
                    queue_[s] = null;
                    siftDown(i, moved);
                    if (queue_[i] == moved) siftUp(i, moved);
                }
            }
        }
    }

    private void siftUp(int pos, AStarNode AStarNode) {
        AStarNode e;
        int parent;
        while (pos > 0) {
            parent = (pos - 1) >>> 1;
            e = queue_[parent];
            if (AStarNode.getF() >= e.getF()) break;
            queue_[pos] = e;
            pos = parent;
        }
        queue_[pos] = AStarNode;
    }

    private void siftDown(int pos, AStarNode AStarNode) {
        int half = size_ >>> 1;
        AStarNode c;
        int child;
        while (pos < half) {
            child = (pos << 1) + 1;
            c = queue_[child];
            int right = child + 1;
            if (right < size_ && c.getF() > queue_[right].getF()) c = queue_[child = right];
            if (AStarNode.getF() <= c.getF()) break;
            queue_[pos] = c;
            pos = child;
        }
        queue_[pos] = AStarNode;
    }
}
