package com.example.myapplication5;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class messageActivity extends AppCompatActivity {
    private TextView num,name,tel,email,gender,birth;
    private static String Rname,Rtel,Rnum,Rgender,Rbirth,Remail;
    private ImageView image;
    private Handler handler;
    private Button submit;
    private String birthday;
    private String[] string;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);
        name = (TextView)findViewById(R.id.name);
        tel = (TextView)findViewById(R.id.tel);
        num = (TextView)findViewById(R.id.num);
        birth = (TextView)findViewById(R.id.birth);
        gender = (TextView)findViewById(R.id.gender);
        email = (TextView)findViewById(R.id.email);
        image = (ImageView)findViewById(R.id.images);
        submit = (Button)findViewById(R.id.submit);

         handler = new Handler();
        getMessage();
       //change();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("name",Rname);
                bundle.putString("birth",birthday);
                bundle.putString("gender",Rgender);
                bundle.putString("email",Remail);
                bundle.putString("tel",Rtel);
                bundle.putString("num",Rnum);

                Intent intent = new Intent(messageActivity.this,updataActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });




    }


   /** private void change(){
        name.setText(Rname);
        tel.setText(Rtel);
        num.setText(Rnum);
        Toast.makeText(messageActivity.this,Rname+Rtel+Rnum,Toast.LENGTH_LONG).show();

    }*/


    private void getMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建一个OkHttp实例
                    Request request = new Request.Builder().url("https://api.ohaiyo.vip/users/1/").addHeader("Authorization","jwt"+" "+LoadActivity.token) .build();



                    //Request request = new Request.Builder().url("https://api.seniverse.com/v3/weather/now.json?key=SrvH71t8JeTOXNLJP&location=beijing&language=zh-Hans&unit=c").build();//创建Request对象发起请求,记得替换成你自己的key
                    Response response = client.newCall(request).execute();//创建call对象并调用execute获取返回的数据
                    String responseData = response.body().string();
                    parseJSONWithJSONObject(responseData);//解析SSON数据
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void parseJSONWithJSONObject(String jsonData) {//用JSONObect解析JSON数据
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            //JSONObject jsonObject = new JSONObject(response.body().toString());
            Rname = jsonObject.getString("name");
            Rtel = jsonObject.getString("mobile");
            Rnum = jsonObject.getString("car");
            Rgender = jsonObject.getString("gender");
            Remail = jsonObject.getString("email");
            Rbirth = jsonObject.getString("birthday");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        handler.post(runnableUi);
    }
    Runnable runnableUi=new Runnable(){
        @Override
        public void run() {
//更新界面
            string = Rbirth.split("-");
            birthday = string[0]+"-"+string[1]+"-"+string[2];
            name.setText(Rname);
            tel.setText(Rtel);
            num.setText(Rnum);
            birth.setText(birthday);
            gender.setText(Rgender);
            email.setText(Remail);
            Toast.makeText(messageActivity.this,Rname+Rtel+Rnum,Toast.LENGTH_LONG).show();
        }

    };}


