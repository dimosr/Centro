package com.centro.util;

import java.util.List;


public class PlaceInfo {
    public List<String> imageLinks;
    public String averageRating;
    public String websiteLink;
    
    public PlaceInfo(List<String> imageLinks, String averageRating, String websiteLink) {
        this.imageLinks = imageLinks;
        this.averageRating = averageRating;
        this.websiteLink = websiteLink;
    }
}
