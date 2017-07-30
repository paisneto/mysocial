package com.example.nunocoelho.mysocial;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.nunocoelho.mysocial.helpers.Utils;
import com.example.nunocoelho.mysocial.login.Details;
import com.example.nunocoelho.mysocial.mysocialapi.MysocialEndpoints;
import com.example.nunocoelho.mysocial.trip.ListTripActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    //declaração das variaveis
    Realm realm = null;


    CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private View view;
    private LoginButton LoginButton;
    EditText tv_username, tv_password;
    private String userName = "", userEmail = "", photoUrl ="";

    public final static String EXTRA_MESSAGE = "com.example.nunocoelho.mysocial.MESSAGE";



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Realm.init(getApplicationContext());
        final Intent intent = getIntent();

        String logOut = intent.getStringExtra("kill_user");

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

        };

        if(!Utils.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile","user_birthday");



        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            Details newUser = new Details();

            @Override
            public void onSuccess(LoginResult loginResult) {
                newUser.setOauthID(loginResult.getAccessToken().getUserId());
                newUser.setToken(loginResult.getAccessToken().getToken());
                getFacebookData(loginResult, newUser);
            }

            @Override
            public void onCancel() {
                //LoginManager.getInstance().logOut();
            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        realm = Realm.getDefaultInstance();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null){
                    realm.beginTransaction();
                    Details user = realm.where(Details.class).findFirst();
                    if(user != null){
                        user.deleteFromRealm();
                        realm.commitTransaction();

                    }

                }
            }
        };

        accessTokenTracker.startTracking();

        Details user = realm.where(Details.class).findFirst();
        if(user != null){
            loadProfile(user);
        }

    }


    private void loadProfile(Details user){


        userName = user.getName();
        userEmail = user.getEmail();


        goListTripActivity();

    }



    private void getFacebookData(final LoginResult loginResult, final Details user)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            userName = response.getJSONObject().getString("first_name")+" "+response.getJSONObject().getString("last_name");
                            userEmail = response.getJSONObject().getString("email");
                            user.setEmail(response.getJSONObject().getString("email"));
                            user.setName(userName);
                            user.setFirstName(response.getJSONObject().getString("first_name"));
                            user.setLastName(response.getJSONObject().getString("last_name"));
                            user.setGender(response.getJSONObject().getString("gender"));
                            user.setUsername(response.getJSONObject().getString("email"));
                            //user.setPhotoUri(response.getJSONObject().get("picture").toString());

                            getFacebookPicture(loginResult, user);

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void getFacebookPicture(final LoginResult loginResult, final Details user){
        Bundle params = new Bundle();
        params.putBoolean("redirect", false);

        /* make the API call */
        new GraphRequest(
                loginResult.getAccessToken(),
                "/"+loginResult.getAccessToken().getUserId()+"/picture",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {

                            user.setPhotoUri(response.getJSONObject().getJSONObject("data").getString("url"));
                            photoUrl = response.getJSONObject().getJSONObject("data").getString("url");
                            getLogin(user);

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    public void getLogin(final Details user){

        final String _email = user.getEmail().toString();

        MysocialEndpoints api = MysocialEndpoints.retrofit_2.create(MysocialEndpoints.class);
        Call< Details> call = api.getUser(_email);

        call.enqueue(new Callback<Details>() {
            @Override
            public void onResponse(Call<Details> call, Response<Details> response) {
                if(response.code() == 200 && response.body() != null){
                    updateUser(user);
                } else{

                    insertUser(user);
                }
            }

            @Override
            public void onFailure(Call<Details> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void updateUser(Details user){
        final String email = userEmail = user.getEmail().toString();
        final String oauthID = user.getOauthID().toString();
        final String token = user.getToken().toString();
        final String name = userName = user.getName().toString();
        final String first_name = user.getFirstName().toString();
        final String last_name = user.getLastName().toString();
        final String username = user.getUsername().toString();
        final String gender = user.getGender().toString();

        MysocialEndpoints api = MysocialEndpoints.retrofit_2.create(MysocialEndpoints.class);
        Call< Details> call = api.updateUser(email,oauthID,token,name,first_name,last_name,username,gender,true);

        call.enqueue(new Callback<Details>() {
            @Override
            public void onResponse(Call<Details> call, Response<Details> response){

                if(response.code()==200){
                    Details user = response.body();

                    loadProfile(user);
                    goListTripActivity();

                }
            }
            @Override
            public void onFailure(Call<Details> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    private void insertUser(Details user){

        MysocialEndpoints api_v2 = MysocialEndpoints.retrofit_2.create(MysocialEndpoints.class);

        Call< Details> call_post = api_v2.insertUser(
                  user.getEmail().toString()
                , user.getOauthID().toString()
                , user.getToken().toString()
                , user.getName().toString()
                , user.getFirstName().toString()
                , user.getLastName().toString()
                , user.getEmail().toString()
                , user.getGender().toLowerCase()
                ,  true);

        call_post.enqueue(new Callback<Details>() {
            @Override
            public void onResponse(Call<Details> call, Response<Details> response) {
                if(response.code() == 200){
                    Details user = response.body();

                    loadProfile(user);
                    goListTripActivity();

                }

            }

            @Override
            public void onFailure(Call<Details> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    //metodo chamado para mudar para a ListTripActivity
    protected void goListTripActivity(){

     //  photoUrl  = "https://meimysocial.blob.core.windows.net/upload/ec8f14e8-ce10-414b-833c-6b4105e88ac0.jpg";

        Intent intent = new Intent(this, ListTripActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "LoginActivity");
        intent.putExtra("userName", userName);
        intent.putExtra("userEmail", userEmail);
        intent.putExtra("photoUrl", photoUrl);
        startActivity(intent);
    }
    //metodo chamado para mudar para a RecoverPassActivity

    //metodo chamado para mudar para a RegisterUserActivity


    protected void logOutUser()
    {
        realm = Realm.getDefaultInstance();

        if(!realm.isInTransaction()) realm.beginTransaction();

        Details user = realm.where(Details.class).findFirst();

        if(user != null) {
            //user.deleteFromRealm();
            //realm.commitTransaction();
            //realm.close();
            //realm.beginTransaction();
            user.deleteFromRealm();
            realm.commitTransaction();
        }

        /*realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Details> result = realm.where(Details.class).findAll();
                result.deleteAllFromRealm();
            }
        });*/
    }

    //add


    //metodo chamado para validar user
//    protected void goValidatUserLoggedActivity(){
//
//        realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//       // Details user = realm.where(Details.class).findFirst();
//
//        //get data from facebook
//
//
//        //RealmQuery<Anwser> query = realm.where(Anwser.class);
//        //RealmResults<Anwser> results = query.findAll();
//        MysocialEndpoints api = MysocialEndpoints.retrofit.create(MysocialEndpoints.class);
//        Call< Details> call = api.getUser(user.getEmail().toString());
//
//        call.enqueue(new Callback<Details>() {
//            @Override
//            public void onResponse(Call<Details> call, Response<Details> response) {
//                if(response.code() == 200){
//                    Details user = response.body();
//                    saveToRealm(user);
//                    //  setProfile(user);
//                    //  mListenerLogin.onFragmentLogin();
//
//                    updateUser(user);
//
//
//                    goListTripActivity();
//                } else{
//                    Details newUser = new Details();
//                    insertUser(newUser);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<Details> call, Throwable t) {
//
//
//
//            }
//        });
//
//
//
//
//        if(user == null) {
//            Details newUser = new Details();
//
//            tv_username = (EditText) findViewById(R.id.tv_username);
//            tv_password = (EditText) findViewById(R.id.tv_password);
//
//            final String username = tv_username.getText().toString().trim();
//            final String password = tv_password.getText().toString().trim();
//
//            if (username != "" && password != "")
//            {
//                newUser.setUsername(username);
//                newUser.setPassword(password);
//                realm.copyToRealm(newUser);
//                realm.commitTransaction();
//                goListTripActivity();
//            }
//            //senao mostrar mensagem faz login.
//        }
//        else {
//
//            goListTripActivity();
//        }
//    }



    //add
    private void saveToRealm(Details user){
        realm.beginTransaction();
        realm.copyToRealm(user);
        realm.commitTransaction();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        //realm.close();
    }
}
