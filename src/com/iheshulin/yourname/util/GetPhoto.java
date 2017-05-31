package com.iheshulin.yourname.util;

import com.iheshulin.Filter.YournameFilter;
import com.iheshulin.baiduimage.Crawler;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by HeShulin on 2017/5/31.
 */
public class GetPhoto {
    public String getWebPhoto(String photoname) throws Exception {
        Crawler crawler = new Crawler();
        crawler.pullPhoto(photoname);
        String oldphotourl = crawler.getPhotourl();
        download(oldphotourl, "test.jpg","c:\\image\\");
        YournameFilter yournameFilter = new YournameFilter();
        try {
            yournameFilter.pullPhoto("c:\\image\\test.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return yournameFilter.getPhotoUrl();
    }
    public static void download(String urlString, String filename,String savePath) throws Exception {
        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        //设置请求超时为5s
        con.setConnectTimeout(5*1000);
        // 输入流
        InputStream is = con.getInputStream();

        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        File sf=new File(savePath);
        if(!sf.exists()){
            sf.mkdirs();
        }
        OutputStream os = new FileOutputStream(sf.getPath()+"\\"+filename);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        // 完毕，关闭所有链接
        os.close();
        is.close();
    }

}
