package com.example.myapplication5;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class registerActivity extends AppCompatActivity {
    private TextView psw,tel,pswsure,text;
    private Button sub;
    private static String cpsw,ctel,cpswsure,ctest;
    private Button btn,change,changeSure;
    private String codeReturn,codeReturn1;
    private boolean flag = false,flagSure = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        change = (Button)findViewById(R.id.change);
        psw = (TextView)findViewById(R.id.psw);
        pswsure = (TextView)findViewById(R.id.pswsure);
        tel = (TextView)findViewById(R.id.tel);
        sub = (Button)findViewById(R.id.submit);
        text=(TextView)findViewById(R.id.test);
        btn = (Button)findViewById(R.id.code);
        changeSure = (Button)findViewById(R.id.changeSure) ;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctel = tel.getText().toString().trim();
                getcode(ctel);
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag){
                    psw.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    flag = false;
                }else{
                    psw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    flag = true;
                }
            }
        });

        changeSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagSure){
                    pswsure.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    flagSure = false;
                }else{
                    pswsure.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    flagSure = true;
                }




            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cpsw = psw.getText().toString().trim();
                cpswsure = pswsure.getText().toString().trim();
                ctel = tel.getText().toString().trim();
                ctest = text.getText().toString().trim();
                System.out.println(ctest+"++++++++++++++++++++++++++++++++++++++++++++++++++++");

                 if(TextUtils.isEmpty(cpsw)){
                    Toast.makeText(registerActivity.this,"密码不能为空",Toast.LENGTH_LONG).show();
                    return;
                }

                else if(TextUtils.isEmpty(cpswsure)){
                    Toast.makeText(registerActivity.this,"请输入确认密码",Toast.LENGTH_LONG).show();
                    return;
                }

                else if(TextUtils.isEmpty(ctel)){
                    Toast.makeText(registerActivity.this,"请输入电话号码",Toast.LENGTH_LONG).show();
                    return;
                }
                else if (!cpsw.equals(cpswsure)){
                    Toast.makeText(registerActivity.this,"两次输入的密码不同，请重新输入",Toast.LENGTH_LONG).show();
                    return;

                }
                else if(ctel==cpsw){
                    Toast.makeText(registerActivity.this,"用户名不能和密码相同，请重新输入",Toast.LENGTH_LONG).show();
                    return;
                }
                else if(ctel.length()!=11){
                    Toast.makeText(registerActivity.this,"请输入正确的号码",Toast.LENGTH_LONG).show();
                    return;

                }else if(ctest.length()==0){
                     Toast.makeText(registerActivity.this,"验证码不能为空",Toast.LENGTH_SHORT).show();
                 }

                else  {
                    HashMap<String,String> mapuse=new HashMap<>();
                    mapuse.put("username",ctel);
                    mapuse.put("code",ctest);
                    mapuse.put("mobile",ctel);
                    mapuse.put("password",cpsw);

                    Gson gson = new Gson();
                    String userdata = gson.toJson(mapuse);
                    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
                    RequestBody requestBody = FormBody.create(MediaType.parse("application/json;charset=utf-8"),userdata);
                    Request request = new Request.Builder().post(requestBody).url("https://api.ohaiyo.vip/users/").build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(registerActivity.this,"连接失败",Toast.LENGTH_LONG).show();
                            return;

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Looper.prepare();//增加部分
                            String StringTemp = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(StringTemp);
                                JSONArray results = jsonObject.getJSONArray("code");
                                codeReturn1=results.getString(0);
                                if(ctest==null)
                                    Toast.makeText(registerActivity.this,"请正确输入验证码",Toast.LENGTH_LONG).show();
                                else if(codeReturn1.equals("验证码过期")||codeReturn1.equals("验证码错误"))
                                Toast.makeText(registerActivity.this,codeReturn1,Toast.LENGTH_LONG).show();
                                else {
                                    Toast.makeText(registerActivity.this,"注册成功",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(registerActivity.this,LoadActivity.class);
                                startActivity(intent);
                                finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            call.cancel();
                            Looper.loop();
                            //System.out.println(response.body().string());


                        }
                    });

                /**Toast.makeText(registerActivity.this,"注册成功",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(registerActivity.this,LoadActivity.class);
                startActivity(intent);
                finish();*/}


            }
        });

    }
    public void getcode(String cctel){
        if(TextUtils.isEmpty(cctel)){
            Toast.makeText(registerActivity.this,"请输入电话号码",Toast.LENGTH_LONG).show();
            return;
        }else if(cctel.length()!=11) {
            Toast.makeText(registerActivity.this,"请输入正确的号码",Toast.LENGTH_LONG).show();
            return;

        }
        HashMap<String,String> map=new HashMap<>();

        map.put("mobile",cctel);
        Gson gson = new Gson();
        String data = gson.toJson(map);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json;charset=utf-8"),data);
        Request request = new Request.Builder().post(requestBody).url("https://api.ohaiyo.vip/codes/").build();
        okHttpClient.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(registerActivity.this,"连接失败",Toast.LENGTH_LONG).show();
                return;

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Looper.prepare();//增加部分
                String StringTemp = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(StringTemp);
                    JSONArray results = jsonObject.getJSONArray("mobile");//得到键为results的JSONArray
                    //JSONObject obj = (JSONObject)results.opt(0);
                    //codeReturn = obj.getString("space_num");  //获得城市名
                    codeReturn=results.getString(0);
                    Toast.makeText(registerActivity.this,codeReturn,Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                call.cancel();
                Looper.loop();



            }
        });


    }
}
