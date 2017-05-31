package test;


import com.iheshulin.baiduimage.Crawler;
import com.iheshulin.yourname.util.Message;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;


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


//        ArrayList a1 = new ArrayList();
//        a1.add("1");
//        a1.add("2");
//        a1.add("3");
//        System.out.println(a1.get(a1.size()-1));
    public static void main(String arg0[]) throws IOException {
        Crawler crawler=new Crawler();
        crawler.pullPhoto("内蒙古大学");
        System.out.println(crawler.getPhotourl());
    }


}
