package com.yy.sign.ui;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.yy.sign.HttpService;
import com.yy.sign.R;
import com.yy.sign.model.SignRequest;
import com.yy.sign.model.SignResponse;
import com.yy.sign.utils.Utils;

import java.util.Date;
import java.util.Random;

import okhttp3.OkHttpClient;
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
    private String chooseDate;
    private HttpService httpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.sendBtn = (Button) findViewById(R.id.sendBtn);
        this.pickTimeBtn = (Button) findViewById(R.id.pickTimeBtn);
        this.dateTv = (TextView) findViewById(R.id.dateTv);

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
                        Log.d("TAG", String.valueOf(response.isSuccessful()));
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
                .baseUrl("http://xzapi.acme.com.cn")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.httpService = retrofit.create(HttpService.class);
    }


}