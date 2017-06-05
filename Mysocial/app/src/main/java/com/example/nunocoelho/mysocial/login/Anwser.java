package com.example.nunocoelho.mysocial.login;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ernesto Casanova on 21/05/2017.
 */

public class Anwser extends RealmObject {

    public RealmList<Details> users;
}

