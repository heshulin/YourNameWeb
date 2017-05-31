package com.iheshulin.yourname.controller;

import com.iheshulin.yourname.bean.BottleSetting;
import com.iheshulin.yourname.bean.CheckCode;
import com.iheshulin.yourname.bean.DriftingBottle;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by HeShulin on 2017/5/31.
 */
public class DriftingBottleController {
    private Log log = Logs.get();
    private MD5 md5=MD5.getMd5();


    @Inject
    Dao dao;


    //发送漂流瓶
    @Ok("json")
    @Fail("http:500")
    @At("pushdriftingbottle")
    @AdaptBy(type = UploadAdaptor.class, args = { "${app.root}/WEB-INF/tmp" })
    @POST
    public Object pushDriftingBottle(@Param("userid")int postuserid,@Param("receiveuserid")int receiveuserid,@Param("bottlecontent")String bottlecontent, @Param("secretkey")String secretkey, HttpServletRequest request) {
        try {
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("userid", "=", postuserid).and("secretkey", "=", secretkey)).isEmpty();
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
                        dao.update(driftingBottle1);
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
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error finding password");
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
            boolean res = dao.query(User.class, Cnd.where("userid", "=", postuserid).and("secretkey", "=", secretkey)).isEmpty();
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
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error finding password");
            return re;
        }

    }


}
