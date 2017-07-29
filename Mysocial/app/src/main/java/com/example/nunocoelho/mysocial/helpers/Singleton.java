package com.example.nunocoelho.mysocial.helpers;

import android.content.Context;

/**
 * Created by ecasanova on 28/07/2017.
 */

public class Singleton {
    private static Singleton instance = null;

    //a private constructor so no instances can be made outside this class
    private Singleton() {}

    //Everytime you need an instance, call this
    public static synchronized Singleton getInstance() {
        if(instance == null)
            instance = new Singleton();

        return instance;
    }

    //Initialize this or any other variables in probably the Application class
    public void init(Context context) {}
}
