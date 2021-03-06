package com.example.nunocoelho.mysocial.trip;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.helpers.Utils;
import com.example.nunocoelho.mysocial.moment.AddMommentActivity;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class EditTripActivity extends AppCompatActivity {
    private Button btn_save, btn_delete, btn_addmomment;
    private Button btn_back;
    private EditText et_title, et_country,et_city,et_description,et_date;
    private TextView tv_result_country, tv_result_city, tv_result_lat, tv_result_lon;
    private ImageView iv_phototrip;
    private String strTitle, strCountry, strCity, strLat, strLon, strDescription, strDate;

    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);


        if(!Utils.isNetworkConnected(EditTripActivity.this)) { Toast.makeText(EditTripActivity.this, "Error - No Network Connection!", Toast.LENGTH_SHORT).show(); finish(); }


        btn_addmomment = (Button) findViewById(R.id.btn_addmomment);
        btn_delete     = (Button) findViewById(R.id.btn_delete);
        //btn_back       = (Button) findViewById(R.id.btn_back);
        btn_save       = (Button) findViewById(R.id.btn_save);
        iv_phototrip   = (ImageView) findViewById(R.id.iv_fototrip);
        et_title       = (EditText) findViewById(R.id.et_title);

        tv_result_country     = (TextView)findViewById(R.id.tv_result_country);
        tv_result_city        = (TextView)findViewById(R.id.tv_result_city);

        tv_result_lat     = (TextView)findViewById(R.id.tv_result_lat);
        tv_result_lon        = (TextView)findViewById(R.id.tv_result_lon);

        et_description = (EditText)findViewById(R.id.et_description);
        et_date        = (EditText) findViewById(R.id.et_date);

        strTitle       = et_title.getText().toString().trim();
        strCountry     = tv_result_country.getText().toString().trim();
        strCity        = tv_result_city.getText().toString().trim();
        strLat         = tv_result_lat.getText().toString().trim();
        strLon         = tv_result_lon.getText().toString().trim();
        strDescription = et_description.getText().toString().trim();
        strDate        = et_date.getText().toString().trim();

        //botao para adicionar o momento
        btn_addmomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addiciona a viagem
                addTrip();

                //depois de adiciona a viagem, vai chamar a AddMommentActivity
                goMomment();
            }
        });

        //gravar a viagem
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //vai executar metodo para verificar se os campos estão preenchidos
                if (executeValidation()) {
                    //adiciona a viagem
                    addTrip();
                }
            }
        });

        //para inserir uma imagem da camera do telemovel ou da galeria
        iv_phototrip.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        //voltar para a ultima activity
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goDetailTrip();
            }
        });

    }
    //metodo para ir para a AddMommentActivity
    protected void goMomment(){
        Intent intent = new Intent(this, AddMommentActivity.class);
        startActivity(intent);
    }

    //metodo para introduzir os dados da viagem
    protected void addTrip(){//edit trip
        MysocialEndpoints api =
                MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
        if (!TextUtils.isEmpty(strTitle) && !TextUtils.isEmpty(strCountry) && !TextUtils.isEmpty(strCity)
                && !TextUtils.isEmpty(strDescription) && !TextUtils.isEmpty(strDate)) {
            try {


        /*MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
        RequestBody _id = RequestBody.create(okhttp3.MultipartBody.FORM, trip_id);
        RequestBody user = RequestBody.create(okhttp3.MultipartBody.FORM, "593526ab66a1cd0004b50d1e");

        Call<Anwser> call = api.uploadTripFiles(
                _id, user, body
        );*/



        /*Call<Anwser> call = api.uploadTripFiles(
                _id, country, body
        );*/


/*                String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
                SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_PATTERN);
                Date myDate = formatter.parse(strDate);
                api.addTrip(strTitle, strCountry, strCity, strLat, strLon, strDescription, myDate.toString(), "Ernesto Casanova", "ernestonet@msn.com").enqueue(new Callback<Anwser>() {
                    @Override
                    public void onResponse(Call<Anwser> call, Response<Anwser> response) {

                        if (response.code()==200)
                        {
                            Toast.makeText(getApplicationContext(),"Saved!!", Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(getApplicationContext(),"There was a problem saving!!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<Anwser> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"There was a problem with upload!", Toast.LENGTH_SHORT).show();
                    }
                });*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //metodo para ir para a ListTripActivity
    protected void goDetailTrip(){
        Intent intent = new Intent(this, EditTripActivity.class);
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
                    Toast.makeText(EditTripActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    iv_phototrip.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EditTripActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            iv_phototrip.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(EditTripActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    //metodo que excuta as validações
    protected boolean executeValidation(){
        if (TextUtils.isEmpty(strTitle) || TextUtils.isEmpty(strCountry) || TextUtils.isEmpty(strCity)
                || TextUtils.isEmpty(strDescription) || TextUtils.isEmpty(strDate)) {

            final AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
            pictureDialog.setTitle("Preencha os Campos em Falta.");
            pictureDialog.show();
            return false;
        }
        return true;
    }
}
