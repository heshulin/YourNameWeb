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
    @At("do_achieve_diary_events")
    @GET
    public Object doAchieveDiaryEvents(@Param("userid")Integer userId, @Param("secretkey")String secretKey){
        try{
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("id", "=", userId).and("secretkey", "=", secretKey)).isEmpty();
            if(!res) {
                NutMap data = new NutMap();
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
                    data.put(preDate,tempList);
                    //System.out.println(data);
                    if(i>=list.size()-1)
                        break;
                    i--;
                }
                re.put("statues", 1);
                re.put("msg", "OK");
                re.put("data", data);
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
            re.put("msg", "error in do_achieve_diary_events");
            return re;
        }
    }
}
