package com.fifthgen.trafficsim.routing.astar;

public class LookupTable<K, V> {

    private AStarNode[] table_;

    private int size_;

    public LookupTable(int initialCapacity) {
        if (initialCapacity < 0) initialCapacity = 1000;

        table_ = new AStarNode[initialCapacity];
        size_ = initialCapacity;
    }

    public AStarNode get(com.fifthgen.trafficsim.map.Node key) {
        int pos = key.getNodeID();
        if (pos > size_) return null;
        else return table_[key.getNodeID()];
    }

    public void put(com.fifthgen.trafficsim.map.Node key, AStarNode value) {
        int pos = key.getNodeID();
        if (pos > size_) {
            AStarNode[] newTable = new AStarNode[pos + 1];
            System.arraycopy(table_, 0, newTable, 0, size_);
            table_ = newTable;
            size_ = pos + 1;
        }
        table_[pos] = value;
    }
}
