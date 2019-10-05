package com.fifthgen.trafficsim.routing;

import com.fifthgen.trafficsim.map.Node;
import com.fifthgen.trafficsim.map.Street;

import java.util.ArrayDeque;

public interface RoutingAlgorithm {

    ArrayDeque<Node> getRouting(int mode, int direction, int startX, int startY, Street startStreet, double startStreetPos, int targetX, int targetY, Street targetStreet, double targetStreetPos, Street[] penaltyStreets, int[] penaltyDirections, int[] penalties, int penaltySize, int additionalVar);
}