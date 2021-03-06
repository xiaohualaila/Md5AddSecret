package com.example.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.util.AESUtil;
import com.example.util.ChangeDESString;
import com.example.util.R;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button request_;
    private TextView tv_message;

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

    public void requestApp() {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType json = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        JSONObject obj = new JSONObject();

        try {
            obj.put("username", "shadmin");
            obj.put("password", "123456");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String des_str = ChangeDESString.changeDESString(obj,"DEVICEAPI_GETUSERLIST");
        RequestBody body = RequestBody.create(json, des_str);
        Request request = new Request.Builder()//创建Request 对象。
                .url("https://cloud.zq12369.com/nodeapi/deviceapi")
                .post(body)
                .build();
        Call call = client.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // callBack.failed("网路异常");
                  //      Log.i("sss", "网路异常 "+e.getMessage());
                        tv_message.setText(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {//回调的方法执行在子线程。
                            String str = null;
                            try {
                                str = response.body().string();
                                str = AESUtil.decrypt(str,"EMNZCGFSBWFWAQ==","bWFwaWVuY29kZQ==");
                                tv_message.setText(str);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            tv_message.setText(response.message());
                        }
                    }
                });
            }
        });
    }

}
