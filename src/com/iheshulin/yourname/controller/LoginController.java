package com.iheshulin.yourname.controller;

import com.iheshulin.yourname.bean.User;
import com.iheshulin.yourname.util.MD5;
import com.iheshulin.yourname.util.SecretKey;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;


/**
 * Created by LC on 2017/5/17.
 */


@IocBean
public class LoginController {
    private  Log log = Logs.get();
    private MD5 md5=MD5.getMd5();

    @Inject
    Dao dao;
    @Ok("json")
    @Fail("http:500")
    @At("login")
    @POST
    public Object doLogin(@Param("userphone")String userPhone, @Param("password")String password){
        try{
            NutMap re = new NutMap();
            if(userPhone!=null&&password!=null) {
                password = md5.getMd5(password);
                User u = dao.fetch(User.class, Cnd.where("userphone", "=", userPhone).and("password", "=", password));
                if (u!=null) {
                    String secretKey = SecretKey.getSecretKey();
                    u.setSecretkey(secretKey);
                    dao.update(u);
                    re.put("statues", 1);
                    re.put("msg", "OK");
                    re.put("secretkey", secretKey);
                    re.put("userid", u.getId());
                } else {
                    re.put("statues", 0);
                    re.put("msg", "账号或密码错误");
                }
            }else{
                re.put("statues", 0);
                re.put("msg", "账号或密码错误");
            }
            return re;
        }catch (Exception e){
            log.info(e);
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error in login");
            return re;
        }

    }

}
