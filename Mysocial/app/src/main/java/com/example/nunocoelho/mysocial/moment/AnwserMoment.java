package com.example.nunocoelho.mysocial.moment;

import com.example.nunocoelho.mysocial.trip.EntryDetails;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ecasanova on 02/06/2017.
 */

public class AnwserMoment {

    @SerializedName("docs")
    //@SerializedName("")
    private List<EntryDetailsMoment> entradas;

    public List<EntryDetailsMoment> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<EntryDetailsMoment> entradas) {
        this.entradas = entradas;

    }
}
