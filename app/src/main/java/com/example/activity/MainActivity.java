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
import java.util.Date;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView request_, tv_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        request_ = findViewById(R.id.request_);
        tv_message = findViewById(R.id.tv_message);
        request_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.request_:
                requestApp()    ;
            //    requestApp3();
                break;
        }
    }

    /**
     * 加密接口测试
     *
     * @param callBack
     */
    private static Handler handler = new Handler(Looper.getMainLooper());

    public void requestApp() {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType json = MediaType.parse("application/text; charset=utf-8");
        JSONObject object = new JSONObject();


        // long timestamp = new Date().getTime();
        long timestamp = 1577763311801L;
        String appId = "09a81bbeead6416312a6d2df0418a449";
        String method = "DEVICEAPI_GETUSERLIST";
        String clienttype = "Android";
        String des_str = "";
        try {
            object.put("appId", appId);
            object.put("method", method);
            object.put("timestamp", timestamp);
            object.put("clienttype", clienttype);
                        JSONObject obj = new JSONObject();
                        obj.put("username", "shadmin");
                        obj.put("password", "123456");
            object.put("object", obj);
            Log.i("xxx", "加密前的内容   " + appId + method + timestamp + clienttype + obj.toString());
            String md5 = Md5Util.getMd5(appId + method + timestamp + clienttype + obj.toString());
            object.put("secret", md5);


            Log.i("sss", "要加密的内容   " + object.toString());
            des_str = "text=" + DESUtil.encrypt(object.toString());
            Log.i("sss", "加密后   " + des_str);
            des_str =des_str.replace("+", "%2B");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("sss", "最后的请求 " + des_str);
        RequestBody body = RequestBody.create(json, des_str);

        Request request = new Request.Builder()//创建Request 对象。
                .url("https://cloud.zq12369.com/nodeapi/deviceapi")
                .post(body)
                .build();
        Call call = client.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // callBack.failed("网路异常");
                        Log.i("sss", "网路异常 ");
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
                                Log.i("sss", "返回结果 " + str);
                                tv_message.setText(str);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.i("sss", "返回结果 " + response.code());
                            tv_message.setText(response.message());
                        }
                    }
                });

            }
        });
    }

    public void requestApp2() {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType json = MediaType.parse("application/json; charset=utf-8");
        JSONObject object = new JSONObject();
        JSONObject obj = new JSONObject();
        try {
            object.put("method", "NKCLOUDAPI_GETLASTAPP");
            obj.put("appname", "张家港");

            object.put("params", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("sss", object.toString());

        RequestBody body = RequestBody.create(json, object.toString());

        Request request = new Request.Builder()//创建Request 对象。
                .url("https://cloud.zq12369.com/nodeapi/nkcleartext")
                .post(body)
                .build();
        Call call = client.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // callBack.failed("网路异常");
                        Log.i("sss", "网路异常 ");
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
                                Log.i("sss", "返回结果 " + str);
                                tv_message.setText(str);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.i("sss", "返回结果 " + response.code());
                            tv_message.setText(response.message());
                        }
                    }
                });

            }
        });
    }


    public void requestApp3() {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType json = MediaType.parse("application/text; charset=utf-8");
        JSONObject object = new JSONObject();


        long timestamp = new Date().getTime();
        String appId = "e5c2c8c309978e58a5881daf3562f13b";
        String method = " DEVICEAPI_QUERYDEVICEFACTORYIOLISTNEW";
        String clienttype = "WEB";
        String des_str = "";
        try {
            object.put("appId", appId);
            object.put("method", method);
            object.put("timestamp", timestamp);
            object.put("clienttype", clienttype);
            JSONObject obj = new JSONObject();
            obj.put("factoryabbr", "");
            object.put("object", obj);
            Log.i("xxx", "加密前的内容   " + appId + method + timestamp + clienttype + obj);
            String md5 = Md5Util.getMd5(appId + method + timestamp + clienttype + obj);
            object.put("secret", md5);


            Log.i("sss", "要加密的内容   " + object.toString());
            des_str = "text=" + DESUtil.encrypt(object.toString());
            Log.i("sss", "加密后   " + des_str);
            des_str =des_str.replace("+", "%2B");
         //   des_str =URLEncoder.encode(des_str, "utf-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        Log.i("sss", "最后的请求 " + des_str);
        RequestBody body = RequestBody.create(json, des_str);

        Request request = new Request.Builder()//创建Request 对象。
                .url("https://cloud.zq12369.com/nodeapi/deviceapi")
                .post(body)
                .build();
        Call call = client.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // callBack.failed("网路异常");
                        Log.i("sss", "网路异常 ");
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
                                Log.i("sss", "返回结果 " + str);
                                tv_message.setText(str);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.i("sss", "返回结果 " + response.code());
                            tv_message.setText(response.message());
                        }
                    }
                });

            }
        });
    }
}
