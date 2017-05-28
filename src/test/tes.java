package test;

import com.iheshulin.yourname.util.Message;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Date;


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
    public static void main(String arg0[]) throws IOException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.print(df.format(new Date()));
        Message message=new Message();
        message.sendMessage("18548186741");
    }


}
