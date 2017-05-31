package com.iheshulin.yourname.controller;
/**
 * Created by LC on 2017/5/28.
 */
import com.iheshulin.Filter.YournameFilter;
import com.iheshulin.yourname.bean.*;
import com.iheshulin.yourname.util.*;
import okhttp3.Connection;
import org.apache.jasper.tagplugins.jstl.core.Url;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.img.Images;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class PushDiaryEventController {
    private Log log = Logs.get();
    private UploadFile uploadFile;
    public Double complishDgree = 0.00;
    private final Double MIN_SIMILARITY_DIGREE = 0.00;
    @Inject
    Dao dao;


    private Double getComplishDegree(Integer userId, String secretKey, Integer randUserId, Date choisenDay) throws Exception{
        Double complished = 0.00;

        List<Diary> taskDiaryEventsList = dao.query(Diary.class, Cnd.where("userid","=",randUserId)
                .and("date_format(contenttime,'%Y-%m-%d')","=",choisenDay));
        List<Diary> myDiaryEventsList = dao.query(Diary.class, Cnd.where("userid","=",userId)
                .and("date_format(contenttime,'%Y-%m-%d')","=",GetDatetime.getNowString("yyyy-MM-dd")));



        for(int i=0;i<myDiaryEventsList.size();i++){
            for(int j=0;j<taskDiaryEventsList.size();j++){
                MatchingAnalysisTool matchingAnalysisTool = new MatchingAnalysisTool(myDiaryEventsList.get(i).getContent(),taskDiaryEventsList.get(j).getContent());
                Double temp = matchingAnalysisTool.similarityDegree();
                if(temp>MIN_SIMILARITY_DIGREE){
                    complished++;
                    break;
                }
            }
        }
        return complished/(double)taskDiaryEventsList.size();

    }
    @Ok("json")
    @Fail("http:500")
    @At("do_push_diary_event")
    @POST
    public Object doPushDiaryEvent(@Param("userid")Integer userId, @Param("secretkey")String secretKey,
                                   @Param("content")String content, @Param("location")String location,
                                   @Param("fileurl")String fileUrl){
        try{
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("id", "=", userId).and("secretkey", "=", secretKey)).isEmpty();
            if(!res) {
                if (content != null && location != null) {
                    if(fileUrl==null){
                        GetPhoto getPhoto = new GetPhoto();
//                        System.out.println(location);
//                        System.out.println(location);

                        String savaPath = "c:\\image\\";
                        String fileName = SecretKey.getSecretKey() + ".png";
                        String filePath = getPhoto.getWebPhoto(location, savaPath, fileName);
                        this.uploadFile = new UploadFile();
                        fileUrl = uploadFile.uploadDiaryPhoto(filePath);
                        if(fileUrl==null){
                            re.put("statues", 0);
                            re.put("msg", "图片获取失败");
                            return re;
                        }
                    }

                    Diary diary = new Diary();
                    diary.setUserid(userId);
                    diary.setContent(content);
                    diary.setContenttime(new Date());
                    diary.setLocation(location);
                    diary.setContentphote(fileUrl);
                    dao.insert(diary);
                    re.put("statues", 1);
                    re.put("msg", "上传成功");
                    Date lastDay = new Date(GetDatetime.getNow().getTime() - 1 * 24 * 60 * 60 * 1000);
                    ObtainDiary obtainDiaryList = dao.fetch(ObtainDiary.class, Cnd.where("userid", "=", userId).and("obtaintime", ">=", lastDay));
                    if(obtainDiaryList!=null) {
                        Double complishDegree = this.getComplishDegree(userId, secretKey, obtainDiaryList.getOtheruserid(), obtainDiaryList.getDiarytime());
                        obtainDiaryList.setCompletion(complishDegree);
                        dao.update(obtainDiaryList);
                        BottleSetting bottleSetting = new BottleSetting();
                        bottleSetting.setPostuserid(userId);
                        bottleSetting.setReceiveuserid(obtainDiaryList.getOtheruserid());
                        bottleSetting.setEndtime(new Date(GetDatetime.getNow().getTime() + 1 * 24 * 60 * 60 * 1000));
                        BottleSetting bottleSetting1 = new BottleSetting();
                        bottleSetting1.setPostuserid(obtainDiaryList.getOtheruserid());
                        bottleSetting1.setReceiveuserid(userId);
                        bottleSetting1.setEndtime(new Date(GetDatetime.getNow().getTime() + 1 * 24 * 60 * 60 * 1000));
                        re.put("currentcomplishdegree", complishDegree);
                    }
                }
            }else{
                re.put("statues", 0);
                re.put("msg", "请登录");

            }
            return re;
        }catch (Exception e){
            log.info(e);
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error in do_push_diary_event");
            return re;
        }
    }


    @AdaptBy(type = UploadAdaptor.class, args = {"${app.root}/tmp"})
    @Ok("json")
    @Fail("http:500")
    @At("do_upload_dairy_photo")
    @POST
    public Object doUploadDiaryPhoto(@Param("userid")Integer userId, @Param("secretkey")String secretKey,
                                     @Param("filename")TempFile tempFile
                                     ){
        try{
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("userid", "=", userId).and("secretkey", "=", secretKey)).isEmpty();
            if(!res) {
                if (userId != null && secretKey != null && tempFile != null) {
                    File f = tempFile.getFile();
                    String filePath = f.getPath();
                    YournameFilter yournameFilter = new YournameFilter();
                    yournameFilter.pullPhoto(filePath);
                    filePath = yournameFilter.getPhotoUrl();
                    this.uploadFile = new UploadFile();
                    String fileUrl = uploadFile.uploadDiaryPhoto(filePath);
                    if (fileUrl != null) {
                        re.put("statues", 1);
                        re.put("msg", "上传成功");
                        re.put("fileurl", fileUrl);
                    } else {
                        re.put("statues", 0);
                        re.put("msg", "上传失败");
                    }
                } else {
                    re.put("statues", 0);
                    re.put("msg", "上传失败");
                }
            }else{
                re.put("statues", 0);
                re.put("msg", "请登录");
            }
            return re;
        }catch (Exception e){
            log.info(e);
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error in do_upload_dairy_photo");
            return re;
        }
    }

}
