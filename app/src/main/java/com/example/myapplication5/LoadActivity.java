package com.example.myapplication5;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.SDKInitializer;
import com.example.myapplication5.Manage.UserManage;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoadActivity extends AppCompatActivity {
    private Button btDenglu,btZhuce,btChange;
    private TextView userName,psw;
    private static String Cusername,Cupsw;
    //public String token=null;
    private String logReturn;
    public static String token ;
    private boolean flag = false;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_denglu);
        btDenglu = (Button)findViewById(R.id.denglu);
        btZhuce = (Button)findViewById(R.id.zhuce);
        btChange = (Button)findViewById(R.id.change);
        userName = (TextView)findViewById(R.id.username);
        psw = (TextView)findViewById(R.id.psw);
        btChange.setOnClickListener(new View.OnClickListener() {
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

        btDenglu.setOnClickListener(v -> {
            Cusername = userName.getText().toString().trim();
            Cupsw = psw.getText().toString().trim();
            if(TextUtils.isEmpty(Cusername)){
                Toast.makeText(LoadActivity.this,"请输入用户名",Toast.LENGTH_LONG).show();
                return;
            }
            else if(TextUtils.isEmpty(Cupsw)){
                Toast.makeText(LoadActivity.this,"请输入密码",Toast.LENGTH_LONG).show();
                return;
            }
            else {
                okhttpSend(Cusername,Cupsw);


                }
        });
        btZhuce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(LoadActivity.this,registerActivity.class);
                startActivity(intent3);

            }
        });


    }
    private void okhttpSend(String username,String password){
        HashMap<String,String> mapuse=new HashMap<>();
        mapuse.put("username",username);
        mapuse.put("password",password);


        Gson gson = new Gson();
        String userdata = gson.toJson(mapuse);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json;charset=utf-8"),userdata);
        Request request = new Request.Builder().post(requestBody).url("https://api.ohaiyo.vip/login/").build();
        //Request request = Request.Builder.get(body).url("https://api.ohaiyo.vip/login/").header("token",s) .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    // 重新提交验证   在这里最好限制提交次数
                   okHttpClient.newCall(call.request()).enqueue(this);
                }
                if (e instanceof ConnectException) {
                    Log.e("frost_connection",e.getMessage());
                }



            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Looper.prepare();//增加部分
                String StringTemp = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(StringTemp);
                    String s = jsonObject.toString();

                    if(s.charAt(2)=='t'){

                        token =  jsonObject.getString("token");
                        UserManage.getInstance().saveUserInfo(LoadActivity.this, Cusername, Cupsw,token);
                        Toast.makeText(LoadActivity.this,token,Toast.LENGTH_LONG).show();
                        Intent intent2 = new Intent(LoadActivity.this, GeoCoderDemo.class);
                        startActivity(intent2);
                        call.cancel();
                        finish();
                    }
                    //JSONArray results = jsonObject.getJSONArray("non_field_errors");
                    //logReturn=results.getString(0);
                    else{

                        JSONArray results = jsonObject.getJSONArray("non_field_errors");
                        logReturn=results.getString(0);

                        Toast.makeText(LoadActivity.this,logReturn,Toast.LENGTH_LONG).show();
                        call.cancel();
                        Looper.loop();
                        }






                    //finish();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                call.cancel();
                Looper.loop();
                //System.out.println(response.body().string());


            }
        });
        //判断账户是否合法
    }
}
