package com.example.nunocoelho.mysocial.moment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.adapters.MomentCommentsAdapter;
import com.example.nunocoelho.mysocial.helpers.Utils;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditMommentActivity extends AppCompatActivity {

    private static MomentCommentsAdapter adapter;
    private ListView lv_edit_comments;
    private TextView tv_titledetail, tv_placedetail, tv_datedetail, tv_narrativedetail, tv_latdetail,  tv_londetail;
    private String _id_moment, strTripID, strOriginalName, userName, userEmail;
    private ArrayList<EntryDetailsComments> entryDetailsCommentsList;
    private FloatingActionButton btn_save_comment;

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


        if(!Utils.isNetworkConnected(EditMommentActivity.this)) { Toast.makeText(EditMommentActivity.this, "Error - No Network Connection!", Toast.LENGTH_SHORT).show(); finish(); }


        tv_titledetail = (TextView)findViewById(R.id.tv_title_comment);
        tv_placedetail = (TextView)findViewById(R.id.tv_place_comment);
        tv_datedetail = (TextView) findViewById(R.id.tv_date_comment);
        tv_narrativedetail = (TextView) findViewById(R.id.tv_narrative_comment);

        _id_moment = intent.getStringExtra("_id");
        tv_titledetail.setText(intent.getStringExtra("title"));
        tv_placedetail.setText(intent.getStringExtra("place"));
        tv_narrativedetail.setText(intent.getStringExtra("narrative"));

        btn_save_comment = (FloatingActionButton)findViewById(R.id.btn_save_comment);

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

        lv_edit_comments    = (ListView) findViewById(R.id.lv_edit_comments);

        entryDetailsCommentsList = new ArrayList<>();

        adapter = new MomentCommentsAdapter(entryDetailsCommentsList, getApplicationContext());

        lv_edit_comments.setAdapter(adapter);

        lv_edit_comments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                //goDetailMoment(i);
            }
        });

        showMoments();

        btn_save_comment.setOnClickListener(new View.OnClickListener(
        ) {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(EditMommentActivity.this);

                alert.setTitle("New Comment!");
                alert.setMessage(" ");

                // Set an EditText view to get user input
                final EditText input = new EditText(EditMommentActivity.this);
                alert.setView(input);

                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final Dialog progress_spinner = Utils.LoadingSpinner(EditMommentActivity.this);
                        progress_spinner.show();
                        MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
                        Call<AnwserMoment> call = api.addCommentNew(
                                _id_moment.trim()
                                , input.getText().toString(), userName, userEmail
                        );

                        call.enqueue(new Callback<AnwserMoment>() {

                            @Override
                            public void onResponse(Call<AnwserMoment> call, Response<AnwserMoment> response) {
                                if(response.code() == 200) {
                                    progress_spinner.dismiss();

                                    Toast.makeText(EditMommentActivity.this, "Comment Saved!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<AnwserMoment> call, Throwable t) {
                                progress_spinner.dismiss();
                                Toast.makeText(EditMommentActivity.this, "Comment Saved!", Toast.LENGTH_SHORT).show();
                                t.printStackTrace();
                            }
                        });
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                alert.show();
            }
        });
    }


    //metodo para carregar os moments de uma viagem
    protected void showMoments(){
        try {
            MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
            Call<List<AnwserListComments>> call = api.getMomentComments(
                    strTripID
            );
            call.enqueue(new Callback<List<AnwserListComments>>() {

                @Override
                public void onResponse(Call<List<AnwserListComments>> call, Response<List<AnwserListComments>> response) {
                    if(response.code() == 200) {
                        List<AnwserListComments> resp = response.body();
                        try {
                            int size = resp.size();
                            for (int i= 0; i< size; i++){
                                for(EntryDetailsComments e : resp.get(i).getEntradas()) {
                                    entryDetailsCommentsList.add(e);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
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
                public void onFailure(Call<List<AnwserListComments>> call, Throwable t) {
                    t.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
