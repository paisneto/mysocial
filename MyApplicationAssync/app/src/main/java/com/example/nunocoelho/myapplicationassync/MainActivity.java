package com.example.nunocoelho.myapplicationassync;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView tv_horainicial, tv_res;
    private Button btn_inicio;
    String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_horainicial = (TextView) findViewById(R.id.tv_horainicial);
        tv_res = (TextView) findViewById(R.id.tv_res);
        btn_inicio = (Button) findViewById(R.id.btn_inicio);

        btn_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
//                String date = sdf.format(new Date());
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Date hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
                String dataFormatada = sdf.format(hora);


                String sec = "25";



                tv_horainicial.setText(dataFormatada);
                new MyAsync(tv_res).execute(sec);



            }
        });
    }
}
