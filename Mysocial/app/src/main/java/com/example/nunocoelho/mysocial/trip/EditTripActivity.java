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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.moment.AddMommentActivity;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditTripActivity extends AppCompatActivity {
    private Button btn_save, btn_delete, btn_addmomment;
    private ImageButton btn_back;
    private EditText et_title, et_country,et_city,et_description,et_date;
    private ImageView iv_phototrip;
    private String strTitle, strCountry, strCity, strDescription, strDate;

    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        btn_addmomment = (Button) findViewById(R.id.btn_addmomment);
        btn_delete     = (Button) findViewById(R.id.btn_delete);
        btn_back       = (ImageButton) findViewById(R.id.btn_back);
        btn_save       = (Button) findViewById(R.id.btn_save);
        iv_phototrip   = (ImageView) findViewById(R.id.iv_fototrip);
        et_title       = (EditText) findViewById(R.id.et_title);
        et_country     = (EditText)findViewById(R.id.et_country);
        et_city        = (EditText)findViewById(R.id.et_city);
        et_description = (EditText)findViewById(R.id.et_description);
        et_date        = (EditText) findViewById(R.id.et_date);

        strTitle       = et_title.getText().toString().trim();
        strCountry     = et_country.getText().toString().trim();
        strCity        = et_city.getText().toString().trim();
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
    protected void addTrip(){
        MysocialEndpoints api =
                MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
        if (!TextUtils.isEmpty(strTitle) && !TextUtils.isEmpty(strCountry) && !TextUtils.isEmpty(strCity)
                && !TextUtils.isEmpty(strDescription) && !TextUtils.isEmpty(strDate)) {
            api.addTrip(strTitle, strCountry, strCity, strDescription, strDate).enqueue(new Callback<Anwser>() {
                @Override
                public void onResponse(Call<Anwser> call, Response<Anwser> response) {
                    et_title.setText("");
                    et_country.setText("");
                }
                @Override
                public void onFailure(Call<Anwser> call, Throwable t) {
                }
            });
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
