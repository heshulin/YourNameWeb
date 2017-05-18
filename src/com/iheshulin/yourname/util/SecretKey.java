package com.iheshulin.yourname.util;

import java.util.Random;

/**
 * Created by HeShulin on 2017/5/17.
 */
public class SecretKey {
    public String getRandom() {

        String src="ZXCVBNMASDFGHJKLQWERTYUIOPzxcvbnmasdfghjklpouiytrewq";
        String random="";
        for(int i=0;i<10;i++)
        {
            int temp=(int)((Math.random()*10000)%52);
            random=random + src.charAt(temp);
        }

        return random;
    }
    public static String getSecretKey(int[] random) {
        return "asd";
    }
}
