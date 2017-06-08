package com.example.nunocoelho.mysocial.helpers;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ecasanova on 08/06/2017.
 */

public class UploadHelper {
    /*public static Call<String> multiPartSetUp(Bitmap bitmap, String userId, String storyId) {
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File fileU = new File(path, "temp.jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        try {
            fOut = new FileOutputStream(fileU);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
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

        // add another part within the multipart request
        RequestBody userIdBody = RequestBody.create(okhttp3.MultipartBody.FORM, userId);
        RequestBody storyIdBody = RequestBody.create(okhttp3.MultipartBody.FORM, storyId);

        return  MysocialEndpoints.retrofit.uploadMedia(userIdBody, storyIdBody, body);
    }*/


}