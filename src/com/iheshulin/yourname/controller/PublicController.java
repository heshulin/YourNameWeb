package com.iheshulin.yourname.controller;

import com.iheshulin.yourname.bean.User;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by HeShulin on 2017/5/16.
 */
@IocBean
public class PublicController {
    @Inject
    Dao dao;

    @Ok("re")
    @Fail("http:500")
    @At("login")
    @POST
    public String login(@Param("username")String username, @Param("password")String password, HttpServletRequest request, HttpSession session){

        if (dao.count(User.class, Cnd.where("username","=",username).and("password","=",password))>0){
            session.setAttribute("username", username);
            return "jsp:/home";
        }
        request.setAttribute("msg","用户名或者密码错误");
        return "jsp:/index";
    }

    @At("login")
    @GET
    @Ok("re")
    @Fail("http:500")
    public String loginPage(){
        return "jsp:/index";
    }
}
