package com.example.nunocoelho.mysocial.moment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ecasanova on 03/06/2017.
 */

public class AnwserListMoments {
    @SerializedName("docs")
    private List<EntryDetailsMoment> entradas;

    public List<EntryDetailsMoment> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<EntryDetailsMoment> entradas) {
        this.entradas = entradas;

    }
}
