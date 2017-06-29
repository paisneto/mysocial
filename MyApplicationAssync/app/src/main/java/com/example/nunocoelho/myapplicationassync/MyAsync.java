package com.example.nunocoelho.myapplicationassync;

import android.os.AsyncTask;
import android.widget.TextView;



import static com.example.nunocoelho.myapplicationassync.R.id.tv_res;


/**
 * Created by Nuno Coelho on 12/06/2017.
 */

public class MyAsync extends AsyncTask<Integer, String, String>{

    TextView tv_res;

    public MyAsync (TextView tv_res){
        this.tv_res = tv_res;
    }

       @Override
    protected Integer doInBackground(Integer... params) {

           Integer sec = params[0];
           int conta, aux;
           conta = 60-sec;




        return conta;
    }

    @Override
    protected void onPostExecute(Integer result){
        String aux;

        //super.onPostExecute(result);
        aux = result.toString();
        tv_res.setText(result);

    }

}
