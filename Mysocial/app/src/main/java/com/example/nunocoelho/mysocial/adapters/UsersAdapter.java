package com.example.nunocoelho.mysocial.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.login.Details;

import io.realm.RealmResults;

/**
 * Created by ecasanova on 03/06/2017.
 */

public class UsersAdapter extends BaseAdapter {

    RealmResults<Details> datasource;
    Context context;

    public UsersAdapter(Context ctx, RealmResults<Details> ds) {
        this.context = ctx;
        this.datasource = ds;
    }

    @Override
    public int getCount() {
        return datasource.size();
    }

    @Override
    public Object getItem(int i) {
        return datasource.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        TextView tv_username, tv_password;

        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.login,null);
        }

        //tv_username = (TextView) view.findViewById(R.id.tv_username);
        //tv_password = (TextView) view.findViewById(R.id.tv_password);

        Details p = datasource.get(i);

        //tv_username.setText(p.username + "");
        //tv_password.setText(p.password + "");

        return view;


    }
}
