package com.iheshulin.yourname.controller;

import com.iheshulin.Filter.YournameFilter;
import com.iheshulin.yourname.bean.User;
import com.iheshulin.yourname.util.MD5;
import com.iheshulin.yourname.util.UploadFile;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * Created by HeShulin on 2017/6/3.
 */
public class GetPersonalInfoController {
    private Log log = Logs.get();
    private MD5 md5=MD5.getMd5();


    @Inject
    Dao dao;
    //获得个人信息
    @Ok("json")
    @Fail("http:500")
    @At("ChangePersonalInformation")
    @AdaptBy(type = UploadAdaptor.class, args = { "${app.root}/WEB-INF/tmp" })
    @POST
    public Object ChangePersonalInformation(@Param("userid")int userid, @Param("secretkey")String secretkey, HttpServletRequest request) {
        try {
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("userid", "=", userid).and("secretkey", "=", secretkey)).isEmpty();
            if(!res) {
                User u = dao.fetch(User.class, Cnd.where("userid", "=", userid).and("secretkey", "=", secretkey));
                re.put("statues", 1);
                re.put("msg", "ok");
                re.put("username", u.getUsername());
                re.put("age", u.getAge());
                re.put("sex", u.getSex());
                re.put("photo", u.getUserphoto());
            }else{
                re.put("statues", 0);
                re.put("msg", "请登录");
            }
            return re;
        } catch (Exception e) {
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error finding password");
            return re;
        }

    }



}
