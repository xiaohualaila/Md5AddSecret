package com.example.md5addsecret;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 前面添加 text=
 * 替换字符串里面的+  换行符 /  才能正常返回 否则返回500
 */
public class ChangeDESString {

    public static String changeDESString(JSONObject obj,String _method){
        JSONObject object = new JSONObject();

      //  long timestamp = System.currentTimeMillis();
        long timestamp = 1578032037L;
        String appId = "09a81bbeead6416312a6d2df0418a449";
        String method = _method;
        String clienttype = "Android";
        String des_str = "";

        try {
            object.put("appId", appId);
            object.put("method", method);
            object.put("timestamp", timestamp);
            object.put("clienttype", clienttype);
            object.put("object", obj);
            Log.i("xxx", "加密前的内容   " + appId + method + timestamp + clienttype + obj.toString());
            String md5 = Md5Util.getMd5(appId + method + timestamp + clienttype + obj.toString());
            object.put("secret", md5);
            des_str = "text=" + DESUtil.encrypt(object.toString());
            Log.i("sss", "加密后   " + des_str);
            des_str =des_str.replaceAll("\\+", "%2B");
            des_str = des_str.replaceAll("/", "%2F");
            des_str= des_str.replaceAll("\n","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return des_str;
    }
}
