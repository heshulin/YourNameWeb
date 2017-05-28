package com.iheshulin.yourname.util;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
/**
 * Created by LC on 2017/5/18.
 */
public class GetDatetime {

    /**ljn*
        获取当前服务器时间
        yyyy-MM-dd HH:mm:ss格式
     */
    public String getNowString(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }
    /**ljn*
     获取当前服务器时间
     Date类型
     */
    public Date GetNow(){
        return new Date();
    }
    /**ljn*
     Date类型时间转yyyy-MM-dd HH:mm:ss格式的字符串
     */
    public String dateToString(Date datetime){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(datetime);
    }

    /**ljn*
     yyyy-MM-dd HH:mm:ss格式的字符串转Date
     */
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
