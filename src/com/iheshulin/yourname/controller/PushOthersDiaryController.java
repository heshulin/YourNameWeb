package com.iheshulin.yourname.controller;

/**
 * Created by LC on 2017/5/29.
 */

import com.iheshulin.yourname.bean.Diary;
import com.iheshulin.yourname.bean.User;
import com.iheshulin.yourname.util.GetDatetime;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import java.util.Date;
import java.util.Random;

@IocBean
public class PushOthersDiaryController{
    private Log log = Logs.get();

    /*
    *获取最近三天，随机某个用户的一篇手账
    */
    @Inject
    Dao dao;
    private Object getRandUserIdInThreeDays() throws Exception{
        /*
        *生成1-3内的随机数，选取三天内的某一天，因为三天时间内大环境变化不会很大，所以选取三天
         */
        Random rand = new Random();
        Integer chosenDay = rand.nextInt(3)+1;

        //获取三天之前的日期
        Date now = GetDatetime.GetNow();
        Date threeDaysAgo = new Date(now.getTime() - 3 * 24 * 60 * 60 * 1000);
        String sThreeDaysAgo = GetDatetime.dateToString(threeDaysAgo,"yyyy-MM-dd");
        Date dThreeDaysAgo = GetDatetime.stringToDate(sThreeDaysAgo,"yyyy-MM-dd");

        //未完成
        String sql = "select * from diary where ";
        return null;
    }


    @Ok("json")
    @Fail("http:500")
    @At("do_push_others_diary_to_user")
    @GET
    public Object doPushOthersDiaryToUser(@Param("userid")Integer userId, @Param("secretkey")String secretKey){
        try{
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("id", "=", userId).and("secretkey", "=", secretKey)).isEmpty();
            if(!res) {
                return 1;
            }else{
                re.put("statues", 0);
                re.put("msg", "请登录");
                return re;
            }
        }catch (Exception e){
            log.info(e);
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error in do_push_others_diary_to_user");
            return re;
        }
    }

}
