package com.example.nunocoelho.mysocial.mysocialapi;
import com.example.nunocoelho.mysocial.moment.AnwserMoment;
import com.example.nunocoelho.mysocial.moment.EntryDetailsMoment;
import com.example.nunocoelho.mysocial.trip.Anwser;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Nuno Coelho on 21/05/2017.
 */

public interface MysocialEndpoints {

    //pesquisar viagem por id
    final static String BASE_API_URL = "https://secret-plains-68464.herokuapp.com/api/v1/";
    final static String BASE_URL = "https://secret-plains-68464.herokuapp.com/";
    //final static String MEDIA_URL = "https://s3.eu-west-2.amazonaws.com/meimysocial/upload/media/";
    final static String MEDIA_URL = "https://meimysocial.blob.core.windows.net/upload/";

    //
    @GET("trips/?limit=500&page=1&number=100&sort=-created")//&title={title}")
    Call<Anwser> getAllTrips(
            //@Path("limit") String limit,
            //@Path("page") String page,
            //@Path("number") String number,
            //@Path("sort") String sort,
           // @Path("title") String title//search field name
    );

    @GET("trips/{id}/")
    Call<Anwser> getTripPhrase(
            @Path("id") String trip
            /*,
            @Query("docs") String docs*/
    );

    @GET("trips/{id}/")
    Call<Anwser> getTripbyId(
            @Path("id") String id
    );

    //inserir uma viagem
    @FormUrlEncoded
    @POST("trips/")
    Call<Anwser> addTrip(
            @Field("title") String title,
            @Field("country") String country,
            @Field("city") String city,
            @Field("description") String description,
            @Field("date") String date
    );

    @Multipart
    //@POST("trips/{_id}/users/{user}/files/")
    @POST("trips/users/files/")
    Call<Anwser> uploadTripFiles(@Part("_id") RequestBody _id, @Part("user") RequestBody user, @Part MultipartBody.Part file);


    /*Moments*/
    //@GET("moments/trips/{trip}/moment/")
    @GET("moments/?limit=500&page=1&number=20&sort=-created")
    Call<AnwserMoment> getMomentsTrip(
            //@Path("trip") String trip
    );

    /*
    * "title": "Portugal",
  "place": "Ipca",
  "moment_date": "2017-11-30T00:00:00.000Z",
  "narrative": "Go to Barcelos",
  "lat": "41,5317",
  "lon": "-8,6179",
  "img": "buffer",
  "created": "2017-11-30T00:00:00
    * */

    //inserir um momento
    @FormUrlEncoded
    @POST("moments/moment/")
    Call<EntryDetailsMoment> addMomment(
            @Field("title") String title,
            @Field("place") String place,
            @Field("moment_date") String moment_date,
            @Field("narrative") String narrative,
            @Field("lat") String lat,
            @Field("lon") String lon,
            @Field("trip") String trip
    );

    @FormUrlEncoded
    @POST("countries/country/")
    Call<Anwser> postCountryNew(
            @Field("name") String name
    );

    final static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}