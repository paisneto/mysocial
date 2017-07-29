package com.example.nunocoelho.mysocial.trip;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.nunocoelho.mysocial.moment.AddMommentActivity;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private String strTitle, strCountry, strCity, strLat, strLon, strDescription, strDate, strFilePath;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2, MARKER_PICKER_REQUEST = 3;
    private Bitmap imageBitmap;
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

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {

                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

                et_date.setText(sdf.format(myCalendar.getTime()));
            }

        };

        //gravar a viagem
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strTitle       = et_title.getText().toString().trim();
                strCountry     = tv_result_country.getText().toString().trim();
                strCity        = tv_result_city.getText().toString().trim();
                strLat         = tv_result_lat.getText().toString().trim();
                strLon         = tv_result_lon.getText().toString().trim();
                strDescription = et_description.getText().toString().trim();
                strDate        = et_date.getText().toString().trim();

                //vai executar metodo para verificar se os campos estão preenchidos
                if (executeValidation()) {
                    MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
                    if (!TextUtils.isEmpty(strTitle)
                            && !TextUtils.isEmpty(strCountry)
                            && !TextUtils.isEmpty(strCity)
                            && !TextUtils.isEmpty(strLat)
                            && !TextUtils.isEmpty(strLon)
                            && !TextUtils.isEmpty(strDescription)
                            && !TextUtils.isEmpty(strDate)) {
                        try {

                            String DATE_FORMAT_PATTERN = "dd-MM-yyyy";//"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
                            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_PATTERN);
                            Date myDate = formatter.parse(strDate);

                            String MY_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MY_DATE_FORMAT_PATTERN, Locale.getDefault());
                            Date myFinalDate = simpleDateFormat.parse(myDate.toString());

                            api.addTrip(strTitle, strCountry, strCity, strLat, strLon, strDescription, myFinalDate, "Ernesto Casanova", "ernestonet@msn.com").enqueue(new Callback<Anwser>() {
                                @Override
                                public void onResponse(Call<Anwser> call, Response<Anwser> response) {

                                    if (response.code()==200)
                                    {
                                        loadFiles();
                                        getClearAll();
                                        goListTrip();
                                    }
                                    else Toast.makeText(getApplicationContext(),"There was a problem saving!!", Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onFailure(Call<Anwser> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(),"There was a problem with upload!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
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
        //
        //botão para ir para a addmommentactivity
        /*btn_addmomment = (Button)findViewById(R.id.btn_addmomment);
        btn_addmomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAddMomment();
            }
        });*/

        //para inserir uma imagem da camera do telemovel ou da galeria
        iv_add_image_trip.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v) {
              showPictureDialog();
          }
        });

        //voltar para a ultima activity
        /*btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goListTrip();
            }
        });*/
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
    }

    //metodo para ir para a ListTripActivity
    protected void goListTrip(){
        Intent intent = new Intent(this, ListTripActivity.class);
        startActivity(intent);
    }

    //metodo para ir para a addmommentactivity
    protected void goAddMomment(){
        Intent intent = new Intent(this, AddMommentActivity.class);
        startActivity(intent);
    }

    //inserir imagem da galeria ou tirando uma foto
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
            //String toastMsg = String.format("Place: %s", place.getName());
            //Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            TextView country = (TextView)findViewById(R.id.tv_result_country);
            TextView city = (TextView)findViewById(R.id.tv_result_city);
            TextView latitud = (TextView)findViewById(R.id.tv_result_lat);
            TextView longitud = (TextView)findViewById(R.id.tv_result_lon);

            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                Log.d("geo",addresses.get(0).getCountryName());
                Log.d("geo",addresses.get(0).getFeatureName());
                country.setText(addresses.get(0).getCountryName());
                //city.setText(addresses.get(0).getThoroughfare());
                city.setText(addresses.get(0).getAdminArea());
                latitud.setText(String.valueOf(addresses.get(0).getLatitude()));
                longitud.setText(String.valueOf(addresses.get(0).getLongitude()));
                et_date.findFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("geo",Double.toString(place.getLatLng().latitude));
            Log.d("geo",Double.toString(place.getLatLng().longitude));
            //latitudeEditText.setText(Double.toString(place.getLatLng().latitude));
            //longitudeEditText.setText(Double.toString(place.getLatLng().longitude));
        } else if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                   // String path = saveImage(bitmap);
                    //Toast.makeText(AddTripActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();

                    iv_add_image_trip.setImageBitmap(bitmap);
                    //loadFiles();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddTripActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = imageBitmap = (Bitmap) data.getExtras().get("data");
            iv_add_image_trip.setImageBitmap(thumbnail);
            //saveImage(thumbnail);
            //loadFiles();
            //Toast.makeText(AddTripActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());
            strFilePath = f.getAbsolutePath();
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }


    private void loadFiles()
    {

        String path = Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY;
        OutputStream fOut = null;
        File fileU = new File(path, "temp.jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        try {
            fOut = new FileOutputStream(fileU);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
        try {
            fOut.flush(); // Not really required
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close(); // do not forget to close the stream
        } catch (IOException e) {
            e.printStackTrace();
        }

        // use the FileUtils to get the actual file by uri
        File file = new File(String.valueOf(fileU));

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"),file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("temp.jpg", file.getName(), requestFile);

        MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
        RequestBody _id = RequestBody.create(okhttp3.MultipartBody.FORM, "593526ab66a1cd0004b50d1e");
        RequestBody user = RequestBody.create(okhttp3.MultipartBody.FORM, "593526ab66a1cd0004b50d1e");

        Call<Anwser> call = api.uploadTripFiles(
                _id, user, body
        );
        call.enqueue(new Callback<Anwser>() {

            @Override
            public void onResponse(Call<Anwser> call, Response<Anwser> response) {
                if(response.code() == 200) {

                    //spinner.setVisibility(View.GONE);
                    Toast.makeText(AddTripActivity.this, "Image Saved in server!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Anwser> call, Throwable t) {
                t.printStackTrace();
                //spinner.setVisibility(View.GONE);
                Toast.makeText(AddTripActivity.this, "Erro Saving!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //metodo que excuta as validações
    protected boolean executeValidation(){
        if (TextUtils.isEmpty(strTitle) || TextUtils.isEmpty(strCountry) || TextUtils.isEmpty(strCity)
                || TextUtils.isEmpty(strDescription) || TextUtils.isEmpty(strDate)) {

            /*final AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
            pictureDialog.setTitle("Preencha os Campos em Falta.");
            pictureDialog.show();*/

            Toast.makeText(AddTripActivity.this,
                    "Missing fields!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
