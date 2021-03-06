package com.example.nunocoelho.mysocial.trip;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
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
    SearchView searchView = null;
    private SwipeRefreshLayout swipeContainer;
    private static DetailTripAdapter adapter;
    private Button btn_search;
    private FloatingActionButton btn_addtrip, btn_listtrips_marker;
   // private Class lastActivity;
    private TextView tv_title, tv_country,tv_res;
    private ListView lv_trips;
    private String output;
    private ArrayList<EntryDetails> entryDetailsList;
    private ProgressBar spinner;
    public Toolbar toolbar;
    private ArrayList<Markers> listMarkers = new ArrayList<Markers>();
    private String userName = "", userEmail = "", photoUrl = "", searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_trip);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.tv_title_header);

        if(!Utils.isNetworkConnected(ListTripActivity.this)) { Toast.makeText(ListTripActivity.this, "Error - No Network Connection!", Toast.LENGTH_SHORT).show(); finish(); }

        final Intent intent = getIntent();
        userName             = intent.getStringExtra("userName");
        userEmail             = intent.getStringExtra("userEmail");
        photoUrl = intent.getStringExtra("photoUrl");


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        /*swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isScroll = true;
               // showTrips();
                swipeContainer.setRefreshing(false);
            }
        });*/

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showTrips();
                        swipeContainer.setRefreshing(false);
                    }
                }, 2000);
            }
        });


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);
        ImageView iv_photo = (ImageView)header.findViewById(R.id.iv_photo);
        TextView tv_name = (TextView)header.findViewById(R.id.tv_name);
        TextView tv_email = (TextView)header.findViewById(R.id.tv_email);
        tv_name.setText(userName);
        tv_email.setText(userEmail);

        if (photoUrl == null) iv_photo.setImageResource(R.drawable.logo);
        else new Utils.DownloadImageTask((ImageView) iv_photo.findViewById(R.id.iv_photo)).execute(photoUrl);

//        name = (TextView)header.findViewById(R.id.tv_name);
//        email = (TextView)header.findViewById(R.id.tv_email);

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
        intent.putExtra("userName", userName);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
    }

    //metodo para ir para a AddTripActivity
    protected void goAddTrip(){
        Intent intent = new Intent(this, AddTripActivity.class);
        intent.putExtra("userName", userName);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
    }

    //metodo para carregar as viagens todoas
    protected void showTrips(){
        final Dialog progress_spinner = Utils.LoadingSpinner(this);
        progress_spinner.show();
        entryDetailsList.clear();
        MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
        Call<Anwser> call = api.getAllTrips(userEmail, searchText);
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
                        searchText = "";
                        adapter.notifyDataSetChanged();
                        adapter.notifyDataSetInvalidated();
                    } catch (Exception e) {
                        progress_spinner.dismiss();
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

        // Associate searchable configuration with the SearchView
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 0) {
                    searchText = query;
                    showTrips();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        Intent intent;
        switch (item.getItemId()) {
           case R.id.action_search:
               //Toast.makeText(ListTripActivity.this, "Error - Loading data!", Toast.LENGTH_SHORT).show();
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

        }
// else if (id == R.id.nav_share) {
//
//        }
            else if (id == R.id.nav_send) {
            LoginManager.getInstance().logOut();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
