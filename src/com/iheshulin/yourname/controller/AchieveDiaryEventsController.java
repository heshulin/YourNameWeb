package com.iheshulin.yourname.controller;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LC on 2017/5/28.
 */

@IocBean
public class AchieveDiaryEventsController {
    private Log log = Logs.get();

    @Inject
    Dao dao;

    @Ok("json")
    @Fail("http:500")
    @At("achieve_diary_events/do_achieve_history_diary_events")
    @GET
    public Object doAchieveHistoryDiaryEvents(@Param("userid")Integer userId, @Param("secretkey")String secretKey){
        try{
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("id", "=", userId).and("secretkey", "=", secretKey)).isEmpty();
            if(!res) {
                List<List> data = new ArrayList<List>();
                List<Diary> list = (dao.query(Diary.class, Cnd.where("userid","=",userId).desc("contenttime")));
                for (int i=0;i<list.size();i++) {
                    String preDate=GetDatetime.dateToString(list.get(i).getContenttime(),"yyyy-MM-dd");
                    String date = GetDatetime.dateToString(list.get(i).getContenttime(),"yyyy-MM-dd");
                    List<Diary> tempList = new ArrayList<Diary>();
                    while(date.equals(preDate)&&i<list.size()-1){
                        tempList.add(list.get(i));
                        i++;
                        date = GetDatetime.dateToString(list.get(i).getContenttime(),"yyyy-MM-dd");
                        if(i==list.size()-1&&date.equals(preDate))
                            tempList.add(list.get(i));
                    }
                    //System.out.println(tempList);
                    data.add(tempList);
                    //System.out.println(data);
                    if(i>=list.size()-1)
                        break;
                    i--;
                }
                re.put("statues", 1);
                re.put("msg", "OK");
                re.put("datalist", data);
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
            re.put("msg", "error in do_achieve_history_diary_events");
            return re;
        }
    }


    @Ok("json")
    @Fail("http:500")
    @At("achieve_diary_events/do_achieve_today_diary_events")
    @GET
    public Object doAchieveTodayDiaryEvents(@Param("userid")Integer userId, @Param("secretkey")String secretKey){
        try {
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("id", "=", userId).and("secretkey", "=", secretKey)).isEmpty();
            if (!res) {
                NutMap data = new NutMap();
                List<Diary> diaryList = (dao.query(Diary.class, Cnd.where("userid","=",userId).and("date_format(contenttime,'%Y-%m-%d')", "=", GetDatetime.getNowString("yyyy-MM-dd")).desc("contenttime")));
                if(diaryList.size()>0){
                    re.put("statues", 1);
                    re.put("msg", "OK");
                    re.put("datalist",diaryList);
                    return re;
                }else{
                    re.put("statues", 0);
                    re.put("msg", "今天没有记录");
                    return re;
                }

            } else {
                re.put("statues", 0);
                re.put("msg", "请登录");
                return re;
            }
        }catch (Exception e){
            log.info(e);
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error in do_achieve_today_diary_events");
            return re;
        }
    }
}
