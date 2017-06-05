package com.example.nunocoelho.mysocial.trip;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ecasanova on 04/06/2017.
 */

public class EntryMediaDetails {

    @SerializedName("_id")
    private String id;

    @SerializedName("originalname")
    private String originalname;

    @SerializedName("path")
    private String path;

    private List<EntryMediaDetails> entradas;

    public EntryMediaDetails() {
        id = "";
        path="";
        originalname="";
    }

    public void setID(String id) { this.id = id;}

    public String getID() { return id; }

    public void setPath(String path) { this.path = path;}

    public String getPath() { return path; }

    public void setOriginalname(String originalname) { this.path = originalname;}

    public String getOriginalname() { return originalname; }

    public List<EntryMediaDetails> getEntradas() {
        return entradas;
    }
}
