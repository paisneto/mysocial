package com.example.nunocoelho.mysocial.mysocialapi;

import com.example.nunocoelho.mysocial.login.Details;
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
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Nuno Coelho on 21/05/2017.
 */

public interface MysocialEndpoints {

    //pesquisar viagem por id
    final static String BASE_API_URL = "https://secret-plains-68464.herokuapp.com/api/v1/";
    final static String BASE_API_URL_V2 = "https://secret-plains-68464.herokuapp.com/api/v2/";
    final static String BASE_URL = "https://secret-plains-68464.herokuapp.com/";
    //final static String MEDIA_URL = "https://s3.eu-west-2.amazonaws.com/meimysocial/upload/media/";
    final static String MEDIA_URL = "https://meimysocial.blob.core.windows.net/upload/";

    //get user by email
    @GET("users/{email}")
    Call<Details> getUser(
            @Path("email") String email
        );

    //update user email
    @FormUrlEncoded
    @PUT("users/{email}")
    Call<Details>updateUser(
            @Path("email") String email,
            @Field("oauthID") String oauthID,
            @Field("token") String token,
            @Field("name") String name,
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("username") String username,
            @Field("gender") String gender,
            @Field("isauthenticated") Boolean isauthenticated
    );


    // insert user
    @FormUrlEncoded
    @POST("users/")
    Call<Details> insertUser(
           @Field("email") String email,
           @Field("oauthID") String oauthID,
           @Field("token") String token,
           @Field("name") String name,
           @Field("first_name") String first_name,
           @Field("last_name") String last_name,
           @Field("username") String username,
           @Field("gender") String gender,
           @Field("isauthenticated") Boolean isauthenticated

    );


    //
    @GET("trips/?limit=500&page=1&number=100&sort=-created")//&title={title}")
    Call<Anwser> getAllTrips(
            @Query("postedByEmail") String postedByEmail
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
    /*@FormUrlEncoded
    @POST("trips/")
    Call<Anwser> addTrip(
            @Field("title") String title,
            @Field("country") String country,
            @Field("city") String city,
            @Field("lat") String lat,
            @Field("lon") String lon,
            @Field("description") String description,
            @Field("date") String date,// Date
            @Field("postedByName") String name,
            @Field("postedByEmail") String email//,
           // @Part MultipartBody.Part file

    );*/

    //inserir uma viagem
    @Multipart
    @POST("trips/")
    Call<Anwser> addTrip(
            @Part("title") RequestBody title,
            @Part("country") RequestBody country,
            @Part("city") RequestBody city,
            @Part("lat") RequestBody lat,
            @Part("lon") RequestBody lon,
            @Part("description") RequestBody description,
            @Part("date") RequestBody date,
            @Part("username") RequestBody postedByName,
            @Part("useremail") RequestBody postedByEmail,
            @Part MultipartBody.Part file
    );

    //partilhar uma viagem
    @FormUrlEncoded
    @PUT("trips/{id}/share/")
    Call<Anwser> shareTrip(
            @Path("id") String id,
            @Field("isprivate") Boolean isprivate
    );

    @Multipart
    //@POST("trips/{_id}/users/{user}/files/")
    @POST("trips/users/files/")
    Call<Anwser> uploadTripFiles(@Part("_id") RequestBody _id, @Part("user") RequestBody user, @Part MultipartBody.Part file);


    /*Moments*/
    @GET("moments/?limit=500&page=1&number=20&sort=-created")
    Call<AnwserMoment> getMomentsTrip(
            @Query("trip") String trip
    );

    @FormUrlEncoded
    @PUT("moments/{id}/classifications/")
    Call<AnwserMoment> getClassifMoments(
            @Path("id") String id,
            @Field("value") Number value,
            @Field("postedByEmail") String postedByEmail,
            @Field("postedByName") String postedByName
    );

    //inserir um momento
    //@FormUrlEncoded

    @Multipart
    @POST("moments/")
    Call<EntryDetailsMoment> addMomment(
            @Part("title") RequestBody title,
            @Part("place") RequestBody place,
            @Part("moment_date") RequestBody moment_date,
            @Part("narrative") RequestBody narrative,
            @Part("lat") RequestBody lat,
            @Part("lon") RequestBody lon,
            @Part("trip") RequestBody trip,
            @Part MultipartBody.Part file
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

    final static Retrofit retrofit_2 = new Retrofit.Builder()
            .baseUrl(BASE_API_URL_V2)
            .addConverterFactory(GsonConverterFactory.create())
            .build();



}