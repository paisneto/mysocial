package com.example.nunocoelho.mysocial.trip;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.adapters.DetailTripAdapter;
import com.example.nunocoelho.mysocial.helpers.Markers;
import com.example.nunocoelho.mysocial.helpers.Utils;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;
import com.facebook.login.LoginManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListTripActivity extends AppCompatActivity
     implements NavigationView.OnNavigationItemSelectedListener {

    //declaração das variaveis
    private static DetailTripAdapter adapter;
    private Button btn_search;
    private FloatingActionButton btn_addtrip, btn_listtrips_marker;
   // private Class lastActivity;
    private TextView tv_title, tv_country,tv_res;
    private ListView lv_trips;
    private  String output;
    private ArrayList<EntryDetails> entryDetailsList;
    private ProgressBar spinner;
    public Toolbar toolbar;
    private ArrayList<Markers> listMarkers = new ArrayList<Markers>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_trip);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.tv_title_header);

        tv_title    = (TextView) findViewById(R.id.tv_title);
        tv_country  = (TextView)findViewById(R.id.tv_country);
        btn_addtrip = (FloatingActionButton)findViewById(R.id.btn_addtrip);
        //btn_search  = (android.widget.Button)findViewById(R.id.btn_back);

        lv_trips    = (ListView) findViewById(R.id.lv_trips);

        entryDetailsList = new ArrayList<>();

        adapter = new DetailTripAdapter(entryDetailsList, getApplicationContext());

        lv_trips.setAdapter(adapter);

        lv_trips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                goDetailTrip(i);
            }
        });

        showTrips();
        //botao para adicionar uma nova viagem
        btn_addtrip.setOnClickListener(new View.OnClickListener(
        ) {
            @Override
            public void onClick(View v) {
                goAddTrip();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



       /* NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        MenuItem btnFacebookLogout = navigationView.getMenu().findItem(R.id.nav_send);
        btnFacebookLogout.setOnMenuItemClickListener();*/

    }

    //metodo para voltar para a LoginActivity
    protected void goSearchActivity (){
        Intent intent = new Intent(this, SearchTripActivity.class);
        startActivity(intent);
    }
    //metodo para ir para a DetailTripActivity
    protected void goDetailTrip(int i) {
        Intent intent = new Intent(this, DetailTripActivity.class);
        intent.putExtra("_id", adapter.getItem(i).getId());
        intent.putExtra("title", adapter.getItem(i).getTitle());
        intent.putExtra("country", adapter.getItem(i).getCountry());
        intent.putExtra("city", adapter.getItem(i).getCity());
        intent.putExtra("date", adapter.getItem(i).getDate());
        intent.putExtra("description", adapter.getItem(i).getDescription());
        intent.putExtra("originalname", adapter.getItem(i).getFilename());
        startActivity(intent);
    }

    //metodo para ir para a AddTripActivity
    protected void goAddTrip(){
        Intent intent = new Intent(this, AddTripActivity.class);
        startActivity(intent);
    }

    //metodo para carregar as viagens todoas
    protected void showTrips(){
        final Dialog progress_spinner = Utils.LoadingSpinner(this);
        progress_spinner.show();

        String limit, page, number, sort, title;

        limit = "500"; page = "1"; number = "100"; sort = "-created"; title = "";

        MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
        Call<Anwser> call = api.getAllTrips(
                //limit, page, number, sort, title//last one the search field name
                //title
                );
        call.enqueue(new Callback<Anwser>() {

            @Override
            public void onResponse(Call<Anwser> call, Response<Anwser> response) {
                if(response.code() == 200) {
                    try {
                        Anwser resp = response.body();
                        for(EntryDetails e : resp.getEntradas()) {
                            entryDetailsList.add(e);
                            listMarkers.add(new Markers(Double.valueOf(e.getLat()).doubleValue(), Double.valueOf(e.getLon()).doubleValue(), e.getCity()));
                        }
                        adapter.notifyDataSetChanged();
                        adapter.notifyDataSetInvalidated();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progress_spinner.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Anwser> call, Throwable t) {
                t.printStackTrace();
                progress_spinner.dismiss();
                Toast.makeText(ListTripActivity.this, "Error - Loading data!", Toast.LENGTH_SHORT).show();
            }
        });



    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        Intent intent;
        switch (item.getItemId()) {
           case R.id.action_settings:
               intent = new Intent(this, SearchTripActivity.class);
               startActivity(intent);
               return true;
            case R.id.action_markers:
                try {
                    Intent intt = new Intent(this, MapsActivity.class);
                    intt.putExtra("EntryDetailsList", (ArrayList<Markers>) listMarkers);
                    startActivity(intt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }


        //return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    //@Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            LoginManager.getInstance().logOut();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
