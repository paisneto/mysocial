package com.example.nunocoelho.mysocial.moment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nunocoelho.mysocial.LoginActivity;
import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.helpers.Utils;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;
import com.example.nunocoelho.mysocial.trip.DetailTripActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMommentActivity extends AppCompatActivity {

    private Button btn_save, btn_back;
    String strTrip, strTitle, strCountry, strCity, strDate, strDescription, strFileOriginalName, userName, userEmail;
    private ImageView iv_add_image_moment;
    EditText et_moment_title, et_moment_date, et_narrative;
    TextView tv_result_place, tv_result_moment_lat, tv_result_moment_lon;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar myCalendar;
    private String momentDate;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2, MARKER_PICKER_REQUEST = 3;
    private Bitmap imageBitmap;

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
        strTitle            = intent.getStringExtra("title");
        strCountry          = intent.getStringExtra("country");
        strCity             = intent.getStringExtra("city");
        strDate             = intent.getStringExtra("date");
        strDescription      = intent.getStringExtra("description");
        strFileOriginalName = intent.getStringExtra("originalname");
        userName            = intent.getStringExtra("userName");
        userEmail           = intent.getStringExtra("userEmail");

        et_moment_title = (EditText) findViewById(R.id.et_moment_title);
        tv_result_place = (TextView) findViewById(R.id.tv_result_place);
        tv_result_moment_lat = (TextView) findViewById(R.id.tv_result_moment_lat);
        tv_result_moment_lon = (TextView) findViewById(R.id.tv_result_moment_lon);
        et_moment_date = (EditText) findViewById(R.id.et_moment_date);
        et_narrative = (EditText) findViewById(R.id.et_narrative);
        iv_add_image_moment = (ImageView) findViewById(R.id.iv_add_image_moment);

        myCalendar = Calendar.getInstance();
        et_moment_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddMommentActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        date = new DatePickerDialog.OnDateSetListener() {
            String MY_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            String DATE_FORMAT_PATTERN = "EEE MMM dd HH:mm:ss z yyyy";
            StringBuilder Date;
            @Override
            public void onDateSet(DatePicker view, int year, int month,
                                  int day) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);

                Date = new StringBuilder().append(year).append("-").append(month + 1).append("-").append(day).append(" ");
                updateLabel();
            }

            private void updateLabel() {
                momentDate = Date.toString();
                String myViewFormat = "dd MMMM yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myViewFormat, Locale.ENGLISH);

                et_moment_date.setText(sdf.format(myCalendar.getTime()));
            }

        };

        Button markerPickerButton = (Button) findViewById(R.id.btn_moment_marker_picker);
        markerPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(AddMommentActivity.this), MARKER_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(getApplicationContext(),"There was a problem with your Google Services", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        btn_save       = (Button) findViewById(R.id.btn_save);
        //voltar à activity anterior
        btn_save.setOnClickListener(new View.OnClickListener(
        ) {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(et_moment_title.getText().toString())
                            && !TextUtils.isEmpty(tv_result_place.getText().toString())
                            && !TextUtils.isEmpty(et_moment_date.getText().toString())
                            && !TextUtils.isEmpty(et_narrative.getText().toString())
                            && !TextUtils.isEmpty(tv_result_moment_lat.getText().toString())
                            && !TextUtils.isEmpty(tv_result_moment_lon.getText().toString())) {
                        loadFiles();
                } else  Toast.makeText(AddMommentActivity.this,
                        "Missing fields!", Toast.LENGTH_SHORT).show();
            }
        });

        //para inserir uma imagem da camera do telemovel ou da galeria
        iv_add_image_moment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    //metodo para voltar ao ecra anterior
    protected void  backLastActivity(){
        onBackPressed();
    }

    private void loadFiles()
    {
        final Dialog progress_spinner = Utils.LoadingSpinner(AddMommentActivity.this);
        progress_spinner.show();

        String strTitle        = et_moment_title.getText().toString().trim();
        String strPlace        = tv_result_place.getText().toString().trim();
        String strDate         = momentDate.toString();
        String strNarrative    = et_narrative.getText().toString().trim();
        String strLat          = tv_result_moment_lat.getText().toString().trim();
        String strLon          = tv_result_moment_lon.getText().toString().trim();

        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        String path = Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY;
        OutputStream fOut = null;
        File fileU = new File(path, randomUUIDString+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        try {
            fOut = new FileOutputStream(fileU);
        } catch (FileNotFoundException e) {
            progress_spinner.dismiss();
            e.printStackTrace();
        }


        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
        try {
            fOut.flush(); // Not really required
        } catch (IOException e) {
            progress_spinner.dismiss();
            e.printStackTrace();
        }
        try {
            fOut.close(); // do not forget to close the stream
        } catch (IOException e) {
            progress_spinner.dismiss();
            e.printStackTrace();
        }

        // use the FileUtils to get the actual file by uri
        File file = new File(String.valueOf(fileU));

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"),file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData(randomUUIDString+".jpg", file.getName(), requestFile);

        MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
        RequestBody title = RequestBody.create(okhttp3.MultipartBody.FORM, strTitle);
        RequestBody place = RequestBody.create(okhttp3.MultipartBody.FORM, strPlace);
        RequestBody narrative = RequestBody.create(okhttp3.MultipartBody.FORM, strNarrative);
        RequestBody moment_date = RequestBody.create(okhttp3.MultipartBody.FORM, strDate);
        RequestBody lat = RequestBody.create(okhttp3.MultipartBody.FORM, strLat);
        RequestBody lon = RequestBody.create(okhttp3.MultipartBody.FORM, strLon);
        RequestBody trip = RequestBody.create(okhttp3.MultipartBody.FORM, strTrip);

        Call<EntryDetailsMoment> call = api.addMomment(
                title, place, moment_date, narrative, lat, lon, trip, body
        );

        call.enqueue(new Callback<EntryDetailsMoment>() {

            @Override
            public void onResponse(Call<EntryDetailsMoment> call, Response<EntryDetailsMoment> response) {
                if(response.code() == 200) {
                    progress_spinner.dismiss();

                    Toast.makeText(AddMommentActivity.this, "Trip Saved!", Toast.LENGTH_SHORT).show();
                    goListMoments();
                    getClearAll();
                } else  {
                    progress_spinner.dismiss();
                    Toast.makeText(AddMommentActivity.this, "Error Saving!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EntryDetailsMoment> call, Throwable t) {
                progress_spinner.dismiss();
                Toast.makeText(AddMommentActivity.this, "Error Saving!", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void getClearAll()
    {
        et_moment_title.setText("");
        tv_result_place.setText("");
        tv_result_moment_lat.setText("");
        tv_result_moment_lon.setText("");
        et_narrative.setText("");
        et_moment_date.setText("");
        iv_add_image_moment.setImageResource(R.drawable.logo);
    }

    protected void goListMoments(){
        Intent intent = new Intent(this, DetailTripActivity.class);
        intent.putExtra("_id", strTrip);
        intent.putExtra("title", strTitle);
        intent.putExtra("country", strCountry);
        intent.putExtra("city", strCity);
        intent.putExtra("date", strDate);
        intent.putExtra("description", strDescription);
        intent.putExtra("originalname", strFileOriginalName);
        intent.putExtra("userName", userName);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        } else if (requestCode == MARKER_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(getApplicationContext(), data);
            TextView myplace = (TextView)findViewById(R.id.tv_result_place);
            TextView latitud = (TextView)findViewById(R.id.tv_result_moment_lat);
            TextView longitud = (TextView)findViewById(R.id.tv_result_moment_lon);

            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                myplace.setText(addresses.get(0).getThoroughfare().toString());
                latitud.setText(String.valueOf(addresses.get(0).getLatitude()));
                longitud.setText(String.valueOf(addresses.get(0).getLongitude()));
                et_moment_date.findFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    iv_add_image_moment.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddMommentActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = imageBitmap = (Bitmap) data.getExtras().get("data");
            iv_add_image_moment.setImageBitmap(thumbnail);
        }
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
