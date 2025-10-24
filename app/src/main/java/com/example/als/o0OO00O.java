package com.example.als;

import android.text.TextUtils;
import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class o0OO00O {
    public static String OooO00o(String s, String s1) {
        String s2;
        if(TextUtils.isEmpty(s)) {
            return s;
        }

        if(s1 != null && !s1.isEmpty()) {
            try {
                s2 = Base64.encodeToString(o0OO00O.OooO0O0(s1, s), 0);
                return !s2.contains("\n") ? s2 : s2.replace("\n", "");
            }
            catch(Exception exception0) {
                exception0.printStackTrace();
                return "";
            }

            //return s2;
        }

        return s;
    }

    public static byte[] OooO0O0(String s, String s1) throws Exception {
        Cipher cipher0 = Cipher.getInstance("AES/ECB/PKCS7Padding");
        cipher0.init(1, new SecretKeySpec(s.getBytes(), "AES"));
        return cipher0.doFinal(s1.getBytes("UTF-8"));
    }
}
