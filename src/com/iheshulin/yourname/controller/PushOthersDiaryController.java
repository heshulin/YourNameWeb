package com.iheshulin.yourname.controller;

/**
 * Created by LC on 2017/5/29.
 */

import com.iheshulin.yourname.bean.Diary;
import com.iheshulin.yourname.bean.ObtainDiary;
import com.iheshulin.yourname.bean.User;
import com.iheshulin.yourname.util.GetDatetime;
import com.iheshulin.yourname.util.MatchingAnalysisTool;
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

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@IocBean
public class PushOthersDiaryController{
    public String sThreeDaysAgo;
    public Integer randUserId = null;
    public Double complishDgree = 0.00;
    /*
    private class MDiary{
        private String content;
        private Date contenttime;

    } */
    private Log log = Logs.get();
    private final Double MIN_SIMILARITY_DIGREE = 0.60;//最小相似度设定
    /*
    *获取最近三天，随机某个用户的一篇手账
    */
    @Inject
    Dao dao;
    private NutMap getRandUserIdInThreeDays() throws Exception{
        /*
        *生成1-3内的随机数，选取三天内的某一天，因为三天时间内大环境变化不会很大，所以选取三天
         */
        Random rand = new Random();
        Integer chosenDay = rand.nextInt(3)+1;

        //获取chosenDay天之前的日期
        Date now = GetDatetime.getNow();
        Date threeDaysAgo = new Date(now.getTime() - chosenDay * 24 * 60 * 60 * 1000);
        this.sThreeDaysAgo = GetDatetime.dateToString(threeDaysAgo,"yyyy-MM-dd");
        Date dThreeDaysAgo = GetDatetime.stringToDate(this.sThreeDaysAgo,"yyyy-MM-dd");
        Date nextDay = new Date(dThreeDaysAgo.getTime() + 1 * 24 * 60 * 60 * 1000);

        //自定义sql
        String sqlCommand = "select distinct(userid) userId from diary where date_format(contenttime,'%Y-%m-%d')=@sThreeDaysAgo";
        Sql sql = Sqls.fetchRecord(sqlCommand);
        sql.params().set("sThreeDaysAgo",this.sThreeDaysAgo);
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

        if(list.size()==0)
            return null;
        if(list.size()==1){
            this.randUserId = list.get(0);
        }else {
             Integer randUserIdIndex = rand.nextInt(list.size());
             this.randUserId = list.get(randUserIdIndex);
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

        //List<Diary> dataList= dao.query(Diary.class,Cnd.where("contenttime",">=",dThreeDaysAgo).and("userid","<=",this.randUserId).and("contenttime","<",nextDay));
        NutMap res = new NutMap();
        res.put("randUserId",this.randUserId);
        res.put("choisenDay",this.sThreeDaysAgo);
        res.put("datalist",dataList);
        return res;
    }

    private List<Double> matchingAnalysis(List<Diary> taskDiaryEventsList, List<Diary> myDiaryEventsList) throws Exception{

        /*
        对两个diary的list进行事件内容和图片相似度分析，返回每一条event的完成情况,当完成度大于60%认定完成event。
         */
        Double comlished = 0.00;
        List<Double> resultList = new ArrayList<Double>();
        for(int i=0;i<taskDiaryEventsList.size();i++)
            resultList.add(0.00);
        for(int i=0;i<myDiaryEventsList.size();i++){
            for(int j=0;j<taskDiaryEventsList.size();j++){
                MatchingAnalysisTool matchingAnalysisTool = new MatchingAnalysisTool(myDiaryEventsList.get(i).getContent(),taskDiaryEventsList.get(j).getContent());
                Double temp = matchingAnalysisTool.similarityDegree();
                if(temp>MIN_SIMILARITY_DIGREE){
                    comlished++;
                    resultList.set(j,temp);
                    break;
                }
            }
        }
        complishDgree = comlished/(double)taskDiaryEventsList.size();
        return resultList;
    }


    @Ok("json")
    @Fail("http:500")
    @At("others_diary/do_push_others_diary_to_user")
    @GET
    public Object doPushOthersDiaryToUser(@Param("userid")Integer userId, @Param("secretkey")String secretKey){
        try{
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("id", "=", userId).and("secretkey", "=", secretKey)).isEmpty();
            if(!res) {
                //System.out.println();
                NutMap result = this.getRandUserIdInThreeDays();
                if(result!=null) {
                    re.put("statues", 1);
                    re.put("msg", "OK");
                    re.put("data", result);
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
            re.put("msg", "error in others_diary/do_push_others_diary_to_user");
            return re;
        }
    }

    @Ok("json")
    @Fail("http:500")
    @At("others_diary/do_choice_others_diary")
    @POST
    public Object doChoiceOthersDiary(@Param("userid")Integer userId, @Param("secretkey")String secretKey,
                                      @Param("randuserid") Integer randUserId, @Param("choisenday") String choisenDay){
        try{
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("id", "=", userId).and("secretkey", "=", secretKey)).isEmpty();
            if(!res) {
                ObtainDiary obtainDiary = new ObtainDiary();
                obtainDiary.setUserid(userId);
                obtainDiary.setCompletion(0);
                obtainDiary.setOtheruserid(randUserId);
                obtainDiary.setDiarytime(GetDatetime.stringToDate(choisenDay,"yyyy-MM-dd"));
                obtainDiary.setObtaintime(GetDatetime.getNow());
                dao.insert(obtainDiary);
                re.put("statues", 1);
                re.put("msg", "OK");
                return re;
            }else{
                re.put("statues", 0);
                re.put("msg", "请登录");
                return re;
            }
        }catch (Exception e){
            log.info(e);
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error in others_diary/do_choice_others_diary");
            return re;
        }
    }

    @Ok("json")
    @Fail("http:500")
    @At("others_diary/do_display_others_diary")
    @GET
    public Object doDisplayOthersDiary(@Param("userid")Integer userId, @Param("secretkey")String secretKey,
                                       @Param("randuserid") Integer randUserId, @Param("choisenday") String choisenDay){
        try {
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("id", "=", userId).and("secretkey", "=", secretKey)).isEmpty();
            if (!res) {
                //获得任务diary和本人diary，进行相似度分析
                List<Diary> taskDiaryEventsList = dao.query(Diary.class, Cnd.where("userid","=",randUserId)
                        .and("date_format(contenttime,'%Y-%m-%d')","=",choisenDay));
                List<Diary> myDiaryEventsList = dao.query(Diary.class, Cnd.where("userid","=",userId)
                        .and("date_format(contenttime,'%Y-%m-%d')","=",GetDatetime.getNowString("yyyy-MM-dd")));
                //相似度分析
                List<Double> compareResult = this.matchingAnalysis(taskDiaryEventsList,myDiaryEventsList);

                re.put("statues", 1);
                re.put("msg", "OK");
                re.put("compareresultlist", compareResult);
                re.put("taskdiary",taskDiaryEventsList);
                re.put("completiondegree",complishDgree);
                return re;
            } else {
                re.put("statues", 0);
                re.put("msg", "请登录");
                return re;
            }
        }catch (Exception e){
            log.info(e);
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error in others_diary/do_display_others_diary");
            return re;
        }
    }
}
