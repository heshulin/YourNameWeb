package com.iheshulin.yourname.controller;

import com.iheshulin.yourname.util.MD5;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * Created by HeShulin on 2017/5/31.
 */
public class DriftingBottleController {
    private Log log = Logs.get();
    private MD5 md5=MD5.getMd5();


    @Inject
    Dao dao;
    //发送漂流瓶


}
