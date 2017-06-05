package com.example.nunocoelho.mysocial.login;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Ernesto Casanova on 21/05/2017.
 */

public class Details extends RealmObject {

    @Required @Index
    public String username;
    @Required
    public String password;
}
