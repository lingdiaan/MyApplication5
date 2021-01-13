package com.example.myapplication5;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class timeChooseActivity extends Activity {
    private int hour;
    private int minute;
    private String startHour,startMin;
    private int startYear,startMonth,startDay;
    private String endHour,endMin;
    private int endYear,endMonth,endDay;
    private String dateStr;
    private EditText edStart,edEnd;
    private AlertDialog dialog,dialog1;
    private Button tijiao;
    private int price;
    private String name,pri;
    private int id = 1;
    private String addTime;
    private String trade_no;

    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    public static final String APPID = "2016110200785729";

    /**
     * 用于支付宝账户登录授权业务的入参 pid。
     */
    public static final String PID = "2088102181631744";

    /**
     * 用于支付宝账户登录授权业务的入参 target_id。
     */
    public static final String TARGET_ID = "com.alipay.sdk.pay";

    /**
     *  pkcs8 格式的商户私钥。
     *
     * 	如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个，如果两个都设置了，本 Demo 将优先
     * 	使用 RSA2_PRIVATE。RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议商户使用
     * 	RSA2_PRIVATE。
     *
     * 	建议使用支付宝提供的公私钥生成工具生成和获取 RSA2_PRIVATE。
     * 	工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    public static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCKqwmJy1mltdPElGRltks6eEx3E9uSC7NON18Sk9PP7wSSAVbmqHAKDeS+49AspezFBjUqW6G8MAvCLh5pRJcUyDt/JB1c2Ab58tuAAZ8kCUuPFBt9TZzXgIcnlwlKSdLLiagSaDrW9heCsfRpp7JyrAfkfyqOAW642O5m9d+JewxGex5H6SBqMhGUGhx/eKkBfOOq8TcGgYiAnyxv1V3aXunceHmAyMaEw/p66cJRsk0ADGs0JnHqCJrvqaOD7IN4K8JfCU+3lHwppMNfJCbvAKLskhC5S2CzCAtDACb9978eVmDCto6NRFNtNFw76F6SAEiuf0KUpfmgNGSDWsLXAgMBAAECggEAHMnhkw/6rqKPMpK0PUGZYKw1A8vFnA59zVFFla/HG7Y9tqo8hmqVyCCWSuM1Bu/ztfvR8ddQPqei6U911Nj+nLvtTjoLNolK+X1oAK1Vgr/DRhMgmeURGNSAOsHqlde5wbP0hs3I2XQB8YpMedrs+02n3dJg3VaCzDGNXSbSn9Hzrfnro6rHIKOIG/8kazUa40qTp6IdjHXRK9a2PDVUu0OM4rjgb1E4Q+mkXHG1wK8AS9SYNLeZrG0gOdi+WWcTPa5w4mz7CYx4EjGQJDbhxPR8F4vDgwfEDzk2dHIq+fk7e1+lDl/ST0P5o/M+faRyPL09Jp9pcEsa+IiL0k4MYQKBgQDExmw4AgZxn3nk+G1ALJ+885JXw50NH3dGHpuAKxErOqHJNGCQLb0fZsfFTiJsw9RPWAeBsxXE6D71nGaN/asZKf4zr6+q95CrUb1N5Ca6YTG5pnslkLL8UdmRNnV0OKwPtpoe4HJWAkaWtbU3FT4OjKF+/ka5aIQF8wMrMO0njwKBgQC0Z3SVkDxEFTcgFgajm6I0otR6LPywMdyB8nrY4YsE95AeZMborlfwBg3QSTnXCH2Zzpx8pvZwpbDfaIrqvGEDQkrvpHCbtXM1h67Rmp4ugR87iIwxXLB+I0sLWLtT5tIJQV5mHyLUbOtH/Q8JIyXkrTZDDJfQo1+RF26hUQnMOQKBgQCSaHh4q7kTrW7KmLTg/NLVif0m49rkurbKK1fT4zdhDLz3scrvO7jttlGJUnt2pbZAWuUq8Y6O9aZypK4Bk+5MSNxkpKF1+cFgVu8dF1ZhcpPG6EHUT3d9GYFh9D0r/ka3YkwGEUXBDOxskkKE+38y4BwBGzyQE12393oyFrM9rQKBgEIAa6XgfDwIav+hL1KiOQj63bPJS7WGuH8OYKWCduMdU6vbAO7WAjQ9csZWVAP5BkLEVXpBd34lEH3b+J8CxpdzpIjiZ5SAISNffbUP1Xl8IhoczfWtTKEJdoYzM23xz7w1Hz1LfOms47OVwO993Xo5aNXFALIDY45ovT/lryj5AoGARc6bM7v/+DPEFq9Woj2ZvlKVuLFfCC3fCSUPPCXFqw6OCxlGL4x0U+XQ9IBvFvpe8L7fjieYTJVdsNPSVVSyIAf882BoHdq0Wdai3eBu4vnx1cq9oQ08AANs6LsdpGaIpkbYn6Bj8mnddvlL8N8sxLfuVz/gn3u7m8pZfIMH+Dg=";
    public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKGPMVtqoZ3TCsT1BPEZYWrhaNOd89wWFdlweDQS6fSl8VQhUVtHNrDdL27/QWrm+A08YYXHVI/aFGDO4nCLVHrqSBjYFVKv1ozl7shitLmAg8dmwewJHcJuQ8LO7MFwe0mXCZsectk2yYYNFLmGWF0kln7rbaB+1cqF4S4u17QDAgMBAAECgYALAcd6E/S43PUB4DOa/YCumHbc3AkOOI76hngaDCPWYCvl8HMrhdmLCTa/GDLrxpqlxDRcuezf9BqpUc8JneR+c5ntNbTsOWVtXHoQf+zIPnTbF+dgIvB+HRwBv2PO6YMlAQSo8c5vxRSiN5uihIinQzeOnZChm6jJd70aJvtYUQJBAM/ljp7pkP5y7apbWdylz1F2RpMrGE9EMVxgHeOf0GqQcbGiuGxbvOyYsKJuepwWCuZi/cpHnnL1gRQMiXk9nqsCQQDG8OnDoH6JGk3+qXMcbmzHYrqE5h5qSldbRVnYWSLxgL6LdnuUtoaPFYZz44eMcxFKQHjAaRRvKErKYVlsh2AJAkAFb+mE+nLSVMsmc3EsNiHv7Xn3C199YzkvQ0xE0b8vqktu6+SK4PNV9MBZ3y3RuznZwKkGi0z3kLgpgBJwW041AkBNaN69JU03Ugn5Rrwo2vru1obXQaeiGk1FkYW1PnHvYPZD1BWgNynCsVCA9Y7/4qJerxmNXRX7bsUzXI/sP/zpAkB+XXA42hHaJcJT76U6NnzzRzNRacY++J2NOsg+GAOC+/JuhJbXN0QT0S9tDsougu3I/YYXoR0QeS5J10YwPZP7";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    private String ParkName,carnum;
    private TextView carNum;
    private Spinner mSpinner;
    private DatePickerDialog.OnDateSetListener mEndDateSetListener,mStartDateSetListener;
    @SuppressLint("SimpleDateFormat")
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_choose);
        Bundle bundle = getIntent().getExtras();
        ParkName = bundle.getString("name");
        System.out.println(ParkName+"iiiiiiiiiiiiiiiiiiiiiiiiiiiddddddddddddddddddddddddddddddddd");
        carNum = (TextView)findViewById(R.id.car_num) ;
        carnum = carNum.getText().toString();
        edStart=(EditText)findViewById(R.id.edit);
        edEnd=(EditText)findViewById(R.id.edit2);
        edStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog = new AlertDialog.Builder(timeChooseActivity.this).create();
                dialog.show();
                dialog.getWindow().setContentView(R.layout.time_dialog);

                Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR);
                minute = c.get(Calendar.MINUTE);

                ((TimePicker) dialog.getWindow().findViewById(R.id.timePicker)).setIs24HourView(true);
                ((TimePicker) dialog.getWindow().findViewById(R.id.timePicker)).setOnTimeChangedListener((view, hourOfDay, minute) -> {
                    timeChooseActivity.this.hour = hourOfDay;
                    timeChooseActivity.this.minute = minute;

                   startHour=Integer.toString(timeChooseActivity.this.hour);
                    startMin=Integer.toString(timeChooseActivity.this.minute);
                    if(minute<10)
                        dateStr=startHour+":"+"0"+startMin;
                    else
                    dateStr=startHour+":"+startMin;

                    Log.i("time-------------->",""+dateStr);
                });

                dialog.getWindow().findViewById(R.id.time_sure).setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Calendar cal = Calendar.getInstance();
                        startYear = cal.get(Calendar.YEAR);
                        startMonth = cal.get(Calendar.MONTH);
                        startDay = cal.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog = new DatePickerDialog(
                                timeChooseActivity.this,
                                android.R.style.Theme_Holo_Dialog_MinWidth,
                                mStartDateSetListener,
                                startYear, startMonth, startDay);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                    }});



        }
        });
        mStartDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                Log.d(TAG, "onDateSet: date: " + year + "/" + month + "/" + dayOfMonth);
                int rm = month + 1;
                edStart.setText(year + "-" + rm + "-" + dayOfMonth+" "+dateStr);
            }
        };
        edEnd.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                dialog = new AlertDialog.Builder(timeChooseActivity.this).create();
                dialog.show();
                dialog.getWindow().setContentView(R.layout.time_dialog);

                Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR);
                minute = c.get(Calendar.MINUTE);

                ((TimePicker) dialog.getWindow().findViewById(R.id.timePicker)).setIs24HourView(true);
                ((TimePicker) dialog.getWindow().findViewById(R.id.timePicker)).setOnTimeChangedListener((view, hourOfDay, minute) -> {
                    timeChooseActivity.this.hour = hourOfDay;
                    timeChooseActivity.this.minute = minute;

                    endHour=Integer.toString(timeChooseActivity.this.hour);
                    endMin=Integer.toString(timeChooseActivity.this.minute);
                    if(minute<10){
                        dateStr = endHour+":"+"0"+endMin;
                    }
                    else dateStr=endHour+":"+endMin;

                    Log.i("time-------------->",""+dateStr);
                });


                dialog.getWindow().findViewById(R.id.time_sure).setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Calendar cal = Calendar.getInstance();
                        endYear = cal.get(Calendar.YEAR);
                        endMonth = cal.get(Calendar.MONTH);
                        endDay = cal.get(Calendar.DAY_OF_MONTH);
                        System.out.println(endYear+"---"+endMonth+"----"+endDay);

                        DatePickerDialog dialog = new DatePickerDialog(
                                timeChooseActivity.this,
                                android.R.style.Theme_Holo_Dialog_MinWidth,
                                mEndDateSetListener,
                                endYear, endMonth, endDay);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                    }});
            }

        });
        mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                Log.d(TAG, "onDateSet: date: " + year + "/" + month + "/" + dayOfMonth);
                int rm = month + 1;
                edEnd.setText(year + "-" + rm + "-" + dayOfMonth+" "+dateStr);
            }
        };
        tijiao = (Button)findViewById(R.id.tijiao);
        tijiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("提交");
                if(endHour!=null&&startHour!=null&&endMin!=null&&startMin!=null){
                int hour = Integer.parseInt(endHour)-Integer.parseInt(startHour);
                int minute = Integer.parseInt(endMin)-Integer.parseInt(startMin);
                String endString[] = edEnd.getText().toString().split("-");
                String startString[] = edStart.getText().toString().split("-");
                int year = Integer.valueOf(endString[0])-Integer.valueOf(startString[0]);
                int month = Integer.valueOf(endString[1])-Integer.valueOf(startString[1]);
                    String endDay[] = endString[2].split(" ");
                    String startDay[] = startString[2].split(" ");
                int day = Integer.valueOf(endDay[0])-Integer.valueOf(startDay[0]);
                    System.out.println(year+"----"+month+"-----"+day);
                if(year<0||(year==0&&month<0)||(year==0&&month==0&&day<0)){
                    Toast.makeText(timeChooseActivity.this,"时间选择错误",Toast.LENGTH_LONG).show();

                }

                else if(year==0&&month==0&&day==0&&hour<0&&minute<0)
                    Toast.makeText(timeChooseActivity.this,"时间选择错误",Toast.LENGTH_LONG).show();
                else if(minute<0){
                    hour -=-1;
                    minute +=60;
                }

                else if(carNum.getText().toString().length()!=7||ChineseNum(carNum.getText().toString())!=1||ChineseNum(carNum.getText().toString())==0||AbcNum(carNum.getText().toString())==0){

                    Toast.makeText(timeChooseActivity.this,"请输入正确格式的车牌号",Toast.LENGTH_SHORT).show();

                }
                else {
                float hourf = (float)hour;
                float mintf=(float)minute;
                float allPrice = (hour+minute/60)*price;
                carnum=carNum.getText().toString();
//                    if(carnum.length()!=7||!isCHinese(carnum.charAt(0))||ChineseNum(carnum)>1)
//                        Toast.makeText(timeChooseActivity.this,"请正确输入车牌号",Toast.LENGTH_LONG);
//
//                    else{
                    Toast.makeText(timeChooseActivity.this,"共停留"+hour+"小时"+minute+ "分钟"+",需消费"+allPrice+"元",Toast.LENGTH_LONG).show();
                    pri = String.valueOf(allPrice);
                    HashMap<String,String> mapOrder=new HashMap<>();
//                    payV2("25");
                        System.out.println("共停留"+hour+"小时"+minute+ "分钟"+",需消费"+allPrice+"元");
//                    int trade_no = (int)(Math.random()*1000000+1);
//                    mapOrder.put("trade_no", String.valueOf(trade_no));
//                    mapOrder.put("post_script",name);
                    mapOrder.put("post_script","123456");
                    mapOrder.put("order_mount",pri);
                    Gson gson = new Gson();
                    String data = gson.toJson(mapOrder);
                    sentOkHttp("https://api.ohaiyo.vip/order/",data);
                    try{
                        Thread.sleep(2000);
                    }catch (InterruptedException e) {}
                    int id = (int)(Math.random()*1000000+1);
                    HashMap<String,String> mapReserved = new HashMap();
                    mapReserved.put("trade_no",trade_no);
                    mapReserved.put("id",String.valueOf(id));
                    mapReserved.put("car_num",carnum);
                    mapReserved.put("status","outside");
                    mapReserved.put("in_time",edStart.getText().toString());
                    mapReserved.put("out_time",edEnd.getText().toString());
                    mapReserved.put("add_time", addTime);
                    mapReserved.put("parkinglot",ParkName);
                    mapReserved.put("carport","00120191213125928001");
//                        mapReserved.put("parkinglot","001");
                    Gson gson1 = new Gson();
                    String data1 = gson1.toJson(mapReserved);
                    sentOkHttp("https://api.ohaiyo.vip/reserved/",data1);

                    Intent intent = new Intent(timeChooseActivity.this,orderActivity.class);
                    startActivity(intent);
                    finish();

                    }
                }
            else {
                Toast.makeText(timeChooseActivity.this,"请正确输入时间",Toast.LENGTH_LONG).show();
                }}
//            }
        });
//        List<String> list = new ArrayList<>();
//        list.add("inside");
//        list.add("outside");
//        list.add("finish");
//        mSpinner = (Spinner)findViewById(R.id.spinner);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_vict, list);
//        adapter.setDropDownViewResource(R.layout.spinner_item_vict);
//        mSpinner.setAdapter(adapter);
}

//    @SuppressLint("HandlerLeak")
//    private Handler mHandler = new Handler() {
//        @SuppressWarnings("unused")
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case SDK_PAY_FLAG: {
//                    @SuppressWarnings("unchecked")
//                    com.example.myapplication5.PayResult payResult = new com.example.myapplication5.PayResult((Map<String, String>) msg.obj);
//                    /**
//                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
//                     */
//                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//                    String resultStatus = payResult.getResultStatus();
//                    // 判断resultStatus 为9000则代表支付成功
//                    if (TextUtils.equals(resultStatus, "9000")) {
//                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        showAlert(timeChooseActivity.this, getString(R.string.pay_success) + payResult);
//                    } else {
//                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        showAlert(timeChooseActivity.this, getString(R.string.pay_failed) + payResult);
//                    }
//                    break;
//                }
//                case SDK_AUTH_FLAG: {
//                    @SuppressWarnings("unchecked")
//                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
//                    String resultStatus = authResult.getResultStatus();
//
//                    // 判断resultStatus 为“9000”且result_code
//                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
//                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
//                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
//                        // 传入，则支付账户为该授权账户
//                        showAlert(timeChooseActivity.this, getString(R.string.auth_success) + authResult);
//                    } else {
//                        // 其他状态值则为授权失败
//                        showAlert(timeChooseActivity.this, getString(R.string.auth_failed) + authResult);
//                    }
//                    break;
//                }
//                default:
//                    break;
//            }
//        };
//    };
//    public void payV2(String allPrice) {
//        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
//            showAlert(this, getString(R.string.error_missing_appid_rsa_private));
//            return;
//        }
//
//        /*
//         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
//         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
//         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
//         *
//         * orderInfo 的获取必须来自服务端；
//         */
//        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
//        Map<String, String> params = com.example.myapplication5.OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2,allPrice);
//        String orderParam = com.example.myapplication5.OrderInfoUtil2_0.buildOrderParam(params);
//
//        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
//        String sign = com.example.myapplication5.OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
//        final String orderInfo = orderParam + "&" + sign;
//
//        final Runnable payRunnable = new Runnable() {
//
//            @Override
//            public void run() {
//                PayTask alipay = new PayTask(timeChooseActivity.this);
//                Map<String, String> result = alipay.payV2(orderInfo, true);
//                Log.i("msp", result.toString());
//
//                Message msg = new Message();
//                msg.what = SDK_PAY_FLAG;
//                msg.obj = result;
//                mHandler.sendMessage(msg);
//            }
//        };
//
//        // 必须异步调用
//        Thread payThread = new Thread(payRunnable);
//        payThread.start();
//    }
//    private static void showAlert(Context ctx, String info) {
//        showAlert(ctx, info, null);
//    }
//
//    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
//        new AlertDialog.Builder(ctx)
//                .setMessage(info)
//                .setPositiveButton(R.string.confirm, null)
//                .setOnDismissListener(onDismiss)
//                .show();
//    }
    private void sentOkHttp(String url,String data){
        System.out.println("data:"+data);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        RequestBody requestBody = FormBody.create(MediaType.parse("application/json;charset=utf-8"),data);

        Request request = new Request.Builder().post(requestBody).url(url).addHeader("Authorization","JWT"+" "+LoadActivity.token).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String stringTemp = response.body().string();
                System.out.println("request:"+stringTemp+"url:"+url);
                switch (url){
                    case "https://api.ohaiyo.vip/order/":

                        try {
                            JSONObject jsonObject = new JSONObject(stringTemp);
                            addTime = jsonObject.getString("add_time");
                            trade_no=jsonObject.getString("trade_no");
                            System.out.println("addTime"+addTime+"trade_no==============>"+trade_no);
                            try{
                            Thread.sleep(1000);}catch (Exception e){}
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                        default:
                            break;
                }


            }
        });
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
            if(c>='A'&&c<='Z')
                count++;


        }return count;
    }



}
