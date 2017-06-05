package com.example.nunocoelho.mysocial.trip;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Nuno Coelho on 27/05/2017.
 */

public class Post {

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
