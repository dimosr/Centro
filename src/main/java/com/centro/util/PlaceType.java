package com.centro.util;


public enum PlaceType {
    FOOD("food", "Food"),
    CAFE("cafe", "Cafe"),
    RESTAURANT("restaurant", "Restaurant"),
    GYM("gym", "Gym"),
    SUBWAY_STATION("subway_station", "Subway Station"),
    TRAIN_STATION("train_station", "Train Station"),
    SCHOOL("school", "School"),
    NIGHT_CLUB("night_club", "Night club"),
    HEALTH("health", "Hospital"),
    UNIVERSITY("university", "University");
    
    private String googleApiName;
    private String frontEndName;
    PlaceType(String googleApiName, String frontEndName) {
        this.googleApiName = googleApiName;
        this.frontEndName = frontEndName;
    }
    
    public String getFrontEndName() {
        return frontEndName;
    }
    
    public String getGoogleApiName() {
        return googleApiName;
    }
    
}
