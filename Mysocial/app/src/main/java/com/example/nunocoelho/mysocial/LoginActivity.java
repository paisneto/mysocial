package com.example.nunocoelho.mysocial;

import android.content.Intent;
import android.os.UserHandle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.nunocoelho.mysocial.adapters.UsersAdapter;
import com.example.nunocoelho.mysocial.login.Anwser;
import com.example.nunocoelho.mysocial.login.Details;
import com.example.nunocoelho.mysocial.trip.ListTripActivity;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class LoginActivity extends AppCompatActivity {
    //declaração das variaveis
    Realm realm = null;

    private Button btn_login;
    EditText tv_username, tv_password;
    public final static String EXTRA_MESSAGE = "com.example.nunocoelho.mysocial.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Realm.init(getApplicationContext());
        final Intent intent = getIntent();

        String logOut = intent.getStringExtra("kill_user");
        if (logOut != null && logOut.equals("yes"))
            logOutUser();
        else
            goValidatUserLoggedActivity();

        btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener(
        ) {
            @Override
            public void onClick(View v) {
                //verifica se os dados de login estão corretos
                //int newUser = 0, resulUserLogged = 0;
                //resulUserLogged = goValidatUserLoggedActivity();

                //if(resulUserLogged == 0) newUser = goSaveUserLogin();

                //if (newUser == 1) goListTripActivity();
                //goListTripActivity();

                goValidatUserLoggedActivity();
            }
        });

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
