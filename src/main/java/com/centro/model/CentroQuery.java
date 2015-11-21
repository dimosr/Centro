package com.centro.model;


public class CentroQuery {
    private int id;
    private String startingPoints;
    private String modes;
    private String meetingType;
    
    public int getId() {
        return id;
    }
    
    public String getStartingPoints() {
        return startingPoints;
    }
    
    public String modes() {
        return modes;
    }
    
    public String getMeetingType() {
        return meetingType;
    }
    
    public CentroQuery(int id, String startingPoints, String modes, String meetingType) {
        this(startingPoints, modes, meetingType);
        this.id = id;
    }
    
    public CentroQuery(String startingPoints, String modes, String meetingType) {
        this.startingPoints = startingPoints;
        this.modes = modes;
        this.meetingType = meetingType;
    }
}
