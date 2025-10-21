package com.example.als;

public class ChallengeThreeFragment {
    public static final char[] OooO0oO;

    static {
        OooO0oO = "0123456789ABCDEF".toCharArray();
    }

    public ChallengeThreeFragment() {
        //this.OooO0o = (OooO0O0)OooO0o.OooO0O0().OooO00o(false);
    }

    public static String OooO0oO(byte[] arr_b) {
        char[] arr_c = new char[arr_b.length * 2];
        for(int v = 0; v < arr_b.length; ++v) {
            int v1 = arr_b[v] & 0xFF;
            arr_c[v * 2] = OooO0oO[v1 >>> 4];
            arr_c[v * 2 + 1] = OooO0oO[v1 & 15];
        }

        return new String(arr_c);
    }
}

