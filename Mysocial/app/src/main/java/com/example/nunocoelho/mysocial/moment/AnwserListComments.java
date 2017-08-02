package com.example.nunocoelho.mysocial.moment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ecasanova on 01/08/2017.
 */

public class AnwserListComments {
    @SerializedName("comments")
    private List<EntryDetailsComments> entradas;

    public List<EntryDetailsComments> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<EntryDetailsComments> entradas) {
        this.entradas = entradas;

    }
}
