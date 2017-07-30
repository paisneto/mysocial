package com.example.nunocoelho.mysocial.moment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nunocoelho.mysocial.LoginActivity;
import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.adapters.MomentsAdapter;
import com.example.nunocoelho.mysocial.helpers.Utils;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;
import com.example.nunocoelho.mysocial.trip.EditTripActivity;
import com.example.nunocoelho.mysocial.trip.ListTripActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailMomentActivity extends AppCompatActivity {

    private static MomentsAdapter adapter;
    private Button btn_back, btn_class_1, btn_class_2, btn_class_3;
    private String _id_moment, strTripID, strOriginalName, userName, userEmail;
    private FloatingActionButton btn_editmomment;
    private ImageView iv_details_img_moment;
    private TextView tv_titledetail, tv_placedetail, tv_datedetail, tv_narrativedetail, tv_latdetail,  tv_londetail;
    private ArrayList<EntryDetailsMoment> entryDetailsMomentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_moment);
        final Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Detail Moment");

        btn_editmomment = (FloatingActionButton) findViewById(R.id.btn_editmomment);
        //btn_back = (Button) findViewById(R.id.btn_back);
        //fab_search = (FloatingActionButton)findViewById(R.id.fab_search);
        tv_titledetail = (TextView)findViewById(R.id.tv_details_moment_title);
        tv_placedetail = (TextView)findViewById(R.id.tv_details_moment_place);
        tv_datedetail = (TextView) findViewById(R.id.tv_details_moment_date);
        tv_narrativedetail = (TextView) findViewById(R.id.tv_details_moment_narrative);

        tv_latdetail = (TextView) findViewById(R.id.tv_details_moment_lat);
        tv_londetail = (TextView) findViewById(R.id.tv_details_moment_lon);

        _id_moment = intent.getStringExtra("_id");
        tv_titledetail.setText(intent.getStringExtra("title"));
        tv_placedetail.setText(intent.getStringExtra("place"));
        tv_narrativedetail.setText(intent.getStringExtra("narrative"));
        tv_latdetail.setText(intent.getStringExtra("lat"));
        tv_londetail.setText(intent.getStringExtra("lon"));
        userName = intent.getStringExtra("userName");
        userEmail = intent.getStringExtra("userEmail");
        strTripID = intent.getStringExtra("trip");
        strOriginalName = intent.getStringExtra("originalname");
        btn_class_1 = (Button) findViewById(R.id.btn_class_1);
        btn_class_2 = (Button) findViewById(R.id.btn_class_2);
        btn_class_3 = (Button) findViewById(R.id.btn_class_3);

        iv_details_img_moment    = (ImageView) findViewById(R.id.iv_details_img_moment);


        try {

            String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            String reformattedStr = new SimpleDateFormat("dd MMMM yyyy").format(new SimpleDateFormat(DATE_FORMAT_PATTERN).parse(intent.getStringExtra("moment_date")));
            tv_datedetail.setText(reformattedStr);    // format output
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (strOriginalName.isEmpty()) iv_details_img_moment.setImageResource(R.drawable.logo);
        else new Utils.DownloadImageTask((ImageView) iv_details_img_moment.findViewById(R.id.iv_details_img_moment)).execute(MysocialEndpoints.MEDIA_URL + strOriginalName);

        //para editar a trip
        btn_editmomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goEditMoment(intent);
            }
        });

        btn_class_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailMomentActivity.this, "Já fui muito feliz aqui!", Toast.LENGTH_SHORT).show();
                classifMoments(1);
            }
        });

        btn_class_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailMomentActivity.this, "Nunca tinha experimentado, estou maravilhado!", Toast.LENGTH_SHORT).show();
                classifMoments(2);
            }
        });

        btn_class_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailMomentActivity.this, "Esta vai direto para minha bucket list!", Toast.LENGTH_SHORT).show();
                classifMoments(3);
            }
        });
    }
    //metodo para voltar para a ListTripActivity
    protected void goListTrip(){
        Intent intent = new Intent(this, ListTripActivity.class);
        startActivity(intent);
    }
    //metodo para chamar a activity para editar a trip
    protected void goEditTrip(){
        Intent intent = new Intent(this, EditTripActivity.class);
        startActivity(intent);
    }

    //metodo para ir para a DetailTripActivity
    protected void goEditMoment(Intent i) {
        Intent intent = new Intent(this, EditMommentActivity.class);
        intent.putExtra("_id", i.getStringExtra("_id"));
        intent.putExtra("title", i.getStringExtra("title"));
        intent.putExtra("place", i.getStringExtra("place"));
        intent.putExtra("moment_date", i.getStringExtra("moment_date"));
        intent.putExtra("narrative", i.getStringExtra("narrative"));
        intent.putExtra("lat", i.getStringExtra("lat"));
        intent.putExtra("lon", i.getStringExtra("lon"));
        intent.putExtra("trip", i.getStringExtra("trip"));
        intent.putExtra("originalname", i.getStringExtra("originalname"));
        intent.putExtra("userName", userName);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
    }

    //metodo para chamar a activity para editar a trip
    protected void goAddMoment(Intent i){
        Intent intent = new Intent(this, AddMommentActivity.class);
        intent.putExtra("_id", i.getStringExtra("_id"));
        startActivity(intent);
    }

    //metodo para carregar os moments de uma viagem
    protected void showMoments(){
        MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
        Call<AnwserMoment> call = api.getMomentsTrip(_id_moment);
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
                }
            }

            @Override
            public void onFailure(Call<AnwserMoment> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    protected void classifMoments(final int value){
        MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
        Call<AnwserMoment> call = api.getClassifMoments(
                _id_moment, value, userEmail, userName
        );
        call.enqueue(new Callback<AnwserMoment>() {

            @Override
            public void onResponse(Call<AnwserMoment> call, Response<AnwserMoment> response) {
                if(response.code() == 200) {
                    Toast.makeText(DetailMomentActivity.this, "Classification Saved! - " + value, Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.layout_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                // User chose the "Settings" item, show the app settings UI...
               Intent intent_home = new Intent(this, ListTripActivity.class);
               startActivity(intent_home);

            case R.id.action_markers:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case R.id.logout:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
