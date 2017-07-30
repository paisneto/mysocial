package com.example.nunocoelho.mysocial.trip;

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
import com.example.nunocoelho.mysocial.moment.AddMommentActivity;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;
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


public class AddTripActivity extends AppCompatActivity {

    private Button btn_save, btn_addmomment;
    private Button btn_camera, btn_back;
    private EditText et_title,et_description,et_date;
    private TextView tv_result_country, tv_result_city, tv_result_lat, tv_result_lon;
    private ImageView iv_add_image_trip;
    private String strTitle, strCountry, strCity, strLat, strLon, strDescription, strDate, strFilePath, userName, userEmail;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2, MARKER_PICKER_REQUEST = 3;
    private Bitmap imageBitmap;
    private String tripDate;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add Trip");

        btn_addmomment = (Button) findViewById(R.id.btn_addmomment);
        //btn_back       = (Button) findViewById(R.id.btn_back);
        btn_save       = (Button) findViewById(R.id.btn_save);
        //btn_camera     = (ImageButton) findViewById(R.id.btn_camera);
        iv_add_image_trip   = (ImageView) findViewById(R.id.iv_add_image_trip);

        et_title       = (EditText) findViewById(R.id.et_title);
        tv_result_country     = (TextView)findViewById(R.id.tv_result_country);
        tv_result_city        = (TextView)findViewById(R.id.tv_result_city);

        tv_result_lat     = (TextView)findViewById(R.id.tv_result_lat);
        tv_result_lon        = (TextView)findViewById(R.id.tv_result_lon);
        et_description = (EditText)findViewById(R.id.et_description);
        et_date        = (EditText) findViewById(R.id.et_date);

        final Intent intent = getIntent();
        userName             = intent.getStringExtra("userName");
        userEmail             = intent.getStringExtra("userEmail");

        myCalendar = Calendar.getInstance();
        et_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddTripActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        date = new DatePickerDialog.OnDateSetListener() {
            String MY_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            String DATE_FORMAT_PATTERN = "EEE MMM dd HH:mm:ss z yyyy";
            StringBuilder Date;
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);

                Date = new StringBuilder().append(year).append("-").append(month + 1).append("-").append(day).append(" ");
                updateTextView();
            }

            private void updateTextView() {
                tripDate = Date.toString();
                String myViewFormat = "dd MMMM yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myViewFormat, Locale.ENGLISH);

                et_date.setText(sdf.format(myCalendar.getTime()));
            }

        };
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strTitle       = et_title.getText().toString().trim();
                strCountry     = tv_result_country.getText().toString().trim();
                strCity        = tv_result_city.getText().toString().trim();
                strLat         = tv_result_lat.getText().toString().trim();
                strLon         = tv_result_lon.getText().toString().trim();
                strDescription = et_description.getText().toString().trim();

                if (!TextUtils.isEmpty(strTitle)
                            && !TextUtils.isEmpty(strCountry)
                            && !TextUtils.isEmpty(strCity)
                            && !TextUtils.isEmpty(strLat)
                            && !TextUtils.isEmpty(strLon)
                            && !TextUtils.isEmpty(strDescription)) {
                        try {
                            loadFiles(strTitle, strCountry, strCity, strLat, strLon, strDescription, tripDate.toString(), userName, userEmail);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                } else Toast.makeText(AddTripActivity.this,
                    "Missing fields!", Toast.LENGTH_SHORT).show();
            }
            });

        Button markerPickerButton = (Button) findViewById(R.id.btn_marker_picker);
        markerPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(AddTripActivity.this), MARKER_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(getApplicationContext(),"There was a problem with your Google Services", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        iv_add_image_trip.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v) {
              showPictureDialog();
          }
        });
    }

    private void getClearAll()
    {
        et_title.setText("");
        tv_result_country.setText("");
        tv_result_city.setText("");
        tv_result_lat.setText("");
        tv_result_lon.setText("");
        et_description.setText("");
        et_date.setText("");
        iv_add_image_trip.setImageResource(R.drawable.logo);
    }

    //metodo para ir para a ListTripActivity
    protected void goListTrip(){
        Intent intent = new Intent(this, ListTripActivity.class);
        intent.putExtra("userName", userName);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
    }

    protected void goAddMomment(){
        Intent intent = new Intent(this, AddMommentActivity.class);
        startActivity(intent);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        } else if (requestCode == MARKER_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(getApplicationContext(), data);
            TextView country = (TextView)findViewById(R.id.tv_result_country);
            TextView city = (TextView)findViewById(R.id.tv_result_city);
            TextView latitud = (TextView)findViewById(R.id.tv_result_lat);
            TextView longitud = (TextView)findViewById(R.id.tv_result_lon);

            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                country.setText(addresses.get(0).getCountryName());
                city.setText(addresses.get(0).getAdminArea());
                latitud.setText(String.valueOf(addresses.get(0).getLatitude()));
                longitud.setText(String.valueOf(addresses.get(0).getLongitude()));
                et_date.findFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    iv_add_image_trip.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddTripActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = imageBitmap = (Bitmap) data.getExtras().get("data");
            iv_add_image_trip.setImageBitmap(thumbnail);
        }
    }

    private void loadFiles(String _strTitle, String _strCountry, String _strCity, String _strLat, String _strLon, String _strDescription, String _myDate, String _userName, String _userEmail)
    {
        final Dialog progress_spinner = Utils.LoadingSpinner(AddTripActivity.this);
        progress_spinner.show();

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
        RequestBody title = RequestBody.create(okhttp3.MultipartBody.FORM, _strTitle);
        RequestBody country = RequestBody.create(okhttp3.MultipartBody.FORM, _strCountry);
        RequestBody city = RequestBody.create(okhttp3.MultipartBody.FORM, _strCity);
        RequestBody lat = RequestBody.create(okhttp3.MultipartBody.FORM, _strLat);
        RequestBody lon = RequestBody.create(okhttp3.MultipartBody.FORM, _strLon);
        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, _strDescription);
        RequestBody date = RequestBody.create(okhttp3.MultipartBody.FORM, _myDate);
        RequestBody postedByName = RequestBody.create(okhttp3.MultipartBody.FORM, _userName);
        RequestBody postedByEmail = RequestBody.create(okhttp3.MultipartBody.FORM, _userEmail);

        Call<Anwser> call = api.addTrip(
                title, country, city, lat, lon, description, date, postedByName, postedByEmail, body
        );


        call.enqueue(new Callback<Anwser>() {

            @Override
            public void onResponse(Call<Anwser> call, Response<Anwser> response) {
                if(response.code() == 200) {
                    progress_spinner.dismiss();
                    Toast.makeText(AddTripActivity.this, "Trip Saved!", Toast.LENGTH_SHORT).show();
                    goListTrip();
                    getClearAll();
                    //onBackPressed();
                } else  {
                    progress_spinner.dismiss();
                    Toast.makeText(AddTripActivity.this, "Error Saving!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Anwser> call, Throwable t) {
                progress_spinner.dismiss();
                Toast.makeText(AddTripActivity.this, "Error Saving!", Toast.LENGTH_SHORT).show();
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
