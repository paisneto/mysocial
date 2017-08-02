package com.example.nunocoelho.mysocial.moment;

import java.util.List;

/**
 * Created by ecasanova on 01/08/2017.
 */

public class EntryDetailsComments {

    private String text;

    private String _id;

    private String created;

    private String postedByName;

    private String postedByEmail;

    private List<EntryDetailsComments> entradas;

    public String getText ()
    {
        return text;
    }

    public void setText (String text)
    {
        this.text = text;
    }

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
    }

    public String getCreated ()
    {
        return created;
    }

    public void setCreated (String created)
    {
        this.created = created;
    }

    public String getPostedByName ()
    {
        return postedByName;
    }

    public void setPostedByName (String postedByName)
    {
        this.postedByName = postedByName;
    }

    public String getPostedByEmail ()
    {
        return postedByEmail;
    }

    public void setPostedByEmail (String postedByEmail)
    {
        this.postedByEmail = postedByEmail;
    }

    public List<EntryDetailsComments> getEntradas() {
        return entradas;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [text = "+text+", _id = "+_id+", created = "+created+", postedByName = "+postedByName+", postedByEmail = "+postedByEmail+"]";
    }

}
