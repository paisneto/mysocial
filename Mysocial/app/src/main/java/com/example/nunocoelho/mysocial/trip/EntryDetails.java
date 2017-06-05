package com.example.nunocoelho.mysocial.trip;
import android.graphics.Matrix;
import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

import java.nio.Buffer;
import java.util.List;

/**
 * Created by Nuno Coelho on 21/05/2017.
 */

public class EntryDetails {

    //":[{"_id":"59289755e2de3300042e5463",
    // "title":"London",
    // "country":"NNNNN",
    // "city":"nnnn",
    // "description":"nnn",
    // "date":"nn","postedBy":"5914cf6d5c8b5c29d4039d2b","

    @SerializedName("_id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("country")
    private String country;
    @SerializedName("city")
    private String city;
    @SerializedName("description")
    private String description;
    @SerializedName("date")
    private String date;

    @SerializedName("originalname")
    private String filename;

    @SerializedName("img")
    private List<EntryMediaDetails> media;

    private List<EntryDetails> entradas;

    public EntryDetails() {
        id = "";
        title = "";
        country="";
        city="";
        description="";
        date="";
        media=null;
        filename="";
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDescription(String descrption) {
        this.description = description;
    }

    public String getDescription() { return description; }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<EntryDetails> getEntradas() {
        return entradas;
    }

    public List<EntryMediaDetails> getMedia() {
        return media;
    }

   //@Override
    /*public String toString() {
        return "\n" +
                "id: " + id + "\n" +
                "title: " + title + "\n" +
    }*/
}
