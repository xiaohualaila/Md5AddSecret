package com.example.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Sha256 {
    // sha256加密
    public static String encrySha256(String pass) {
        String newPass = "";
        if (pass == null) {
            return newPass;
        }
        try {
            Process p = null;
            BufferedReader stdout = null;
            String command = "node ./sha256.js " + pass;//路径总不能有空格
            p = Runtime.getRuntime().exec(command);
            stdout = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = stdout.readLine()) != null) {
                newPass = line;
            }
            stdout.close();
        } catch (Exception e) {
        }
        return newPass;
    }
}
