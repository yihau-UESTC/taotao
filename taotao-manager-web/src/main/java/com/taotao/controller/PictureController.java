package com.taotao.controller;

import com.taotao.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PictureController {

    @Value("${IMAGE_SERVER}")
    private String IMAGE_SERVER;

    @ResponseBody
    @RequestMapping("/pic/upload")
    public Map picUpload(MultipartFile uploadFile){
        try {
            //1.接收上传文件
            String originalFilename = uploadFile.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/client.conf");
            String url = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
            url = IMAGE_SERVER + url;
            Map result = new HashMap();
            result.put("error", 0);
            result.put("url", url);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Map result = new HashMap();
            result.put("error", 1);
            result.put("message", "图片上传失败");
            return result;
        }

    }
}
