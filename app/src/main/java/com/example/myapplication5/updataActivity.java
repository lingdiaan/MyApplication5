package com.example.myapplication5;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class updataActivity extends AppCompatActivity {
    private EditText car,name,mobil,email,gender;
    private TextView birth;
    private static String Rname,Rmobil,Rcar,Rgender,Rbirth,Remail;
    private Button submit;
    private AlertDialog dialog;
    private int year,month,day;
    private Button sure;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updata);
        Bundle bundle = getIntent().getExtras();
        initView();

        Rname = bundle.getString("name");
        name.setText(Rname);
        Rmobil = bundle.getString("tel");
        mobil.setText(Rmobil);
        Rcar = bundle.getString("num");
        car.setText(Rcar);
        Rgender = bundle.getString("gender");
        gender.setText(Rgender);
        Rbirth = bundle.getString("birth");
        birth.setText(Rbirth);
        Remail = bundle.getString("email");
        email.setText(Remail);

        birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog = new AlertDialog.Builder(updataActivity.this).create();
//                dialog.show();
//                dialog.getWindow().setContentView(R.layout.data_dialog);
//
//                Calendar calendar = Calendar.getInstance();
//                year=calendar.get(Calendar.YEAR);
//                month=calendar.get(Calendar.MONTH);
//                day=calendar.get(Calendar.DAY_OF_MONTH);
//                System.out.println(year+"----"+month+"----"+day);
//
//                dialog.getWindow().findViewById(R.id.data_sure).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        birth.setText(year+"-"+month+"-"+day);
//                        dialog.dismiss();
//                    }
//                });
//
//
//
//
//                birth.setText(String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(day));

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        updataActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                Log.d(TAG, "onDateSet: date: " + year + "/" + month + "/" + dayOfMonth);
                int rm = month + 1;
                birth.setText(year + "-" + rm + "-" + dayOfMonth);

            }
        };




        submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String carnum = car.getText().toString().trim();
                if(carnum.length()<7||ChineseNum(carnum)>1||ChineseNum(carnum)==0||AbcNum(carnum)==0){
                    Toast.makeText(updataActivity.this,"请输入正确格式的车牌号",Toast.LENGTH_SHORT).show();

                }
                HashMap<String,String> map = new HashMap<>();
                map.put("id","1");
                map.put("name",name.getText().toString().trim());
                map.put("mobile",mobil.getText().toString().trim());
                map.put("car",car.getText().toString().trim());
                map.put("gender",gender.getText().toString().trim());
                if(birth.getText().toString()!=null){
                     map.put("birthday",birth.getText().toString().trim());
                }
                else map.put("birthday",null);

                map.put("email",email.getText().toString().trim());

                Gson gson = new Gson();
                String data = gson.toJson(map);

                OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

                RequestBody requestBody = FormBody.create(MediaType.parse("application/json;charset=utf-8"),data);
                System.out.println(birth+"----------------------------------------");
                Request request = new Request.Builder().patch(requestBody).url("https://api.ohaiyo.vip/users/1/").addHeader("Authorization","JWT"+" "+LoadActivity.token).build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
//                        Toast.makeText(updataActivity.this,"网络错误，更新失败",Toast.LENGTH_LONG);

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();

                        System.out.println("更新成果"+"----------"+responseData);
                        Intent intent = new Intent(updataActivity.this,messageActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });
            }
        });



    }
    private void initView(){
        name = (EditText)findViewById(R.id.name);
        mobil = (EditText)findViewById(R.id.tel);
        gender = (EditText)findViewById(R.id.gender);
        birth = (TextView) findViewById(R.id.birth);
        email = (EditText)findViewById(R.id.email);
        car = (EditText)findViewById(R.id.num);
    }

    public static boolean isCHinese(char c){
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if(ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                ||ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                ||ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                ||ub == Character.UnicodeBlock.GENERAL_PUNCTUATION  // GENERAL_PUNCTUATION 判断中文的“号
                ||ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION     // CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
                ||ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS    // HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
        )
            return true;
        return false;
    }
    public static int ChineseNum(String str){
        char[] ch =  str.toCharArray();
        int count = 0;
        for (char c : ch) {
            if(isCHinese(c))
                count++;
        }
        return count;
    }

    public static int AbcNum(String str){
        char[] ch = str.toCharArray();
        int count = 0;
        for(char c : ch){
            if(c>'a'&&c<'z')
                count++;


        }return count;
    }


}
