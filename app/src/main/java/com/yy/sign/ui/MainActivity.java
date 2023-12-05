package com.yy.sign.ui;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.yy.sign.HttpService;
import com.yy.sign.R;
import com.yy.sign.model.SignRequest;
import com.yy.sign.model.SignResponse;
import com.yy.sign.utils.Utils;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends Activity {

    private Button sendBtn;
    private Button pickTimeBtn;
    private TextView dateTv;
    private TextView resultTv;
    private LinearLayout resultLayout;
    private String chooseDate;
    private HttpService httpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.sendBtn = (Button) findViewById(R.id.sendBtn);
        this.pickTimeBtn = (Button) findViewById(R.id.pickTimeBtn);
        this.dateTv = (TextView) findViewById(R.id.dateTv);
        this.resultTv = (TextView) findViewById(R.id.resultTv);
        this.resultLayout = (LinearLayout) findViewById(R.id.resultLayout);

        initRetrofit();

        this.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignRequest signRequest = new SignRequest();
                signRequest.capture_img = "";
                signRequest.capture_time = chooseDate;
                signRequest.dev_sno = "CN4G232060001";
                signRequest.workNo = "";

                Call<SignResponse> signResponseCall = httpService.signTest(signRequest);

                signResponseCall.enqueue(new Callback<SignResponse>() {

                    @Override
                    public void onResponse(Call<SignResponse> call, Response<SignResponse> response) {
                        String res = "";
                        if (response.body() != null) {
                            resultLayout.setVisibility(View.VISIBLE);
                                res =response.body().data?"成功:":"失败:";
                            resultTv.setText(res+"  "+  new Gson().toJson(response.body()));
                        }else{
                            resultLayout.setVisibility(View.VISIBLE);
                            resultTv.setText("请重试");

                        }
                    }


                    @Override
                    public void onFailure(Call<SignResponse> call, Throwable t) {
                        Log.d("TAG", t.toString());
                    }
                });

            }
        });

        this.pickTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String yMd = Utils.getDateStr(new Date(), "yyyy-MM-dd");
                String hM = Utils.getDateStr(new Date(), "HH:mm");

                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hour = "" + hourOfDay;
                        String minutes = "" + minute;
                        if (hourOfDay < 10) {
                            hour = "0" + hourOfDay;
                        }
                        if (minute < 10) {
                            minutes = "0" + minute;
                        }

                        int i = new Random().nextInt(40) + 10;
                        MainActivity.this.dateTv.setText("选择的日期:\n" + yMd + " " + hour + ":" + minutes + ":" + i);
                        chooseDate = Utils.date2TimeStamp(yMd + " " + hour + ":" + minutes + ":" + i, null);

                        Log.d("TAG", chooseDate);
                    }
                }, Integer.parseInt(hM.split(":")[0]), Integer.parseInt(hM.split(":")[1]), true).show();
            }
        });
    }

    private void initRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);


        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl("https://xzapi.acme.com.cn")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.httpService = retrofit.create(HttpService.class);
    }


}