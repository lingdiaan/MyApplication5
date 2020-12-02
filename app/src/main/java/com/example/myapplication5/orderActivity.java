package com.example.myapplication5;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class orderActivity extends AppCompatActivity {

    private List<Order> orderList= new ArrayList<>();
    private TextView textView;
    private String parkName,parkTime,parkId;
    private Handler handler;

protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_order);
    getOrder();
    handler = new Handler();


}
    private void getOrder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建一个OkHttp实例
                    Request request = new Request.Builder().url("https://api.ohaiyo.vip/reserved/?ordering=-status&page_size=999999999999").addHeader("Authorization","jwt"+" "+LoadActivity.token) .build();
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
            JSONArray results = jsonObject.getJSONArray("results");//得到键为results的JSONArray
            System.out.println(results.length());
            for(int i =0;i<results.length();i++) {
                JSONObject obj = results.getJSONObject(i);
                //JSONObject jsonObject = new JSONObject(response.body().toString());
                parkId = obj.getString("parkinglot");
                parkTime = obj.getString("in_time");


                OkHttpClient clientName = new OkHttpClient();
                Request requestName = new Request.Builder().url("https://api.ohaiyo.vip/parkinglot/?id="+parkId).build();
                Response response  =clientName.newCall(requestName).execute();
                String data = response.body().string();
                System.out.println(data);
                JSONObject jsonObject1Id = new JSONObject(data);
                JSONArray resultsGetName = jsonObject1Id.getJSONArray("results");//得到键为results的JSONArray
                JSONObject ans = resultsGetName.getJSONObject(0);
                parkName = ans.getString("name");

                Order order = new Order(parkName,parkTime);
                orderList.add(order);
                System.out.println(i);
            }




        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.post(runnableUi);
    }
    Runnable runnableUi=new Runnable(){
        @Override
        public void run() {
            OrderAdapter adapter = new OrderAdapter(orderActivity.this, R.layout.order_item, orderList);
            ListView listView = (ListView)findViewById(R.id.list_view);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Order order = orderList.get(position);
                    Toast.makeText(orderActivity.this,order.getParkName()+"--------"+order.getParkTime(),Toast.LENGTH_LONG);

                }
            });
//更新界面

        }

    };
//private void initFruits(){
//    int j=0;
//    for(int i = 0;i<5;i++){
//        Order apple = new Order("Apple","还没熟透"+j++);
//        orderList.add(apple);
//        Order banana = new Order("Banana","还没熟透"+j++);
//        orderList.add(banana);
//        Order orange = new Order("Orange","还没熟透"+j++);
//        orderList.add(orange);
//       Order pear = new Order("Pear","还没熟透"+j++);
//
//       orderList.add(pear);
//
//
//    }

}

