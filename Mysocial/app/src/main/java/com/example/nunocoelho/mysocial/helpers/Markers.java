package com.example.nunocoelho.mysocial.helpers;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ecasanova on 27/07/2017.
 */

public class Markers implements Serializable {
    double lat = 0;
    double lon = 0;
    String place = "";

    public Markers(){

    }

    public Markers(double _lat,double _lon,String _place) {
        this.lat=_lat;
        this.lon=_lon;
        this.place=_place;
    }

    ArrayList<Markers> markersList;

    public ArrayList<Markers> getMarkersList() {
        return markersList;
    }

    public void setMarkersList(ArrayList<Markers> markersList) {
        this.markersList = markersList;
    }

    public double getLAT() {
        return lat;
    }

    public void setLAT(double lat) {
        lat = lat;
    }

    public double getLON() {
        return lon;
    }

    public void setLON(double lon) {
        lon = lon;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        place = place;
    }
}