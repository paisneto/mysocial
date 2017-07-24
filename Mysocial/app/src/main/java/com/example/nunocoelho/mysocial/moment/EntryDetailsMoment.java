package com.example.nunocoelho.mysocial.moment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ecasanova on 02/06/2017.
 */

public class EntryDetailsMoment {

    /**Moments objects
     "title": "Portugal",
     "place": "Ipca",
     "moment_date": "2017-11-30T00:00:00.000Z",
     "narrative": "Go to Barcelos",
     "lat": "41,5317",
     "lon": "-8,6179",
     "img": "buffer",
     "created": "2017-11-30T00:00:00
     * */

    @SerializedName("_id")
    private String id;
    private String title;
    private String place;
    private String moment_date;
    private String narrative;
    private String lat;
    private String lon;
    private String[] classification;
    private String trip;
    private String[] comments;
    private String originalname;
    private String path;

    private List<EntryDetailsMoment> entradas;
    public EntryDetailsMoment() {
        id = "";
        title = "";
        place="";
        moment_date="";
        narrative="";
        lat="";
        lon="";
        trip="";
        classification = null;
        comments = null;
        originalname="";
        path="";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getMomentDate() {
        return moment_date;
    }

    public void setMomentDate(String moment_date) {
        this.moment_date = moment_date;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative= narrative;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) { this.lat = lat;}

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) { this.lon = lon;}

    public String getTrip() {
        return trip;
    }

    public void setTrip(String trip) { this.trip = trip;}

    public String[] getClassification ()
    {
        return classification;
    }

    public void setClassification (String[] classification)
    {
        this.classification = classification;
    }

    public String getPath ()
    {
        return path;
    }

    public void setPath (String path)
    {
        this.path = path;
    }

    public String[] getComments ()
    {
        return comments;
    }

    public void setComments (String[] comments)
    {
        this.comments = comments;
    }

    public String getOriginalname ()
    {
        return originalname;
    }

    public void setOriginalname (String originalname)
    {
        this.originalname = originalname;
    }

    public List<EntryDetailsMoment> getEntradas() {
        return entradas;
    }

    @Override
    public String toString() {
        return "\n" +
                "id: " + id + "\n" +
                "title: " + title + "\n" +
         /*       "country: " + country  +"\n"+
                "city: " + city + "\n" +
                "description: " + description + "\n" +
                "date: " + date + "\n" +*/
                "--------------------------";
    }

}
