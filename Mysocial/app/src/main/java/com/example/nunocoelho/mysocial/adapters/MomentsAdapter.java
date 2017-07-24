package com.example.nunocoelho.mysocial.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.helpers.Utils;
import com.example.nunocoelho.mysocial.moment.EntryDetailsMoment;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;

import java.util.List;

/**
 * Created by ecasanova on 02/06/2017.
 */

public class MomentsAdapter extends ArrayAdapter<EntryDetailsMoment> {

    private List<EntryDetailsMoment> datasource;

    private Context contexto;

    public MomentsAdapter(List<EntryDetailsMoment> ds, Context ctx){
        super(ctx, R.layout.detail_list_trip_layout, ds);
        this.datasource = ds;
        this.contexto = ctx;
    }

    @Override
    public int getCount() {
        return datasource.size();
    }

    @Override
    public EntryDetailsMoment getItem(int position) {
        return datasource.get(position);
    }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        EntryDetailsMoment value = getItem(i);
        TextView tv_title, tv_country;
        ImageView img_avatar;

        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(contexto);
            convertView = inflater.inflate(R.layout.detail_list_trip_layout, null);
        }

        tv_title = (TextView)convertView.findViewById(R.id.tv_title);
        img_avatar = (ImageView)convertView.findViewById(R.id.img_avatar);
        //tv_country = (TextView)convertView.findViewById(R.id.tv_country);

        tv_title.setText(value.getTitle());
        //tv_country.setText(value.getCountry());

        if (value.getOriginalname().isEmpty()) img_avatar.setImageResource(R.drawable.avatar_anon);
        else new Utils.DownloadImageTask((ImageView) convertView.findViewById(R.id.img_avatar)).execute(MysocialEndpoints.MEDIA_URL + value.getOriginalname());
        return convertView;
    }

}
