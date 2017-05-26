package test;

import com.iheshulin.Filter.YournameFilter;
import com.iheshulin.yourname.util.UploadFile;

import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Date;
import java.util.logging.Filter;
import java.util.logging.LogRecord;


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
    public static void main(String arg0[]) throws Exception {
        UploadFile uf = new UploadFile("yourname-headphotos");
        uf.uploadHeadPhoto("C:\\Users\\LC\\Desktop\\timg.jpg");

    }


}
