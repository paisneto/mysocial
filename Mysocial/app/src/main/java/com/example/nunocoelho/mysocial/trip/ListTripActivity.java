package com.example.nunocoelho.mysocial.trip;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nunocoelho.mysocial.LoginActivity;
import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.adapters.DetailTripAdapter;
import com.example.nunocoelho.mysocial.login.Details;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;

import java.util.ArrayList;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListTripActivity extends AppCompatActivity {

    //declaração das variaveis

    private static DetailTripAdapter adapter;
    private Button btn_search;
    private FloatingActionButton btn_addtrip;
   // private Class lastActivity;
    private TextView tv_title, tv_country,tv_res;
    private ListView lv_trips;
    private  String output;
    private ArrayList<EntryDetails> entryDetailsList;
    private ProgressBar spinner;
    public Toolbar toolbar;

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

        //spinner.setVisibility(View.VISIBLE);
        showTrips();
        //spinner.setVisibility(View.GONE);

        //botao para chamar a pesquisa
        /*btn_search.setOnClickListener(new View.OnClickListener(
        ) {
            @Override
            public void onClick(View v) {
                //verifica se os dados de login estão corretos
                goSearchActivity();
            }
        });*/

       //botao para adicionar uma nova viagem
       btn_addtrip.setOnClickListener(new View.OnClickListener(
        ) {
            @Override
            public void onClick(View v) {
                goAddTrip();

            }
        });
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
        intent.putExtra("country", adapter.getItem(i).getCountry());
        intent.putExtra("date", adapter.getItem(i).getDate());
        intent.putExtra("description", adapter.getItem(i).getDescription());
        startActivity(intent);
    }
    //metodo para ir para a AddTripActivity
    protected void goAddTrip(){
        Intent intent = new Intent(this, AddTripActivity.class);
        startActivity(intent);
    }

    //metodo para carregar as viagens todoas
    protected void showTrips(){
        //spinner = (ProgressBar)findViewById(R.id.pb_progressbar);
        //spinner.setVisibility(ProgressBar.VISIBLE);

        final ProgressDialog nDialog;
        nDialog = new ProgressDialog(this);
        nDialog.setMessage("Loading..");
        nDialog.setTitle("Getting Data");
        //nDialog.getWindow().setBackgroundDrawableResource(R.color.colorTransparent);
        //nDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();

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
                    Anwser resp = response.body();
                    for(EntryDetails e : resp.getEntradas()) {
                        entryDetailsList.add(e);
                    }
                    adapter.notifyDataSetChanged();
                    adapter.notifyDataSetInvalidated();
                    //spinner.setVisibility(View.GONE);
                    nDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Anwser> call, Throwable t) {
                t.printStackTrace();
                //spinner.setVisibility(View.GONE);
                nDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.layout_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
           // case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
           //     return true;

            case R.id.action_search:
                intent = new Intent(this, SearchTripActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                intent = new Intent(this, LoginActivity.class);
                intent.putExtra("kill_user", "yes");
                startActivity(intent);
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
