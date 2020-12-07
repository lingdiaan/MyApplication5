package com.example.myapplication5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication5.Manage.UserManage;


public class myActivity extends AppCompatActivity {
    private Button massage,exit,order;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        massage = (Button)findViewById(R.id.message);
        exit = (Button)findViewById(R.id.exit);
        order = (Button)findViewById(R.id.order) ;
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManage.getInstance().deleteUserInfo(myActivity.this);
                Intent intent= new Intent(myActivity.this,LoadActivity.class);
                startActivity(intent);
                GeoCoderDemo.geoCoderDemo.finish();
                finish();


            }
        });
        massage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoadActivity.token!=" "){
                Intent intent = new Intent(myActivity.this,messageActivity.class);
                startActivity(intent);}
                else {
                    Intent intent = new Intent(myActivity.this,LoadActivity.class);
                    System.out.println(LoadActivity.token);
                    startActivity(intent);
                }
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myActivity.this,orderActivity.class);
                startActivity(intent);
            }
        });
    }
}
