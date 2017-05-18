package com.iheshulin.yourname.util;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
/**
 * Created by LC on 2017/5/18.
 */
public class GetDatetime {
    public String getNowString(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }
    public Date GetNow(){
        return new Date();
    }
    public String datetimeToString(Date datetime){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(datetime);
    }
    public Date stringToDate(String datetime){
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
            Date date = sdf.parse(datetime);
            return date;
        }
        catch (ParseException e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
