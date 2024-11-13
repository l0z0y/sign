package com.yy.sign.ui;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yy.sign.HttpService;
import com.yy.sign.R;
import com.yy.sign.model.Config;
import com.yy.sign.model.Member;
import com.yy.sign.model.SignRequest;
import com.yy.sign.model.SignResponse;
import com.yy.sign.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends Activity {

    private EditText configEt;
    private Button chooseConfigBtn;
    private Button sendBtn;
    private Button pickTimeBtn;
    private ImageView showImage;
    private TextView dateTv;
    private String chooseDate;
    private String chooseImg;
    private HttpService httpService;
    private List<Config> configList = new ArrayList<>();
    private Spinner configSp;
    private ArrayAdapter configArrayAdapter;
    private List<String> configNameList = new ArrayList<>();
    private String workNo;
    private String chooseKind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
        initRetrofit();
        bindClick();


    }

    private void bindClick() {
        this.chooseConfigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String configJson = Utils.getJson(MainActivity.this, "config.json");
                String memberJson = Utils.getJson(MainActivity.this, "member.json");
                List<Config> configs = (List<Config>) gson.fromJson(configJson, new TypeToken<List<Config>>() {
                }.getType());
                List<Member> memberList = (List<Member>) gson.fromJson(memberJson, new TypeToken<List<Member>>() {
                }.getType());

                String name = configEt.getText().toString();
                if (name.length() == 0) {
                    Toast.makeText(MainActivity.this, "请先输入配置号", Toast.LENGTH_SHORT).show();
                    configList.clear();
                    configNameList.clear();
                    workNo = null;
                    if (configArrayAdapter != null) {
                        configArrayAdapter.clear();

                    }
                    return;
                }

                configList.clear();
                configNameList.clear();
                workNo = null;
                configs.forEach(config -> {
                    if (config.id.equals(name)) {
                        configList.add(config);
                        configNameList.add(config.name);
                    }
                });
                memberList.forEach(item -> {
                    if (item.name.equals(name)) {
                        workNo = item.number;
                    }

                });
                loadConfigItem();
            }
        });

        this.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignRequest signRequest = new SignRequest();
                signRequest.capture_img = chooseImg;
                signRequest.capture_time = chooseDate;
                if(chooseDate == null){
                    Toast.makeText(MainActivity.this, "请选择时间", Toast.LENGTH_SHORT).show();

                    return;
                }
                if (chooseKind.equals("1")){
                    signRequest.dev_sno = "CN4G232060001";

                }else {
                    signRequest.dev_sno = "CJDE232060610";

                }
                signRequest.workNo = workNo;
                Call<SignResponse> signResponseCall = httpService.signTest(signRequest);

                signResponseCall.enqueue(new Callback<SignResponse>() {

                    @Override
                    public void onResponse(Call<SignResponse> call, Response<SignResponse> response) {
                        String res = "";
                        if (response.body() != null) {

                            res = response.body().data ? "成功:" : "失败:";
                            Toast.makeText(MainActivity.this, res + new Gson().toJson(response.body()), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_LONG).show();
                        }

                    }


                    @Override
                    public void onFailure(Call<SignResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this,     t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void loadConfigItem() {
        //参数1.上下文对象 参数2.列表项的样式,Android为我们提供的资源样式为：android.R.layout.simple_spinner_item
        //参数3.定义的字符串数组
        configArrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item, configNameList);
        //设置适配器列表框下拉时的列表样式
        configArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将适配器与下拉列表框关联起来
        configSp.setAdapter(configArrayAdapter);
        configSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = Utils.stringtoBitmap(configList.get(index).img);
                        showImage.setImageBitmap(bitmap);
                        showImage.setVisibility(View.VISIBLE);
                        chooseImg = configList.get(index).img;
                        chooseKind = configList.get(index).kind;
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                showImage.setImageBitmap(null);
                showImage.setVisibility(View.GONE);
            }
        });
    }

    private void bindView() {
        this.configEt = (EditText) findViewById(R.id.configEt);
        this.chooseConfigBtn = (Button) findViewById(R.id.chooseConfigBtn);
        this.sendBtn = (Button) findViewById(R.id.sendBtn);
        this.pickTimeBtn = (Button) findViewById(R.id.pickTimeBtn);
        this.showImage = (ImageView) findViewById(R.id.showImg);
        this.dateTv = (TextView) findViewById(R.id.dateTv);
        this.configSp = (Spinner) findViewById(R.id.configSp);
    }

    private void initConfig() {

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