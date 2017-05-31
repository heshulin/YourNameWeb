package com.iheshulin.yourname.controller;

import com.iheshulin.yourname.bean.CheckCode;
import com.iheshulin.yourname.bean.User;
import com.iheshulin.yourname.util.MD5;
import com.iheshulin.yourname.util.Message;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by HeShulin on 2017/5/31.
 */

@IocBean
public class FindPassWordController {
    private Log log = Logs.get();
    private MD5 md5=MD5.getMd5();


    @Inject
    Dao dao;


    @Ok("json")
    @Fail("http:500")
    @At("findpassword1")
    @POST
    public Object findPassword1(@Param("userphone")String userphone, HttpServletRequest request){
        //获取验证码过期时间
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
        Date tomorrow = c.getTime();

        try{
            NutMap re = new NutMap();
            Message message=new Message();
            String messagetext=message.sendMessage(userphone);
            CheckCode checkCode=new CheckCode();
            checkCode.setUserphone(userphone);
            checkCode.setCheckcode(messagetext);
            checkCode.setExpiration(tomorrow);
            dao.insert(checkCode);
            re.put("statues",1);
            re.put("msg","OK");
            boolean findpassword=true;
            return re;
        }catch (Exception e){
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error finding password");
            return re;
        }

    }

    @Ok("json")
    @Fail("http:500")
    @At("findpassword2")
    @POST
    public Object findPassword2(@Param("userphone")String userphone,@Param("password")String password,@Param("checkcode")String checkcode, HttpServletRequest request){

        //获取当前时间
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = new Date();

        try{
            NutMap re = new NutMap();
            User user=new User();
            List<CheckCode> tempuser=dao.query(CheckCode.class, Cnd.where("userphone","=",userphone));
            //记得要-1
            Date temptime=tempuser.get(tempuser.size()-1).getExpiration();
            String tempcheckcode=tempuser.get(tempuser.size()-1).getCheckcode();

            if(tempcheckcode==checkcode){
                if(today.before(temptime)) {
                    User user1 = dao.fetch(User.class, Cnd.where("userphone", "=",userphone));
                    user1.setPassword(password);
                    dao.update(user);
                    re.put("statues", 1);
                    re.put("msg", "OK");
                    return re;
                }
                else{
                    re.put("statues", 0);
                    re.put("msg", "验证码已过期");
                    return re;
                }
            }
            else {
                re.put("statues",0);
                re.put("msg","验证码错误");
                return re;
            }

        }catch (Exception e){
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error in finding password2");
            return re;
        }

    }


}

