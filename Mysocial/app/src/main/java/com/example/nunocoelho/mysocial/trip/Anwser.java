package com.example.nunocoelho.mysocial.trip;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Nuno Coelho on 21/05/2017.
 */

public class Anwser {

    @SerializedName("docs")
    private List<EntryDetails> entradas;

    public List<EntryDetails> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<EntryDetails> entradas) {
        this.entradas = entradas;

    }
}
