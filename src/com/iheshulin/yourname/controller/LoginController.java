package com.iheshulin.yourname.controller;

import com.iheshulin.yourname.bean.User;
import com.iheshulin.yourname.util.MD5;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by HeShulin on 2017/5/17.
 */


@IocBean
public class LoginController {
    private  Log log = Logs.get();
    private MD5 md5=MD5.getMd5();

    @Inject
    Dao dao;
    @AdaptBy(type=JsonAdaptor.class)
    @Ok("json")
    @Fail("http:500")
    @At("login")
    @POST
    public Object login(@Param("username")String username, @Param("password")String password, HttpServletRequest request){
        try{
            password = MD5.getMd5(password);
            NutMap re = new NutMap();
            if(dao.query(User.class, Cnd.where("username","=",username).and("password","=",password)).size()>0){
                re.put("statues",1);
                re.put("msg","OK");
            }else{
                re.put("statues",0);
                re.put("msg","账号或密码错误");
            }
            return re;
        }catch (Exception e){
            log.info("error in login");
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error in login");
            return re;
        }

    }

}
