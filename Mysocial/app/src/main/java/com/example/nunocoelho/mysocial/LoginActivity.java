package com.example.nunocoelho.mysocial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

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
    private String userName = "", userEmail = "";

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

//                        TextView nameTextView = (TextView)view.findViewById(R.id.nameTextView);
//                        TextView emailTextView = (TextView)view.findViewById(R.id.emailTextView);
//                        TextView createdTextView = (TextView)view.findViewById(R.id.createdTextView);
//                        ImageView avatarImageView = (ImageView)view.findViewById(R.id.avatarImageView);
//
//                        avatarImageView.setImageResource(R.mipmap.travel_with_me_color);
//                        nameTextView.setText("New User");
//                        emailTextView.setText("New Email");
//                        createdTextView.setText("Created on: ");
//
//
//                        mListenerLogout.onFragmentLogout();
                    }

                }
            }
        };

        accessTokenTracker.startTracking();



        Details user = realm.where(Details.class).findFirst();
        if(user != null){
            loadProfile(user);
        }

      //  return view;
    }


    private void loadProfile(Details user){

        userName = user.getName();
        userEmail = user.getEmail();

       // Toast.makeText(LoginActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
//       TextView tv_name = (TextView)view.findViewById(R.id.tv_name);
//       TextView tv_email = (TextView)view.findViewById(R.id.tv_email);
//       ImageView iv_photo = (ImageView)view.findViewById(R.id.iv_photo);
//
        //Picasso.with(fragmentContext).load(user.getPhotoUri()).into(iv_photo);
//        tv_name.setText(user.getFirstName()+" "+user.getLastName());
//        tv_email.setText(user.getEmail());


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
                    saveToRealm(user);
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
                    saveToRealm(user);
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
        Intent intent = new Intent(this, ListTripActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "LoginActivity");
        intent.putExtra("userName", userName);
        intent.putExtra("userEmail", userEmail);
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
