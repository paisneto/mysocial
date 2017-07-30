package com.example.nunocoelho.mysocial.moment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.adapters.MomentsAdapter;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditMommentActivity extends AppCompatActivity {

    private static MomentsAdapter adapter;
    private ListView lv_comments;
    private TextView tv_titledetail, tv_placedetail, tv_datedetail, tv_narrativedetail, tv_latdetail,  tv_londetail;
    private String _id_moment, strTripID, strOriginalName, userName, userEmail;
    private ArrayList<EntryDetailsMoment> entryDetailsMomentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_momment);
        final Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Moment");

        tv_titledetail = (TextView)findViewById(R.id.tv_title_comment);
        tv_placedetail = (TextView)findViewById(R.id.tv_place_comment);
        tv_datedetail = (TextView) findViewById(R.id.tv_date_comment);
        tv_narrativedetail = (TextView) findViewById(R.id.tv_narrative_comment);

        _id_moment = intent.getStringExtra("_id");
        tv_titledetail.setText(intent.getStringExtra("title"));
        tv_placedetail.setText(intent.getStringExtra("place"));
        tv_narrativedetail.setText(intent.getStringExtra("narrative"));
        try {

            String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            String reformattedStr = new SimpleDateFormat("dd MMMM yyyy").format(new SimpleDateFormat(DATE_FORMAT_PATTERN).parse(intent.getStringExtra("moment_date")));
            tv_datedetail.setText(reformattedStr);    // format output
        } catch (Exception e) {
            e.printStackTrace();
        }

        //tv_latdetail.setText(intent.getStringExtra("lat"));
        //tv_londetail.setText(intent.getStringExtra("lon"));
        userName = intent.getStringExtra("userName");
        userEmail = intent.getStringExtra("userEmail");
        strTripID = intent.getStringExtra("trip");
        strOriginalName = intent.getStringExtra("originalname");

        lv_comments    = (ListView) findViewById(R.id.lv_comments);

        entryDetailsMomentList = new ArrayList<>();

        adapter = new MomentsAdapter(entryDetailsMomentList, getApplicationContext());

        lv_comments.setAdapter(adapter);

        showMoments();
    }


    //metodo para carregar os moments de uma viagem
    protected void showMoments(){
        MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
        Call<AnwserMoment> call = api.getMomentsTrip(
                strTripID
        );
        call.enqueue(new Callback<AnwserMoment>() {

            @Override
            public void onResponse(Call<AnwserMoment> call, Response<AnwserMoment> response) {
                if(response.code() == 200) {
                    AnwserMoment resp = response.body();
                    for(EntryDetailsMoment e : resp.getEntradas()) {
                        entryDetailsMomentList.add(e);
                    }
                    adapter.notifyDataSetChanged();
                    adapter.notifyDataSetInvalidated();
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "Else DetailTripActivity toast!";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<AnwserMoment> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
