package test;

import java.util.Random;

/**
 * Created by HeShulin on 2017/5/17.
 */
public class tes {

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
    public static void main(String arg0[])
    {
        tes t=new tes();

        System.out.println(t.getRandom());
    }


}
