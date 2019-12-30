package com.example.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.md5addsecret.DESUtil;
import com.example.md5addsecret.Md5Util;
import com.example.md5addsecret.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView request_;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        request_= findViewById(R.id.request_);
        request_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.request_:
                    requestApp();
                    break;
            }
    }

    /**
     * 加密接口测试
     *
     * @param callBack
     */
    private static Handler handler = new Handler(Looper.getMainLooper());
//    {
//        "appId": "4ab4aca1c61c78b89338c3e3804e0e9d",
//            "method": "DATAAPI_GETDATA",
//            "timestamp": "1484105936",
//            "clienttype": "WEB",
//            "object": {
//        "city": "杭州"
//    }
//        "secret": "md5(appId + method + timestamp + clienttype + json(object) )"
//    }
    public void requestApp() {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType json = MediaType.parse("application/json; charset=utf-8");
        JSONObject object = new JSONObject();

        long timestamp = System.currentTimeMillis();
        try {


            object.put("appId", "e5c2c8c309978e58a5881daf3562f13b");
            object.put("method", "DEVICEAPI_GETUSERLIST");
            object.put("timestamp",timestamp);
            object.put("clienttype", "Android");
                JSONObject obj = new JSONObject();
                obj.put("username","shadmin");
                obj.put("password","123456");
            object.put("object", obj);

                String md5_str ="1d77fc5874c972062dc72fd949ad5304"
                       + "DEVICEAPI_GETUSERLIST"
                       + timestamp
                       + "Android"
                       + obj;
                String md5 = Md5Util.getMd5(md5_str);
            object.put("secret", md5);
            Log.i("sss","加密前   "+object.toString());
            String des_str =DESUtil.encrypt(object.toString());
            Log.i("sss","加密后   " +des_str);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json, DESUtil.encrypt(object.toString()));
        Request request = new Request.Builder()//创建Request 对象。
                .url("https://cloud.zq12369.com/nodeapi/deviceapi")
                .post(body)
                .build();
        Call call2 = client.newCall(request);
        //请求加入调度
        call2.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                       // callBack.failed("网路异常");
                        Log.i("sss","网路异常 ");
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {//回调的方法执行在子线程。
                            String str = null;
                            try {
                                str = response.body().string();
                                Log.i("sss","返回结果 "+str);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Log.i("sss","返回结果 "+response.code());
                        }
                    }
                });

            }
        });
    }
}