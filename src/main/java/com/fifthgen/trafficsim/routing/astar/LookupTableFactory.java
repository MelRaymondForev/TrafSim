package com.fifthgen.trafficsim.routing.astar;

import java.util.ArrayDeque;

public final class LookupTableFactory {

    private static final ArrayDeque<Integer> COUNTER = new ArrayDeque<>();
    private static final ArrayDeque<LookupTable<com.fifthgen.trafficsim.map.Node, AStarNode>> TABLES = new ArrayDeque<>();

    public static synchronized LookupTable<com.fifthgen.trafficsim.map.Node, AStarNode> getTable(int[] counter) {
        if (TABLES.isEmpty()) {
            counter[0] = Integer.MIN_VALUE;
            return new LookupTable<>(com.fifthgen.trafficsim.map.Node.getMaxNodeID() + 1);
        } else {
            counter[0] = COUNTER.poll() + 1;
            if (counter[0] == Integer.MAX_VALUE) {
                TABLES.poll();
                counter[0] = Integer.MIN_VALUE;
                return new LookupTable<>(com.fifthgen.trafficsim.map.Node.getMaxNodeID() + 1);
            } else return TABLES.poll();
        }
    }

    public static synchronized void putTable(int counter, LookupTable<com.fifthgen.trafficsim.map.Node, AStarNode> table) {
        TABLES.add(table);
        COUNTER.add(counter);
    }

    public static synchronized void clear() {
        TABLES.clear();
        COUNTER.clear();
    }
}