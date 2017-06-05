package com.iheshulin.yourname.controller;

import com.iheshulin.yourname.bean.BottleSetting;
import com.iheshulin.yourname.bean.CheckCode;
import com.iheshulin.yourname.bean.DriftingBottle;
import com.iheshulin.yourname.bean.User;
import com.iheshulin.yourname.util.GetDatetime;
import com.iheshulin.yourname.util.MD5;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by HeShulin on 2017/5/31.
 */
@IocBean
public class DriftingBottleController {
    private Log log = Logs.get();
    private MD5 md5=MD5.getMd5();


    @Inject
    Dao dao;


    //发送漂流瓶
    @Ok("json")
    @Fail("http:500")
    @At("pushdriftingbottle")
    @POST
    public Object pushDriftingBottle(@Param("userid")int postuserid,@Param("receiveuserid")int receiveuserid,@Param("bottlecontent")String bottlecontent, @Param("secretkey")String secretkey, HttpServletRequest request) {
        try {
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("id", "=", postuserid).and("secretkey", "=", secretkey)).isEmpty();
            //获取当前时间
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date today = new Date();
            if(!res) {
                BottleSetting bottleSetting1 = new BottleSetting();
                List<BottleSetting> tempbottlesetting=dao.query(BottleSetting.class, Cnd.where("postuserid", "=", postuserid).and("receiveuserid", "=", receiveuserid));
                boolean bottleok = dao.query(BottleSetting.class, Cnd.where("postuserid", "=", postuserid).and("receiveuserid", "=", receiveuserid)).isEmpty();
                if(!bottleok){
                    Date temptime=tempbottlesetting.get(tempbottlesetting.size()-1).getEndtime();
                    if(today.before(temptime)){
                        DriftingBottle driftingBottle1=new DriftingBottle();
                        driftingBottle1.setPostuserid(postuserid);
                        driftingBottle1.setReceiveuserid(receiveuserid);
                        driftingBottle1.setBottlecontent(bottlecontent);
                        driftingBottle1.setTime(GetDatetime.getNowString());
                        dao.insert(driftingBottle1);
                        re.put("statues", 1);
                        re.put("msg", "OK");
                    }
                    else {
                        re.put("statues", 0);
                        re.put("msg", "有缘自会相见");
                    }

                }
                else{
                    re.put("statues", 0);
                    re.put("msg", "您不能给陌生人发漂流瓶");
                }

            }
            else{
                re.put("statues", 0);
                re.put("msg", "请登录");
            }
            return re;
        } catch (Exception e) {
            log.info(e);
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error in pushdriftingbottle");
            return re;
        }

    }


    @Ok("json")
    @Fail("http:500")
    @At("get_contact_list")
    @GET
    public Object getContactList(@Param("userid")int userId,@Param("secretkey")String secretKey, HttpServletRequest request){
        try{
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("id", "=", userId).and("secretkey", "=", secretKey)).isEmpty();
            if(!res) {
                /*
                String sqlCommond = "select distinct id, username, userphoto from User where id in (" +
                        "select receiveuserid from botaindiary where post userid = );
                */
                System.out.println(userId);
                System.out.println(secretKey);
                List<BottleSetting> bottleSettingList = dao.query(BottleSetting.class, Cnd.where("postuserid", "=", userId).and("endtime",">=", GetDatetime.getNow()));
                List<User> userList = new LinkedList<User>();
                System.out.println(bottleSettingList);
                if(bottleSettingList.size()>0){
                    for (BottleSetting bottleSetting: bottleSettingList) {
                        User user = dao.fetch(User.class, Cnd.where("id","=", bottleSetting.getReceiveuserid()));
                        log.info(user.getId());
                        user.setSecretkey(null);
                        user.setAge(null);
                        user.setPassword(null);
                        user.setSex(null);
                        userList.add(user);
                    }
                    re.put("statues",1);
                    re.put("msg","OK");
                }
                else{
                    re.put("msg","当前没有可以联系的人");
                    re.put("statues", 0);

                }


                re.put("data",userList);

            }else{
                re.put("statues", 0);
                re.put("msg", "请登录");
            }
            return re;
            } catch (Exception e){
            log.info(e);
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "get_contact_list");
            return re;
        }
    }


    @Ok("json")
    @Fail("http:500")
    @At("achieve_received_bottles")
    @GET
    public Object achieveReceivedBottles(@Param("userid")int userid,@Param("secretkey")String secretkey, @Param("flag") Integer flag){
        try {
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("id", "=", userid).and("secretkey", "=", secretkey)).isEmpty();
            if (!res) {
                List<DriftingBottle> driftingBottles = null;
                if(flag==0) {
                    driftingBottles = dao.query(DriftingBottle.class, Cnd.where("receiveuserid", "=", userid));
                }else{
                    driftingBottles = dao.query(DriftingBottle.class, Cnd.where("postuserid", "=", userid));
                }
                if(driftingBottles.size()>0) {
                    re.put("datalist", driftingBottles);
                    re.put("statues", 1);
                    re.put("msg", "OK");
                }else {
                    re.put("statues", 0);
                    re.put("msg", "没有记录");
                }
            } else {
                re.put("statues", 0);
                re.put("msg", "请登录");
            }
            return re;
        } catch (Exception e) {
        log.info(e);
        NutMap re = new NutMap();
        re.put("statues", 0);
        re.put("msg", "error achieve_received_bottles");
        return re;
    }
    }

    //得到漂流瓶信息
    @Ok("json")
    @Fail("http:500")
    @At("getdriftingbottle")
    @POST
    public Object getDriftingBottle(@Param("userid")int postuserid,@Param("secretkey")String secretkey, HttpServletRequest request) {
        try {
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("id", "=", postuserid).and("secretkey", "=", secretkey)).isEmpty();
            if(!res) {

                //未完成
                re.put("statues", 1);
                re.put("msg", "待定");
            }
            else{
                re.put("statues", 0);
                re.put("msg", "请登录");
            }
            return re;
        } catch (Exception e) {
            log.info(e);
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error finding password");
            return re;
        }

    }
}
