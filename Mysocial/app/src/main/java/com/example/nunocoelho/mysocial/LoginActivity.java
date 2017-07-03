package com.example.nunocoelho.mysocial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.nunocoelho.mysocial.login.Details;
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


public class LoginActivity extends AppCompatActivity {
    //declaração das variaveis
    Realm realm = null;

    CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;

    private Button btn_login;
    EditText tv_username, tv_password;
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

        accessTokenTracker = new AccessTokenTracker(){
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken){
                if (oldAccessToken!=null){ Log.d("Facebook",oldAccessToken.getToken()); }
                else { Log.d("Facebook",currentAccessToken.getToken()); }
            }
        };

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken!=null){
            //esta authenticado
            Log.d("Facebook", "facebook:accesstoken:"+ accessToken.getToken());

            goValidatUserLoggedActivity();
            //guardar no realm e mostrar a foto do utilizador etc
        }

        /*if (logOut != null && logOut.equals("yes"))
            logOutUser();
        else
            goValidatUserLoggedActivity();*/

        callbackManager = CallbackManager.Factory.create();
        btn_login = (Button)findViewById(R.id.btn_login);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_birthday");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            Details user = new Details();

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook",loginResult.getAccessToken().getUserId());
                Log.d("Facebook",loginResult.getAccessToken().getToken());
                loadFacebookInfo(loginResult, user);


            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                //TODO: se estiver a null tenho de limpar do realm o user forcando o logout
                if (currentAccessToken==null)
                {
                    logOutUser();
                }
            }
        };

        accessTokenTracker.startTracking();

        btn_login.setOnClickListener(new View.OnClickListener(
        ) {
            @Override
            public void onClick(View v) {
                goValidatUserLoggedActivity();
            }
        });

    }

    protected void loadFacebookInfo(final LoginResult loginResult, final Details user)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback(){

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response){
                        try{
                            Log.d("Facebook", response.getJSONObject().getString("email"));
                            Log.d("Facebook", response.getJSONObject().getString("first_name"));
                            Log.d("Facebook", response.getJSONObject().getString("last_name"));
                            goValidatUserLoggedActivity();
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                }
        );
        Bundle param = new Bundle();
        param.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(param);
        request.executeAsync();
    }

    protected void getProfilePicture(final LoginResult loginResult, final Details user)
    {
        Bundle param = new Bundle();
        param.putBoolean("redirect", false);

        new GraphRequest(
                loginResult.getAccessToken(), "/"+loginResult.getAccessToken().getUserId()+"/picture", param, HttpMethod.GET,
                new GraphRequest.Callback(){
                    public void onCompleted(GraphResponse response){
                        try{
                            Log.d("Facebook", response.getJSONObject().getJSONObject("data").getString("url"));

                            //TODO: Aqui enviar todos dos dados para a API para guardar lá a informação



                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if(realm==null) {
            realm = Realm.getDefaultInstance();
        }
        RealmQuery<Details> query2 = realm.where(Details.class);
        RealmResults<Details> datasource = query2.findAll();

        UsersAdapter adapter = new UsersAdapter(this,datasource);

        id_list.setAdapter(adapter);
    }*/

    //metodo chamado para mudar para a ListTripActivity
    protected void goListTripActivity(){
        Intent intent = new Intent(this, ListTripActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "LoginActivity");
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

    //metodo chamado para validar user
    protected void goValidatUserLoggedActivity(){

        realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        Details user = realm.where(Details.class).findFirst();

        //RealmQuery<Anwser> query = realm.where(Anwser.class);
        //RealmResults<Anwser> results = query.findAll();

        if(user == null) {
            Details newUser = new Details();

            tv_username = (EditText) findViewById(R.id.tv_username);
            tv_password = (EditText) findViewById(R.id.tv_password);

            final String username = tv_username.getText().toString().trim();
            final String password = tv_password.getText().toString().trim();

            if (username != "" && password != "")
            {
                newUser.username = username;
                newUser.password = password;
                realm.copyToRealm(newUser);
                realm.commitTransaction();
                goListTripActivity();
            }
            //senao mostrar mensagem faz login.
        }
        else {

            goListTripActivity();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //realm.close();
    }
}
