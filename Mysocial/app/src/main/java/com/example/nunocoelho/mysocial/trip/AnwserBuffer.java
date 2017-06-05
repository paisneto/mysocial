package com.example.nunocoelho.mysocial.trip;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nuno Coelho on 21/05/2017.
 */

public class AnwserBuffer {

    @SerializedName("img")
    private List<EntryMediaDetails> entradas;

    public List<EntryMediaDetails> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<EntryMediaDetails> entradas) {
        this.entradas = entradas;

    }
}
