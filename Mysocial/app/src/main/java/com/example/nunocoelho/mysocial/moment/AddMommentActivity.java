package com.example.nunocoelho.mysocial.moment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.nunocoelho.mysocial.LoginActivity;
import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;
import com.example.nunocoelho.mysocial.trip.DetailTripActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMommentActivity extends AppCompatActivity {

    private Button btn_save;
    private Button btn_back;
    String strTrip;
    EditText et_title, et_place, et_moment_date, et_narrative;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_momment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add Moment");

        final Intent intent = getIntent();
        strTrip             = intent.getStringExtra("_id");//ID Trip from trip list

        et_title = (EditText) findViewById(R.id.et_title);
        et_place = (EditText) findViewById(R.id.et_place);
        et_moment_date = (EditText) findViewById(R.id.et_moment_date);
        et_narrative = (EditText) findViewById(R.id.et_narrative);

        btn_save       = (Button) findViewById(R.id.btn_save);
        //voltar à activity anterior
        btn_save.setOnClickListener(new View.OnClickListener(
        ) {
            @Override
            public void onClick(View v) {
                getLoadData();
            }
        });
    }


    //metodo para voltar ao ecra anterior
    protected void  backLastActivity(){
        onBackPressed();
    }

    private void getLoadData()
    {
        MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);

        String strTitle        = et_title.getText().toString().trim();
        String strPlace        = et_place.getText().toString().trim();
        String strDate         = et_moment_date.getText().toString().trim();
        String strNarrative    = et_narrative.getText().toString().trim();
        String strLat          = "41.5317";
        String strLon          = "-8.6179";


        Call<EntryDetailsMoment> call = api.addMomment(
                strTitle, strPlace, strDate, strNarrative, strLat,strLon, strTrip
        );

        call.enqueue(new Callback<EntryDetailsMoment>() {

            @Override
            public void onResponse(Call<EntryDetailsMoment> call, Response<EntryDetailsMoment> response) {
                if(response.code() == 200) {
                    //EntryDetailsMoment resp = response.body();
                    Toast.makeText(AddMommentActivity.this,
                            "Saved!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<EntryDetailsMoment> call, Throwable t) {
                Toast.makeText(AddMommentActivity.this,
                        "Error saving!", Toast.LENGTH_SHORT).show();
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
        Intent intent;
        switch (item.getItemId()) {
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
