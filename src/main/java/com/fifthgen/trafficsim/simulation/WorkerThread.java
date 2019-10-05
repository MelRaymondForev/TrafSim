package com.fifthgen.trafficsim.simulation;

import com.fifthgen.trafficsim.gui.Renderer;
import com.fifthgen.trafficsim.localization.Messages;
import com.fifthgen.trafficsim.map.Node;
import com.fifthgen.trafficsim.map.Region;
import com.fifthgen.trafficsim.scenario.RSU;
import com.fifthgen.trafficsim.scenario.Vehicle;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public final class WorkerThread extends Thread {

    private static int simulationMode_ = 1;

    private final Region[] ourRegions_;

    private final int timePerStep_;

    private final LinkedHashSet<Integer> changedRegions_ = new LinkedHashSet<Integer>(16);

    private CyclicBarrier barrierStart_;

    private CyclicBarrier barrierDuringWork_;

    private CyclicBarrier barrierFinish_;

    public WorkerThread(Region[] ourRegions, int timePerStep) {
        setName("Worker startX:" + ourRegions[0].getX() + " startY:" + +ourRegions[0].getY());
        ourRegions_ = ourRegions;
        timePerStep_ = timePerStep;
    }

    public static int getSimulationMode_() {
        return simulationMode_;
    }

    public static void setSimulationMode_(int simulationMode) {
        simulationMode_ = simulationMode;
    }

    public void setBarriers(CyclicBarrier barrierStart, CyclicBarrier barrierDuringWork, CyclicBarrier barrierFinish) {
        barrierStart_ = barrierStart;
        barrierDuringWork_ = barrierDuringWork;
        barrierFinish_ = barrierFinish;
    }

    public void addChangedRegion(int i) {
        synchronized (changedRegions_) {
            changedRegions_.add(Integer.valueOf(i));
        }
    }

    public void run() {
        int i, j, length;
        int ourRegionsLength = ourRegions_.length;

        Vehicle[][] vehicles = new Vehicle[ourRegionsLength][];
        Vehicle[] vehicleSubarray;
        Vehicle vehicle;

        int tmpTimePassed = 999999999;
        int tmpTimePassedSaved = 99999999;
        int silentPeriodDuration = Vehicle.getTIME_OF_SILENT_PERIODS();
        int silentPeriodFrequency = Vehicle.getTIME_BETWEEN_SILENT_PERIODS();

        RSU[][] rsus = new RSU[ourRegionsLength][];
        RSU[] rsuSubarray;
        RSU rsu;

        Iterator<Integer> changedRegionIterator;
        int tmp;

        for (i = 0; i < ourRegionsLength; ++i) {
            ourRegions_[i].createBacklink(this, i);
            ourRegions_[i].calculateJunctions();
            vehicles[i] = ourRegions_[i].getVehicleArray();

            rsus[i] = ourRegions_[i].getRSUs();
        }

        boolean communicationEnabled = Vehicle.getCommunicationEnabled();
        boolean beaconsEnabled = Vehicle.getBeaconsEnabled();
        boolean recyclingEnabled = Vehicle.getRecyclingEnabled();
        boolean idsEnabled = Vehicle.isIdsActivated();

        while (barrierStart_ == null || barrierDuringWork_ == null || barrierFinish_ == null) {
            try {
                sleep(50);
            } catch (Exception e) {
            }
        }

        if (simulationMode_ == 1) {
            while (true) {

                if (changedRegions_.size() > 0) {
                    changedRegionIterator = changedRegions_.iterator();
                    while (changedRegionIterator.hasNext()) {
                        tmp = changedRegionIterator.next().intValue();
                        vehicles[tmp] = ourRegions_[tmp].getVehicleArray();
                    }
                    changedRegions_.clear();
                }

                try {
                    barrierStart_.await();
                } catch (InterruptedException e) {
                    break;
                } catch (BrokenBarrierException e) {
                    break;
                } catch (Exception e) {

                }

                try {

                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            vehicleSubarray[j].adjustSpeed(timePerStep_);
                        }
                    }

                    for (i = 0; i < ourRegionsLength; ++i) {
                        rsuSubarray = rsus[i];
                        length = rsuSubarray.length;
                        for (j = 0; j < length; ++j) {
                            rsuSubarray[j].cleanup(timePerStep_);
                        }
                    }

                    barrierDuringWork_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {

                    try {
                        barrierDuringWork_.await();
                    } catch (Exception e2) {
                    }
                }

                if (communicationEnabled) {
                    try {

                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getCommunicationCountdown() < 1) {
                                    vehicle.sendMessages();
                                }
                            }
                        }

                        for (i = 0; i < ourRegionsLength; ++i) {
                            rsuSubarray = rsus[i];
                            length = rsuSubarray.length;
                            for (j = 0; j < length; ++j) {
                                rsu = rsuSubarray[j];
                                if (rsu.getCommunicationCountdown() < 1 && !rsu.isEncrypted_()) {
                                    rsuSubarray[j].sendMessages();
                                }
                            }
                        }

                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {

                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }

                if (idsEnabled) {
                    try {
                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
                            }
                        }

                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {

                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }

                if (communicationEnabled && beaconsEnabled) {
                    try {

                        if (Vehicle.isSilentPeriodsOn()) {
                            tmpTimePassed = Renderer.getInstance().getTimePassed();
                            if (tmpTimePassed > silentPeriodFrequency && tmpTimePassed % (silentPeriodDuration + silentPeriodFrequency) < 240) {
                                tmpTimePassedSaved = tmpTimePassed;
                                Vehicle.setSilent_period(true);
                            } else if (Vehicle.isSilent_period() && tmpTimePassed > (tmpTimePassedSaved + silentPeriodDuration))
                                Vehicle.setSilent_period(false);
                        }

                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && !vehicle.isInMixZone()) {
                                    vehicle.sendBeacons();
                                }
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && vehicle.isInMixZone() && vehicle.getCurMixNode_() != null && vehicle.getCurMixNode_().getEncryptedRSU_() != null) {
                                    vehicle.sendEncryptedBeacons();
                                }
                            }
                        }

                        for (i = 0; i < ourRegionsLength; ++i) {
                            rsuSubarray = rsus[i];
                            length = rsuSubarray.length;
                            for (j = 0; j < length; ++j) {
                                rsu = rsuSubarray[j];
                                if (rsu.getBeaconCountdown() < 1 && !rsu.isEncrypted_()) rsu.sendBeacons();
                                if (rsu.getBeaconCountdown() < 1 && rsu.isEncrypted_()) rsu.sendEncryptedBeacons();
                            }
                        }

                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {

                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }

                try {
                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            if (vehicleSubarray[j].isActive()) vehicleSubarray[j].move(timePerStep_);
                            else if (recyclingEnabled && vehicleSubarray[j].getMayBeRecycled() && !vehicleSubarray[j].isDoNotRecycle_())
                                vehicleSubarray[j].reset();

                        }
                    }

                    barrierDuringWork_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {

                    try {
                        barrierDuringWork_.await();
                    } catch (Exception e2) {
                    }
                }

                try {
                    Node[] tmpNodes = null;
                    for (i = 0; i < ourRegions_.length; i++) {
                        tmpNodes = ourRegions_[i].getNodes();
                        for (j = 0; j < tmpNodes.length; j++) {
                            if (tmpNodes[j].isHasTrafficSignal_() && tmpNodes[j].getJunction() != null && tmpNodes[j].getJunction().getNode().getTrafficLight_() != null) {
                                tmpNodes[j].getJunction().getNode().getTrafficLight_().changePhases(timePerStep_);
                            }
                        }
                    }

                    barrierFinish_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {

                    try {
                        barrierFinish_.await();
                    } catch (Exception e2) {
                    }
                }

            }
        } else if (simulationMode_ == 2) {
            while (true) {

                if (changedRegions_.size() > 0) {
                    changedRegionIterator = changedRegions_.iterator();
                    while (changedRegionIterator.hasNext()) {
                        tmp = changedRegionIterator.next().intValue();
                        vehicles[tmp] = ourRegions_[tmp].getVehicleArray();
                    }
                    changedRegions_.clear();
                }

                try {
                    barrierStart_.await();
                } catch (InterruptedException e) {
                    break;
                } catch (BrokenBarrierException e) {
                    break;
                } catch (Exception e) {

                }

                for (i = 0; i < ourRegionsLength; ++i) {
                    vehicleSubarray = vehicles[i];
                    length = vehicleSubarray.length;
                    for (j = 0; j < length; ++j) {
                        vehicleSubarray[j].adjustSpeedWithIDM(timePerStep_);
                    }
                }

                for (i = 0; i < ourRegionsLength; ++i) {
                    rsuSubarray = rsus[i];
                    length = rsuSubarray.length;
                    for (j = 0; j < length; ++j) {
                        rsuSubarray[j].cleanup(timePerStep_);
                    }
                }

                try {
                    barrierDuringWork_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {

                    try {
                        barrierDuringWork_.await();
                    } catch (Exception e2) {
                    }
                }

                if (communicationEnabled) {

                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            vehicle = vehicleSubarray[j];
                            if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getCommunicationCountdown() < 1) {
                                vehicle.sendMessages();
                            }
                        }
                    }

                    for (i = 0; i < ourRegionsLength; ++i) {
                        rsuSubarray = rsus[i];
                        length = rsuSubarray.length;
                        for (j = 0; j < length; ++j) {
                            rsu = rsuSubarray[j];
                            if (rsu.getCommunicationCountdown() < 1 && !rsu.isEncrypted_()) {
                                rsuSubarray[j].sendMessages();
                            }
                        }
                    }

                    try {
                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {

                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }

                if (idsEnabled) {

                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            vehicle = vehicleSubarray[j];
                        }
                    }

                    try {
                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {

                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }

                if (communicationEnabled && beaconsEnabled) {

                    if (Vehicle.isSilentPeriodsOn()) {
                        tmpTimePassed = Renderer.getInstance().getTimePassed();
                        if (tmpTimePassed > silentPeriodFrequency && tmpTimePassed % (silentPeriodDuration + silentPeriodFrequency) < 240) {
                            tmpTimePassedSaved = tmpTimePassed;
                            Vehicle.setSilent_period(true);
                        } else if (Vehicle.isSilent_period() && tmpTimePassed > (tmpTimePassedSaved + silentPeriodDuration))
                            Vehicle.setSilent_period(false);
                    }

                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            vehicle = vehicleSubarray[j];
                            if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && !vehicle.isInMixZone()) {
                                vehicle.sendBeacons();
                            }
                            if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && vehicle.isInMixZone() && vehicle.getCurMixNode_() != null && vehicle.getCurMixNode_().getEncryptedRSU_() != null) {
                                vehicle.sendEncryptedBeacons();
                            }
                        }
                    }

                    for (i = 0; i < ourRegionsLength; ++i) {
                        rsuSubarray = rsus[i];
                        length = rsuSubarray.length;
                        for (j = 0; j < length; ++j) {
                            rsu = rsuSubarray[j];
                            if (rsu.getBeaconCountdown() < 1 && !rsu.isEncrypted_()) rsu.sendBeacons();
                            if (rsu.getBeaconCountdown() < 1 && rsu.isEncrypted_()) rsu.sendEncryptedBeacons();
                        }
                    }

                    try {
                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {

                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }

                for (i = 0; i < ourRegionsLength; ++i) {
                    vehicleSubarray = vehicles[i];
                    length = vehicleSubarray.length;
                    for (j = 0; j < length; ++j) {
                        if (vehicleSubarray[j].isActive()) vehicleSubarray[j].move(timePerStep_);
                        else if (recyclingEnabled && vehicleSubarray[j].getMayBeRecycled() && !vehicleSubarray[j].isDoNotRecycle_())
                            vehicleSubarray[j].reset();

                    }
                }

                try {
                    barrierFinish_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {

                    try {
                        barrierFinish_.await();
                    } catch (Exception e2) {
                    }
                }

            }
        }

        if (simulationMode_ == 3) {
            while (true) {

                if (changedRegions_.size() > 0) {
                    changedRegionIterator = changedRegions_.iterator();
                    while (changedRegionIterator.hasNext()) {
                        tmp = changedRegionIterator.next().intValue();
                        vehicles[tmp] = ourRegions_[tmp].getVehicleArray();
                    }
                    changedRegions_.clear();
                }

                try {
                    barrierStart_.await();
                } catch (InterruptedException e) {
                    break;
                } catch (BrokenBarrierException e) {
                    break;
                } catch (Exception e) {

                }

                try {

                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            vehicleSubarray[j].adjustSpeedWithSJTUTraceFiles(timePerStep_);
                        }
                    }

                    for (i = 0; i < ourRegionsLength; ++i) {
                        rsuSubarray = rsus[i];
                        length = rsuSubarray.length;
                        for (j = 0; j < length; ++j) {
                            rsuSubarray[j].cleanup(timePerStep_);
                        }
                    }

                    barrierDuringWork_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {

                    try {
                        barrierDuringWork_.await();
                    } catch (Exception e2) {
                    }
                }

                if (communicationEnabled) {
                    try {

                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getCommunicationCountdown() < 1) {
                                    vehicle.sendMessages();
                                }
                            }
                        }

                        for (i = 0; i < ourRegionsLength; ++i) {
                            rsuSubarray = rsus[i];
                            length = rsuSubarray.length;
                            for (j = 0; j < length; ++j) {
                                rsu = rsuSubarray[j];
                                if (rsu.getCommunicationCountdown() < 1 && !rsu.isEncrypted_()) {
                                    rsuSubarray[j].sendMessages();
                                }
                            }
                        }

                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {

                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }

                if (idsEnabled) {
                    try {
                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
                            }
                        }

                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {

                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }

                if (communicationEnabled && beaconsEnabled) {
                    try {

                        if (Vehicle.isSilentPeriodsOn()) {
                            tmpTimePassed = Renderer.getInstance().getTimePassed();
                            if (tmpTimePassed > silentPeriodFrequency && tmpTimePassed % (silentPeriodDuration + silentPeriodFrequency) < 240) {
                                tmpTimePassedSaved = tmpTimePassed;
                                Vehicle.setSilent_period(true);
                            } else if (Vehicle.isSilent_period() && tmpTimePassed > (tmpTimePassedSaved + silentPeriodDuration))
                                Vehicle.setSilent_period(false);
                        }

                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && !vehicle.isInMixZone()) {
                                    vehicle.sendBeacons();
                                }
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && vehicle.isInMixZone() && vehicle.getCurMixNode_() != null && vehicle.getCurMixNode_().getEncryptedRSU_() != null) {
                                    vehicle.sendEncryptedBeacons();
                                }
                            }
                        }

                        for (i = 0; i < ourRegionsLength; ++i) {
                            rsuSubarray = rsus[i];
                            length = rsuSubarray.length;
                            for (j = 0; j < length; ++j) {
                                rsu = rsuSubarray[j];
                                if (rsu.getBeaconCountdown() < 1 && !rsu.isEncrypted_()) rsu.sendBeacons();
                                if (rsu.getBeaconCountdown() < 1 && rsu.isEncrypted_()) rsu.sendEncryptedBeacons();
                            }
                        }

                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {

                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }

                try {
                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            if (vehicleSubarray[j].isActive()) vehicleSubarray[j].move(timePerStep_);
                            else if (recyclingEnabled && vehicleSubarray[j].getMayBeRecycled() && !vehicleSubarray[j].isDoNotRecycle_())
                                vehicleSubarray[j].reset();

                        }
                    }

                    barrierFinish_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {

                    try {
                        barrierFinish_.await();
                    } catch (Exception e2) {
                    }
                }

            }
        }

        if (simulationMode_ == 4) {
            while (true) {

                if (changedRegions_.size() > 0) {
                    changedRegionIterator = changedRegions_.iterator();
                    while (changedRegionIterator.hasNext()) {
                        tmp = changedRegionIterator.next().intValue();
                        vehicles[tmp] = ourRegions_[tmp].getVehicleArray();
                    }
                    changedRegions_.clear();
                }

                try {
                    barrierStart_.await();
                } catch (InterruptedException e) {
                    break;
                } catch (BrokenBarrierException e) {
                    break;
                } catch (Exception e) {

                }

                try {

                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            vehicleSubarray[j].adjustSpeedWithSanFranciscoTraceFiles(timePerStep_);
                        }
                    }

                    for (i = 0; i < ourRegionsLength; ++i) {
                        rsuSubarray = rsus[i];
                        length = rsuSubarray.length;
                        for (j = 0; j < length; ++j) {
                            rsuSubarray[j].cleanup(timePerStep_);
                        }
                    }

                    barrierDuringWork_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {

                    try {
                        barrierDuringWork_.await();
                    } catch (Exception e2) {
                    }
                }

                if (communicationEnabled) {
                    try {

                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getCommunicationCountdown() < 1) {
                                    vehicle.sendMessages();
                                }
                            }
                        }

                        for (i = 0; i < ourRegionsLength; ++i) {
                            rsuSubarray = rsus[i];
                            length = rsuSubarray.length;
                            for (j = 0; j < length; ++j) {
                                rsu = rsuSubarray[j];
                                if (rsu.getCommunicationCountdown() < 1 && !rsu.isEncrypted_()) {
                                    rsuSubarray[j].sendMessages();
                                }
                            }
                        }

                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {

                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }

                if (idsEnabled) {
                    try {
                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
                            }
                        }

                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {

                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }

                if (communicationEnabled && beaconsEnabled) {
                    try {

                        if (Vehicle.isSilentPeriodsOn()) {
                            tmpTimePassed = Renderer.getInstance().getTimePassed();
                            if (tmpTimePassed > silentPeriodFrequency && tmpTimePassed % (silentPeriodDuration + silentPeriodFrequency) < 240) {
                                tmpTimePassedSaved = tmpTimePassed;
                                Vehicle.setSilent_period(true);
                            } else if (Vehicle.isSilent_period() && tmpTimePassed > (tmpTimePassedSaved + silentPeriodDuration))
                                Vehicle.setSilent_period(false);
                        }

                        for (i = 0; i < ourRegionsLength; ++i) {
                            vehicleSubarray = vehicles[i];
                            length = vehicleSubarray.length;
                            for (j = 0; j < length; ++j) {
                                vehicle = vehicleSubarray[j];
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && !vehicle.isInMixZone()) {
                                    vehicle.sendBeacons();
                                }
                                if (vehicle.isActive() && vehicle.isWiFiEnabled() && vehicle.getBeaconCountdown() < 1 && vehicle.isInMixZone() && vehicle.getCurMixNode_() != null && vehicle.getCurMixNode_().getEncryptedRSU_() != null) {
                                    vehicle.sendEncryptedBeacons();
                                }
                            }
                        }

                        for (i = 0; i < ourRegionsLength; ++i) {
                            rsuSubarray = rsus[i];
                            length = rsuSubarray.length;
                            for (j = 0; j < length; ++j) {
                                rsu = rsuSubarray[j];
                                if (rsu.getBeaconCountdown() < 1 && !rsu.isEncrypted_()) rsu.sendBeacons();
                                if (rsu.getBeaconCountdown() < 1 && rsu.isEncrypted_()) rsu.sendEncryptedBeacons();
                            }
                        }

                        barrierDuringWork_.await();
                    } catch (BrokenBarrierException e) {
                    } catch (Exception e) {

                        try {
                            barrierDuringWork_.await();
                        } catch (Exception e2) {
                        }
                    }
                }

                try {
                    for (i = 0; i < ourRegionsLength; ++i) {
                        vehicleSubarray = vehicles[i];
                        length = vehicleSubarray.length;
                        for (j = 0; j < length; ++j) {
                            if (vehicleSubarray[j].isActive()) vehicleSubarray[j].move(timePerStep_);
                            else if (recyclingEnabled && vehicleSubarray[j].getMayBeRecycled() && !vehicleSubarray[j].isDoNotRecycle_())
                                vehicleSubarray[j].reset();

                        }
                    }

                    barrierFinish_.await();
                } catch (BrokenBarrierException e) {
                } catch (Exception e) {

                    try {
                        barrierFinish_.await();
                    } catch (Exception e2) {
                    }
                }

            }
        }

        for (i = 0; i < ourRegionsLength; ++i) {
            ourRegions_[i].createBacklink(null, -1);
        }
    }
}