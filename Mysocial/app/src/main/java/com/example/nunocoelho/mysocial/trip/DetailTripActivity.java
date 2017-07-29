package com.example.nunocoelho.mysocial.trip;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nunocoelho.mysocial.LoginActivity;
import com.example.nunocoelho.mysocial.R;
import com.example.nunocoelho.mysocial.adapters.MomentsAdapter;
import com.example.nunocoelho.mysocial.helpers.Utils;
import com.example.nunocoelho.mysocial.moment.AddMommentActivity;
import com.example.nunocoelho.mysocial.moment.AnwserMoment;
import com.example.nunocoelho.mysocial.moment.DetailMomentActivity;
import com.example.nunocoelho.mysocial.moment.EntryDetailsMoment;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTripActivity extends AppCompatActivity {

    private static MomentsAdapter adapter;
    private Button btn_back;
    private String _id_trip, strFilePath;
    private FloatingActionButton btn_addmomment;
    private ImageView iv_image_selected, img_trip_default;
    private ListView lv_momments;
    private TextView tv_title, tv_address, tv_date, tv_description;
    private ArrayList<EntryDetailsMoment> entryDetailsMomentList;
    private static final String IMAGE_DIRECTORY = "/MEIMySocialUploads";
    private int GALLERY = 1, CAMERA = 2;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_trip);
        final Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Detail Trip");

        //addListenerOnButton();

        btn_addmomment = (FloatingActionButton) findViewById(R.id.btn_addmomment);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_address = (TextView)findViewById(R.id.tv_address);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_description = (TextView) findViewById(R.id.tv_description);
        img_trip_default = (ImageView) findViewById(R.id.img_trip_default);

        _id_trip = intent.getStringExtra("_id");
        tv_title.setText(intent.getStringExtra("title"));
        try {
            String city = (String.valueOf(intent.getStringExtra("city") == null ? "" : ", " + String.valueOf(intent.getStringExtra("city"))));
            String adress = String.valueOf(intent.getStringExtra("country"))  + city;
            tv_address.setText(adress);

            String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            String reformattedStr = new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat(DATE_FORMAT_PATTERN).parse(intent.getStringExtra("date")));
            tv_date.setText(reformattedStr);    // format output

           // String adress = intent.getStringExtra("country").concat(", ").concat(intent.getStringExtra("city"));
            setImageGallery(intent.getStringExtra("originalname"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //tv_date.setText(intent.getStringExtra("date"));
        tv_description.setText(intent.getStringExtra("description"));

        lv_momments    = (ListView) findViewById(R.id.lv_momments);

        entryDetailsMomentList = new ArrayList<>();

        adapter = new MomentsAdapter(entryDetailsMomentList, getApplicationContext());

        lv_momments.setAdapter(adapter);

        showMoments();

        lv_momments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                goDetailMoment(i);
            }
        });

        //para editar a trip
        btn_addmomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAddMoment(intent);
            }
        });

        //para voltar a ListTripActivity

       /* btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goListTrip();
            }
        });*/
    }

    private void setImageGallery(String imgOriginalName) {

        if (imgOriginalName.isEmpty()) img_trip_default.setImageResource(R.drawable.logo);
        else new Utils.DownloadImageTask((ImageView) img_trip_default.findViewById(R.id.img_trip_default)).execute(MysocialEndpoints.MEDIA_URL + imgOriginalName);
        //List<Images> images
        // add optional image into list
        /*List<Images> new_images = new ArrayList<>();
        final ArrayList<String> new_images_str = new ArrayList<>();
        new_images.add(new Images(place.place_id, place.image));
        new_images.addAll(images);
        for (Images img : new_images) {
            new_images_str.add(img.name);
        }

        RecyclerView galleryRecycler = (RecyclerView) findViewById(R.id.galleryRecycler);
        galleryRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        AdapterImageList adapter = new AdapterImageList(new_images);
        galleryRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterImageList.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String viewModel, int pos) {
                Intent i = new Intent(ActivityPlaceDetail.this, ActivityFullScreenImage.class);
                i.putExtra(ActivityFullScreenImage.EXTRA_POS, pos);
                i.putStringArrayListExtra(ActivityFullScreenImage.EXTRA_IMGS, new_images_str);
                startActivity(i);
            }
        });*/
    }

    //metodo para voltar para a ListTripActivity
    protected void goListTrip(){
        Intent intent = new Intent(this, ListTripActivity.class);
        startActivity(intent);
    }
    //metodo para chamar a activity para editar a trip
    protected void goEditTrip(){
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
                    //Toast.makeText(DetailTripActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    iv_image_selected.setImageBitmap(bitmap);
                    InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                    imageBitmap = BitmapFactory.decodeStream(inputStream);
                    loadFiles();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(DetailTripActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            iv_image_selected.setImageBitmap(thumbnail);
            String res = saveImage(thumbnail);
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            if(res != "") loadFiles();
            else Toast.makeText(DetailTripActivity.this, "Image NOT Saved!", Toast.LENGTH_SHORT).show();
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
        if (strFilePath == null) {
            Toast.makeText(DetailTripActivity.this, "strFilePath empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = UUID.randomUUID().toString() + "_" + Calendar.getInstance()
                .getTimeInMillis() + ".jpg";

        String path = Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY;
        OutputStream fOut = null;
        File fileU = new File(path, fileName); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
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
        MultipartBody.Part body = MultipartBody.Part.createFormData(fileName, file.getName(), requestFile);
        RequestBody _id = RequestBody.create(okhttp3.MultipartBody.FORM, _id_trip);
        RequestBody user = RequestBody.create(okhttp3.MultipartBody.FORM, "593526ab66a1cd0004b50d1e");

        MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
        Call<Anwser> call = api.uploadTripFiles(
                _id, user, body
        );
        call.enqueue(new Callback<Anwser>() {

            @Override
            public void onResponse(Call<Anwser> call, Response<Anwser> response) {
                if(response.code() == 200) {

                    //spinner.setVisibility(View.GONE);
                    Toast.makeText(DetailTripActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Anwser> call, Throwable t) {
                t.printStackTrace();
                //spinner.setVisibility(View.GONE);
                Toast.makeText(DetailTripActivity.this, "Error Saving!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //metodo para ir para a DetailTripActivity
    protected void goDetailMoment(int i) {
        try {
            Intent intent = new Intent(this, DetailMomentActivity.class);
            intent.putExtra("_id", adapter.getItem(i).getId());
            intent.putExtra("title", adapter.getItem(i).getTitle());
            intent.putExtra("place", adapter.getItem(i).getPlace());
            intent.putExtra("moment_date", adapter.getItem(i).getMomentDate());
            intent.putExtra("narrative", adapter.getItem(i).getNarrative());
            intent.putExtra("trip", adapter.getItem(i).getTrip());
            intent.putExtra("lat", adapter.getItem(i).getLat());
            intent.putExtra("lon", adapter.getItem(i).getLon());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //metodo para chamar a activity para editar a trip
    protected void goAddMoment(Intent i){
        Intent intent = new Intent(this, AddMommentActivity.class);
        intent.putExtra("_id", i.getStringExtra("_id"));
        startActivity(intent);
    }

    //metodo para carregar os moments de uma viagem
    protected void showMoments(){
        MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
        Call<AnwserMoment> call = api.getMomentsTrip(
                _id_trip
        );
        call.enqueue(new Callback<AnwserMoment>() {

            @Override
            public void onResponse(Call<AnwserMoment> call, Response<AnwserMoment> response) {
                if(response.code() == 200) {
                    AnwserMoment resp = response.body();
                    for(EntryDetailsMoment e : resp.getEntradas()) {
                        entryDetailsMomentList.add(e);
                    }
                    adapter.notifyDataSetChanged();
                    adapter.notifyDataSetInvalidated();
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "Else DetailTripActivity toast!";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<AnwserMoment> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    //metodo para partilhar uma viagem
    protected void shareTrip(){
        MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
        Call<Anwser> call = api.shareTrip(
                _id_trip, false
        );
        call.enqueue(new Callback<Anwser>() {

            @Override
            public void onResponse(Call<Anwser> call, Response<Anwser> response) {
                if(response.code() == 200) {
                    Toast.makeText(DetailTripActivity.this, "Shared to public!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailTripActivity.this, "Error Sharing!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Anwser> call, Throwable t) {
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
            case R.id.action_favorite:
                try {
                    shareTrip();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.

                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
