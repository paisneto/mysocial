package com.example.nunocoelho.mysocial.adapters;

/**
 * Created by ecasanova on 31/07/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.moment.EntryDetailsComments;

import java.util.List;

public class MomentCommentsAdapter extends ArrayAdapter<EntryDetailsComments> {

    private List<EntryDetailsComments> datasource;

    private Context contexto;

    public MomentCommentsAdapter(List<EntryDetailsComments> ds, Context ctx){
        super(ctx, R.layout.detail_list_comments_layout, ds);
        this.datasource = ds;
        this.contexto = ctx;
    }

    @Override
    public int getCount() {
        return datasource.size();
    }

    @Override
    public EntryDetailsComments getItem(int position) {
        return datasource.get(position);
    }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        EntryDetailsComments value = getItem(i);
        TextView tv_title_comment, tv_my_comment;
        ImageView img_avatar;

        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(contexto);
            convertView = inflater.inflate(R.layout.detail_list_comments_layout, null);
        }

        tv_title_comment = (TextView)convertView.findViewById(R.id.tv_title_comment);
        img_avatar = (ImageView)convertView.findViewById(R.id.img_avatar_comments);
        //tv_my_comment = (TextView)convertView.findViewById(R.id.tv_my_comment);



        try {
            tv_title_comment.setText(value.getText());
            //tv_my_comment.setText(value.getPlace());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //if (value.getPostedByEmail().isEmpty()) img_avatar.setImageResource(R.drawable.avatar_anon);
        //else new Utils.DownloadImageTask((ImageView) convertView.findViewById(R.id.img_avatar_comments)).execute(MysocialEndpoints.MEDIA_URL + value.getOriginalname());
        return convertView;
    }

}
