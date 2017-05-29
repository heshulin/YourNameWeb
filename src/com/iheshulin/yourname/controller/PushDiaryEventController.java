package com.iheshulin.yourname.controller;
/**
 * Created by LC on 2017/5/28.
 */
import com.iheshulin.yourname.bean.User;
import com.iheshulin.yourname.bean.Diary;
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
import java.io.File;
import java.util.Date;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class PushDiaryEventController {
    private Log log = Logs.get();
    private UploadFile uploadFile;
    @Inject
    Dao dao;
    @Ok("json")
    @Fail("http:500")
    @At("do_push_diary_event")
    @POST
    public Object doPushDiaryEvent(@Param("userid")Integer userId, @Param("secretkey")String secretKey,
                                   @Param("content")String content, @Param("contenttime")Date contentTime,
                                   @Param("location")String location, @Param("fileurl")String fileUrl){
        try{
            NutMap re = new NutMap();
            boolean res = dao.query(User.class, Cnd.where("id", "=", userId).and("secretkey", "=", secretKey)).isEmpty();
            if(!res) {
                if (content != null && contentTime != null && location != null && fileUrl != null) {
                    Diary diary = new Diary();
                    diary.setUserid(userId);
                    diary.setContent(content);
                    diary.setContenttime(contentTime);
                    diary.setLocation(location);
                    diary.setContentphote(fileUrl);
                    dao.insert(diary);
                    re.put("statues", 1);
                    re.put("msg", "上传成功");
                }
            }else{
                re.put("statues", 0);
                re.put("msg", "请登录");
            }
        }catch (Exception e){
            log.info(e);
            NutMap re = new NutMap();
            re.put("statues", 0);
            re.put("msg", "error in do_upload_dairy_photo");
            return re;
        }
        return null;
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
