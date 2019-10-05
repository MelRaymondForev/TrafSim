package com.fifthgen.trafficsim.scenario.events;

import java.awt.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Cluster {
    private String clusterID_;

    private String eventType_;
    private int minX_ = Integer.MAX_VALUE;
    private int minY_ = Integer.MAX_VALUE;
    private int maxX_ = 0;
    private int maxY_ = 0;
    private int size_ = 0;
    private Color clusterColor = Color.black;

    private ArrayList<Integer> xCoords_ = new ArrayList<Integer>();

    private ArrayList<Integer> yCoords_ = new ArrayList<Integer>();

    public boolean fillCluster(String filePath, String clusterID) {
        String searchKey = "@data";

        clusterID_ = clusterID;

        try {

            FileInputStream fstream = new FileInputStream(filePath);

            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            String[] splitLine;

            boolean found = false;

            while ((strLine = br.readLine()) != null) {
                if (strLine.equals(searchKey)) found = true;

                if (found) {

                    splitLine = strLine.split(",");

                    if (splitLine[splitLine.length - 1].equals("cluster" + clusterID_)) {
                        size_++;
                        eventType_ = splitLine[1];

                        if (eventType_.equals("HUANG_PCN")) clusterColor = Color.green;
                        if (eventType_.equals("HUANG_EEBL")) clusterColor = Color.blue;
                        if (eventType_.equals("PCN_FORWARD")) clusterColor = Color.pink;
                        if (eventType_.equals("HUANG_RHCN")) clusterColor = Color.gray;
                        if (eventType_.equals("EVA_FORWARD")) clusterColor = Color.cyan;
                        if (eventType_.equals("HUANG_EVA")) clusterColor = Color.magenta;

                        xCoords_.add(Integer.parseInt(splitLine[2]));
                        yCoords_.add(Integer.parseInt(splitLine[3]));
                        if (Integer.parseInt(splitLine[2]) > maxX_) maxX_ = Integer.parseInt(splitLine[2]);
                        if (Integer.parseInt(splitLine[2]) < minX_) minX_ = Integer.parseInt(splitLine[2]);

                        if (Integer.parseInt(splitLine[3]) > maxY_) maxY_ = Integer.parseInt(splitLine[3]);
                        if (Integer.parseInt(splitLine[3]) < minY_) minY_ = Integer.parseInt(splitLine[3]);
                    }

                }
            }

            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        return size_ > 0;
    }

    public String toString() {
        return "cluster - " + clusterID_;
    }

    public String getEventType_() {
        return eventType_;
    }

    public int getMinX_() {
        return minX_;
    }

    public int getMinY_() {
        return minY_;
    }

    public int getMaxX_() {
        return maxX_;
    }

    public int getMaxY_() {
        return maxY_;
    }

    public int getSize_() {
        return size_;
    }

    public Color getClusterColor() {
        return clusterColor;
    }

    public ArrayList<Integer> getxCoords_() {
        return xCoords_;
    }

    public ArrayList<Integer> getyCoords_() {
        return yCoords_;
    }

    public String getClusterID_() {
        return clusterID_;
    }

    public void setClusterID_(String clusterID_) {
        this.clusterID_ = clusterID_;
    }
}
