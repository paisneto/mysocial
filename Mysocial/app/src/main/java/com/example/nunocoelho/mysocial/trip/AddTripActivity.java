package com.example.nunocoelho.mysocial.trip;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nunocoelho.mysocial.LoginActivity;
import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.moment.AddMommentActivity;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddTripActivity extends AppCompatActivity {

    private Button btn_save, btn_addmomment;
    private Button btn_camera, btn_back;
    private EditText et_title, et_country,et_city,et_description,et_date;
    private ImageView iv_phototrip;
    private String strTitle, strCountry, strCity, strDescription, strDate, strFilePath;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    private Bitmap imageBitmap;

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
        iv_phototrip   = (ImageView) findViewById(R.id.iv_fototrip);

        et_title       = (EditText) findViewById(R.id.et_title);
        et_country     = (EditText)findViewById(R.id.et_country);
        et_city        = (EditText)findViewById(R.id.et_city);
        et_description = (EditText)findViewById(R.id.et_description);
        et_date        = (EditText) findViewById(R.id.et_date);

        //gravar a viagem
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strTitle       = et_title.getText().toString().trim();
                strCountry     = et_country.getText().toString().trim();
                strCity        = et_city.getText().toString().trim();
                strDescription = et_description.getText().toString().trim();
                strDate        = et_date.getText().toString().trim();

                //vai executar metodo para verificar se os campos estão preenchidos
                if (executeValidation()) {
                    MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
                    if (!TextUtils.isEmpty(strTitle)
                            && !TextUtils.isEmpty(strCountry)
                            && !TextUtils.isEmpty(strCity)
                            && !TextUtils.isEmpty(strDescription)
                            && !TextUtils.isEmpty(strDate)) {
                        api.addTrip(strTitle, strCountry, strCity, strDescription, strDate).enqueue(new Callback<Anwser>() {
                            @Override
                            public void onResponse(Call<Anwser> call, Response<Anwser> response) {
                               // et_title.setText("");
                               // et_country.setText("");
                                goListTrip();
                            }
                            @Override
                            public void onFailure(Call<Anwser> call, Throwable t) {
                            }
                        });
                    }
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
        iv_phototrip.setOnClickListener(new View.OnClickListener(){
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
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(AddTripActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    iv_phototrip.setImageBitmap(bitmap);
                    loadFiles();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddTripActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            iv_phototrip.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            loadFiles();
            Toast.makeText(AddTripActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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
