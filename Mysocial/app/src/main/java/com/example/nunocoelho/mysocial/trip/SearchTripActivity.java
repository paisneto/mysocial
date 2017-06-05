package com.example.nunocoelho.mysocial.trip;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.nunocoelho.mysocial.LoginActivity;
import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;

import retrofit2.Call;

public class SearchTripActivity extends AppCompatActivity {
    private ImageButton btn_search;
    private Button btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_trip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.tv_title_header_search);

//        btn_back   = (Button) findViewById(R.id.btn_back);
        btn_search = (ImageButton) findViewById(R.id.btn_search);

        //butao para fazer a pesquisa
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTrips();
            }
        });

    }

    //metodo para voltar para a ListTripActivity
    protected void goListTrip(){
        Intent intent = new Intent(this, ListTripActivity.class);
        startActivity(intent);
    }

    //metodo para fazer a pesquisa
    protected void searchTrips(){
        MysocialEndpoints api =
                MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
        String id = "5929475dc601d70004525c16";

        Call<Anwser> call = api.getTripbyId(
                id

        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.logout:
                intent = new Intent(this, LoginActivity.class);
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