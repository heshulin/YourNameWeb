package com.iheshulin.yourname.controller;

import com.iheshulin.yourname.bean.CheckCode;
import com.iheshulin.yourname.bean.User;
import com.iheshulin.yourname.util.MD5;
import com.iheshulin.yourname.util.Message;
import com.iheshulin.yourname.util.UploadFile;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by HeShulin on 2017/5/31.
 */
@IocBean
public class ChangePersonalInformation {
    private Log log = Logs.get();
    private MD5 md5=MD5.getMd5();


    @Inject
    Dao dao;
    //修改个人信息
    @Ok("json")
    @Fail("http:500")
    @At("ChangePersonalInformation")
    @AdaptBy(type = UploadAdaptor.class, args = { "${app.root}/WEB-INF/tmp" })
    @POST
    public Object ChangePersonalInformation(@Param("userid")int userid, @Param("username")String username, @Param("age")String age, @Param("sex")String sex, @Param("secretkey")String secretkey, HttpServletRequest request) {
        try {
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("id", "=", userid).and("secretkey", "=", secretkey)).isEmpty();
            if(!res) {
                //用户名
                if (username != null) {
                    User user1 = dao.fetch(User.class, Cnd.where("secretkey", "=", secretkey).and("id", "=", userid));
                    user1.setUsername(username);
                    dao.update(user1);
                }
                //年龄
                if (age != null) {
                    User user1 = dao.fetch(User.class, Cnd.where("secretkey", "=", secretkey).and("id", "=", userid));
                    user1.setAge(age);
                    dao.update(user1);
                }
                //性别
                if (sex != null) {
                    User user1 = dao.fetch(User.class, Cnd.where("secretkey", "=", secretkey).and("id", "=", userid));
                    user1.setSex(sex);
                    dao.update(user1);
                }
                re.put("statues", 1);
                re.put("msg", "OK");
                return re;
            }else{
                re.put("statues", 0);
                re.put("msg", "请登录");
                return re;
            }
        } catch (Exception e) {
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error in ChangePersonalInformation");
            return re;
        }

    }

    @Ok("json")
    @Fail("http:500")
    @At("do_change_head_photo")
    @AdaptBy(type = UploadAdaptor.class, args = { "${app.root}/WEB-INF/tmp" })
    @POST
    public Object doChangeHeadPhoto(@Param("userid")int userid, @Param("userphoto")TempFile userphoto, @Param("secretkey")String secretkey) {
        try {
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("id", "=", userid).and("secretkey", "=", secretkey)).isEmpty();
            if (!res) {
                //用户头像
                User user1 = dao.fetch(User.class, Cnd.where("secretkey", "=", secretkey).and("id", "=", userid));
                UploadFile uploadFile = new UploadFile();
                String fileUrl = uploadFile.uploadHeadPhoto(userphoto.getFile().getPath());
                user1.setUserphoto(fileUrl);
                dao.update(user1);
                re.put("statues", 1);
                re.put("msg", "OK");
                return re;
            } else {
                re.put("statues", 0);
                re.put("msg", "请登录");
                return re;
            }
        }catch (Exception e){
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error in do_change_head_photo");
            return re;
        }
    }

}
