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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nunocoelho.mysocial.LoginActivity;
import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.adapters.MomentsAdapter;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;
import com.example.nunocoelho.mysocial.trip.EditTripActivity;
import com.example.nunocoelho.mysocial.trip.ListTripActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailMomentActivity extends AppCompatActivity {

    private static MomentsAdapter adapter;
    private Button btn_back, btn_class_1, btn_class_2, btn_class_3;
    private String _id_moment;
    private FloatingActionButton btn_editmomment;
    private ListView lv_momments;
    private TextView tv_titledetail, tv_countrydetail, tv_citydetail, tv_datedetail, tv_descriptiondetail;
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
        tv_titledetail = (TextView)findViewById(R.id.tv_titledetail);
        tv_countrydetail = (TextView)findViewById(R.id.tv_countrydetail);
        tv_citydetail = (TextView) findViewById(R.id.tv_citydetail);
        tv_datedetail = (TextView) findViewById(R.id.tv_datedetail);
        tv_descriptiondetail = (TextView) findViewById(R.id.tv_descriptiondetail);

        _id_moment = intent.getStringExtra("_id");
        tv_titledetail.setText(intent.getStringExtra("title"));
        tv_countrydetail.setText(intent.getStringExtra("country"));
        tv_citydetail.setText(intent.getStringExtra("city"));
        tv_datedetail.setText(intent.getStringExtra("date"));
        tv_descriptiondetail.setText(intent.getStringExtra("description"));

        btn_class_1 = (Button) findViewById(R.id.btn_class_1);
        btn_class_2 = (Button) findViewById(R.id.btn_class_2);
        btn_class_3 = (Button) findViewById(R.id.btn_class_3);

        lv_momments    = (ListView) findViewById(R.id.lv_momments);

        entryDetailsMomentList = new ArrayList<>();

        adapter = new MomentsAdapter(entryDetailsMomentList, getApplicationContext());

        lv_momments.setAdapter(adapter);

        showMoments();

        lv_momments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                goDetailMoment(i);
            }
        });

        //para editar a trip
        btn_editmomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAddMoment(intent);
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
    protected void goDetailMoment(int i) {
        Intent intent = new Intent(this, DetailMomentActivity.class);
        intent.putExtra("_id", adapter.getItem(i).getId());
        intent.putExtra("title", adapter.getItem(i).getTitle());
        intent.putExtra("place", adapter.getItem(i).getPlace());
        intent.putExtra("moment_date", adapter.getItem(i).getMomentDate());
        intent.putExtra("narrative", adapter.getItem(i).getNarrative());
        intent.putExtra("lat", adapter.getItem(i).getLat());
        intent.putExtra("lon", adapter.getItem(i).getLon());
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
                _id_moment, value, "ernestonet@msn.com", "Ernesto Casanova"
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
        getMenuInflater().inflate(R.menu.layout, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

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
