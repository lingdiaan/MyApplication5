package com.example.myapplication5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

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

public class firstActivity extends Activity {
    private static final int GO_HOME = 0;//去主页
    private static final int GO_LOGIN = 1;//去登录页
    private Button del;
    private String logReturn;
    public static String token ;
    /**
     * 跳转判断
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME://去主页
//                    Intent intent = new Intent(firstActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
                    break;
                case GO_LOGIN://去登录页
//                    Intent intent2 = new Intent(firstActivity.this, LoadActivity.class);
//                    startActivity(intent2);
//                    finish();
//                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_first);

        if (UserManage.getInstance().hasUserInfo(this))//自动登录判断，SharePrefences中有数据，则跳转到主页，没数据则跳转到登录页
        {  String UserName = UserManage.getInstance().getUserNamef(this);
           String PassWord = UserManage.getInstance().getPassWordf(this);

            HashMap<String,String> mapuse=new HashMap<>();
            mapuse.put("username",UserName);
            mapuse.put("password",PassWord);


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
                            Toast.makeText(firstActivity.this,token,Toast.LENGTH_LONG).show();
                            LoadActivity.token = token;
//                            mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
                        }
                        //JSONArray results = jsonObject.getJSONArray("non_field_errors");
                        //logReturn=results.getString(0);
                        else{
                            JSONArray results = jsonObject.getJSONArray("non_field_errors");
                            logReturn=results.getString(0);
                            Intent intent2 = new Intent(firstActivity.this, LoadActivity.class);
                            startActivity(intent2);
                            finish();}






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



            try{
                Thread.sleep(2000);
            }catch (Exception e){};
            Intent intent = new Intent(firstActivity.this, GeoCoderDemo.class);
            startActivity(intent);
            finish();


//            mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
        } else {
            try{
                Thread.sleep(2000);
            }catch (Exception e){};
//            mHandler.sendEmptyMessageAtTime(GO_LOGIN, 2000);
            Intent intent2 = new Intent(firstActivity.this, LoadActivity.class);
            startActivity(intent2);
            finish();

        }

    }
}
