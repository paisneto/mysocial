package com.example.nunocoelho.mysocial.login;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Ernesto Casanova on 21/05/2017.
 */

public class Details extends RealmObject {

   // @Required @Index
   // public String username;
  //  @Required
    //public String password;

    private String oauthID;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String raw_update_details;
    private String nickname;
    private String gender;
    private String token;
    private String photoUri;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRaw_update_details() {
        return raw_update_details;
    }

    public void setRaw_update_details(String raw_update_details) {
        this.raw_update_details = raw_update_details;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhotoUri() {
        return photoUri;
    }
    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOauthID() {
        return oauthID;
    }

    public void setOauthID(String oauthID) {
        this.oauthID = oauthID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
