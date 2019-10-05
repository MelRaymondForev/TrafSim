package com.fifthgen.trafficsim.routing.astar;

import com.fifthgen.trafficsim.map.Street;
import com.fifthgen.trafficsim.routing.RoutingAlgorithm;

import java.util.ArrayDeque;

public final class Algorithm implements RoutingAlgorithm {

    public Algorithm() {
    }

    private AStarNode computeRoute(int mode, int direction, Street startStreet, double startStreetPos, int targetX, int targetY, Street targetStreet, double targetStreetPos, Street[] penaltyStreets, int[] penaltyDirections, int[] penalties, int penaltySize, int additionalVar) {
        int distanceAdd;
        long dx, dy;
        double f, g, distance;
        boolean target1found = false, target2found = false, endNodeMayBeDestination;
        int speed;
        com.fifthgen.trafficsim.map.Node tmpNode;
        int i, j;
        AStarNode currentAStarNode, successor, startAStarNode;
        Street[] outgoingStreets;
        Street tmpStreet;
        Queue openList = new Queue();

        int[] tmp = new int[1];
        LookupTable<com.fifthgen.trafficsim.map.Node, AStarNode> lookupTable = LookupTableFactory.getTable(tmp);
        int counter = tmp[0];

        endNodeMayBeDestination = !targetStreet.isOneway();

        if (direction > -1) {
            startAStarNode = lookupTable.get(startStreet.getStartNode());
            if (startAStarNode == null) {
                startAStarNode = new AStarNode(startStreet.getStartNode(), counter);
                lookupTable.put(startStreet.getStartNode(), startAStarNode);
            } else {
                startAStarNode.reset(counter);
                startAStarNode.setPredecessor(null);
            }
            if (mode == 0) {
                startAStarNode.setF(startStreetPos);
                startAStarNode.setG(startStreetPos);
            } else {
                if (startStreet.getSpeed() > additionalVar) speed = additionalVar;
                else speed = startStreet.getSpeed();
                startAStarNode.setF(startStreetPos / speed);
                startAStarNode.setG(startStreetPos / speed);
            }
            startAStarNode.setInOpenList(true);
            openList.add(startAStarNode);
        }
        if (direction < 1) {
            startAStarNode = lookupTable.get(startStreet.getEndNode());
            if (startAStarNode == null) {
                startAStarNode = new AStarNode(startStreet.getEndNode(), counter);
                lookupTable.put(startStreet.getEndNode(), startAStarNode);
            } else {
                startAStarNode.reset(counter);
                startAStarNode.setPredecessor(null);
            }
            if (mode == 0) {
                startAStarNode.setF(startStreet.getLength() - startStreetPos);
                startAStarNode.setG(startStreet.getLength() - startStreetPos);
            } else {
                if (startStreet.getSpeed() > additionalVar) speed = additionalVar;
                else speed = startStreet.getSpeed();
                startAStarNode.setF((startStreet.getLength() - startStreetPos) / speed);
                startAStarNode.setG((startStreet.getLength() - startStreetPos) / speed);
            }
            startAStarNode.setInOpenList(true);
            openList.add(startAStarNode);
        }
        do {

            currentAStarNode = openList.poll();

            if (endNodeMayBeDestination && currentAStarNode.getRealNode() == targetStreet.getEndNode()) {
                if (target1found) {
                    LookupTableFactory.putTable(counter, lookupTable);
                    return currentAStarNode;
                } else {
                    if (mode == 0) f = currentAStarNode.getF() + (targetStreet.getLength() - targetStreetPos);
                    else {
                        if (targetStreet.getSpeed() > additionalVar) speed = additionalVar;
                        else speed = targetStreet.getSpeed();
                        f = currentAStarNode.getF() + ((targetStreet.getLength() - targetStreetPos) / speed);
                    }
                    currentAStarNode.setF(f);
                    currentAStarNode.setG(f);
                    openList.add(currentAStarNode);
                    target1found = true;
                }
            } else if (currentAStarNode.getRealNode() == targetStreet.getStartNode()) {
                if (target2found) {
                    LookupTableFactory.putTable(counter, lookupTable);
                    return currentAStarNode;
                } else {
                    if (mode == 0) f = currentAStarNode.getF() + targetStreetPos;
                    else {
                        if (targetStreet.getSpeed() > additionalVar) speed = additionalVar;
                        else speed = targetStreet.getSpeed();
                        f = currentAStarNode.getF() + (targetStreetPos / speed);
                    }
                    currentAStarNode.setF(f);
                    currentAStarNode.setG(f);
                    openList.add(currentAStarNode);
                    target2found = true;
                }

            } else {
                outgoingStreets = currentAStarNode.getRealNode().getOutgoingStreets();
                for (i = 0; i < outgoingStreets.length; ++i) {
                    tmpStreet = outgoingStreets[i];
                    tmpNode = tmpStreet.getStartNode();
                    if (tmpNode == currentAStarNode.getRealNode())
                        tmpNode = tmpStreet.getEndNode();

                    successor = lookupTable.get(tmpNode);
                    if (successor == null) {
                        successor = new AStarNode(tmpNode, counter);
                        lookupTable.put(tmpNode, successor);
                    } else {
                        if (successor.getCounter() != counter) successor.reset(counter);
                    }

                    if (successor.isInClosedList() == false) {

                        distanceAdd = 0;
                        if (penaltySize > 0) {
                            if (tmpStreet.getStartNode() == currentAStarNode.getRealNode()) {
                                for (j = 0; j < penaltySize; ++j) {
                                    if (penaltyStreets[j] == tmpStreet && penaltyDirections[j] < 1) {
                                        if (distanceAdd < penalties[j]) distanceAdd = penalties[j];
                                    }
                                }
                            } else {
                                for (j = 0; j < penaltySize; ++j) {
                                    if (penaltyStreets[j] == tmpStreet && penaltyDirections[j] > -1) {
                                        if (distanceAdd < penalties[j]) distanceAdd = penalties[j];
                                    }
                                }
                            }

                        }

                        dx = targetX - tmpNode.getX();
                        dy = targetY - tmpNode.getY();
                        distance = distanceAdd + Math.sqrt(dx * dx + dy * dy);

                        if (mode == 0) {
                            g = currentAStarNode.getG() + tmpStreet.getLength();
                            f = g + distance;
                        } else {
                            if (tmpStreet.getSpeed() > additionalVar)
                                g = currentAStarNode.getG() + (tmpStreet.getLength() / additionalVar);
                            else g = currentAStarNode.getG() + (tmpStreet.getLength() / tmpStreet.getSpeed());
                            f = g + (distance / additionalVar);
                        }
                        if (!successor.isInOpenList()) {
                            successor.setPredecessor(currentAStarNode);
                            successor.setF(f);
                            successor.setG(g);
                            successor.setInOpenList(true);
                            openList.add(successor);
                        } else if (successor.getF() > f) {
                            if (target1found && successor.getRealNode() == targetStreet.getEndNode()) {
                                if (mode == 0) f = g + (targetStreet.getLength() - targetStreetPos);
                                else {
                                    if (targetStreet.getSpeed() > additionalVar) speed = additionalVar;
                                    else speed = targetStreet.getSpeed();
                                    f = g + ((targetStreet.getLength() - targetStreetPos) / speed);
                                }
                                if (successor.getF() > f) {
                                    successor.setPredecessor(currentAStarNode);
                                    successor.setF(f);
                                    successor.setG(g);
                                    openList.signalDecreasedF(successor);
                                }
                            } else if (target2found && successor.getRealNode() == targetStreet.getStartNode()) {
                                if (mode == 0) f = g + targetStreetPos;
                                else {
                                    if (targetStreet.getSpeed() > additionalVar) speed = additionalVar;
                                    else speed = targetStreet.getSpeed();
                                    f = g + (targetStreetPos / speed);
                                }
                                if (successor.getF() > f) {
                                    successor.setPredecessor(currentAStarNode);
                                    successor.setF(f);
                                    successor.setG(g);
                                    openList.signalDecreasedF(successor);
                                }
                            } else {
                                successor.setPredecessor(currentAStarNode);
                                successor.setF(f);
                                successor.setG(g);
                                openList.signalDecreasedF(successor);
                            }
                        }
                    }
                }

                currentAStarNode.setInClosedList(true);
                currentAStarNode.setInOpenList(false);
            }
        } while (!openList.isEmpty());

        LookupTableFactory.putTable(counter, lookupTable);
        return null;
    }

    public ArrayDeque<com.fifthgen.trafficsim.map.Node> getRouting(int mode, int direction, int startX, int startY, Street startStreet, double startStreetPos, int targetX, int targetY, Street targetStreet, double targetStreetPos, Street[] penaltyStreets, int[] penaltyDirections, int[] penalties, int penaltySize, int additionalVar) {
        AStarNode curAStarNode = computeRoute(mode, direction, startStreet, startStreetPos, targetX, targetY, targetStreet, targetStreetPos, penaltyStreets, penaltyDirections, penalties, penaltySize, additionalVar);
        ArrayDeque<com.fifthgen.trafficsim.map.Node> result = new ArrayDeque<com.fifthgen.trafficsim.map.Node>(255);
        while (curAStarNode != null) {
            result.addFirst(curAStarNode.getRealNode());
            curAStarNode = curAStarNode.getPredecessor();
        }
        return result;
    }
}