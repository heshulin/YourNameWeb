package com.iheshulin.yourname.controller;

/**
 * Created by LC on 2017/5/29.
 */

import com.iheshulin.yourname.bean.Diary;
import com.iheshulin.yourname.bean.User;
import com.iheshulin.yourname.util.GetDatetime;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@IocBean
public class PushOthersDiaryController{
    /*
    private class MDiary{
        private String content;
        private Date contenttime;

    } */
    private Log log = Logs.get();
    /*
    *获取最近三天，随机某个用户的一篇手账
    */
    @Inject
    Dao dao;
    private List<Diary> getRandUserIdInThreeDays() throws Exception{
        /*
        *生成1-3内的随机数，选取三天内的某一天，因为三天时间内大环境变化不会很大，所以选取三天
         */
        Random rand = new Random();
        Integer chosenDay = rand.nextInt(3)+1;

        //获取chosenDay天之前的日期
        Date now = GetDatetime.GetNow();
        Date threeDaysAgo = new Date(now.getTime() - chosenDay * 24 * 60 * 60 * 1000);
        String sThreeDaysAgo = GetDatetime.dateToString(threeDaysAgo,"yyyy-MM-dd");
        Date dThreeDaysAgo = GetDatetime.stringToDate(sThreeDaysAgo,"yyyy-MM-dd");
        Date nextDay = new Date(dThreeDaysAgo.getTime() + 1 * 24 * 60 * 60 * 1000);

        //自定义sql
        String sqlCommand = "select distinct(userid) userId from diary where date_format(contenttime,'%Y-%m-%d')=@sThreeDaysAgo";
        Sql sql = Sqls.fetchRecord(sqlCommand);
        sql.params().set("sThreeDaysAgo",sThreeDaysAgo);
        sql.setCallback(new SqlCallback() {
            @Override
            public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {
                List<Integer> list = new LinkedList<Integer>();
                //System.out.println("111111111");
                while (resultSet.next()){
                    list.add(resultSet.getInt("userId"));
                }
                return list;
            }
        });
        dao.execute(sql);
        List<Integer> list = sql.getList(Integer.class);
        System.out.println(list);
        Integer randUserId = null;
        if(list.size()==0)
            return new LinkedList<Diary>();
        if(list.size()==1){
            randUserId = list.get(0);
        }else {
             Integer randUserIdIndex = rand.nextInt(list.size());
             randUserId = list.get(randUserIdIndex);
        }
//        System.out.println(randUserId);
//        System.out.println(sThreeDaysAgo);
//
//
//        System.out.println(dThreeDaysAgo);
//        System.out.println(nextDay);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(simpleDateFormat.format(dThreeDaysAgo));
        System.out.println(simpleDateFormat.format(nextDay));
        List<Diary> dataList= dao.query(Diary.class,Cnd.where("date_format(contenttime,'%Y-%m-%d')","=",sThreeDaysAgo).and("userid","<=",randUserId));

        //List<Diary> dataList= dao.query(Diary.class,Cnd.where("contenttime",">=",dThreeDaysAgo).and("userid","<=",randUserId).and("contenttime","<",nextDay));
        return dataList;
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
                //System.out.println();
                List<Diary> list = this.getRandUserIdInThreeDays();
                if(list.size()>0) {
                    re.put("statues", 1);
                    re.put("msg", "OK");
                    re.put("data", list);
                    return re;
                }else{
                    re.put("statues", 0);
                    re.put("msg", "未找到推送内容");
                    return re;
                }
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
