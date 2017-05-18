package com.iheshulin.yourname.controller;

import com.iheshulin.yourname.bean.User;
import com.iheshulin.yourname.util.MD5;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by HeShulin on 2017/5/17.
 */


@IocBean
public class LoginController {
    @Inject
    Dao dao;

    private MD5 md5=MD5.getMd5();

    @Ok("re")
    @Fail("http:500")
    @At("login")
    @POST
    public String login(@Param("username")String username, @Param("password")String password, HttpServletRequest request){

        if (dao.count(User.class, Cnd.where("username","=",username).and("password","=",password))>0){


            return "jsp:/home";
        }
        request.setAttribute("msg","用户名或者密码错误");
        return "jsp:/index";
    }

}
