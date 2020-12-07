package com.example02.demo02.controller;


import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * src_type为0时对应的图片上传和获取的接口
 */
@RestController
public class ImgController {

    private String filePath = "C:/upload";

    @PostMapping("/image/upload")
    public JSONObject upLoad(@RequestParam("upload")MultipartFile upload, @RequestParam("user_id") String userId){
        JSONObject res = new JSONObject();

        //判断文件夹是否存在,不存在则创建
        File file = new File(filePath);

        if (!file.exists()){
            file.mkdirs();
        }
        String originalFileName = upload.getOriginalFilename();//获取原始图片的扩展名
        System.out.println(originalFileName);
        String newFileName = UUID.randomUUID()+originalFileName;
        System.out.println(newFileName);
        String newFilePath=filePath+originalFileName; //新文件的路径
        System.out.println(newFilePath);

        try {

            upload.transferTo(new File(newFilePath));//将传来的文件写入新建的文件

            System.out.println("上传图片成功!");

            //返回信息
            res.put("status","200");
            res.put("message","文件上传成功！");
            res.put("fileName",originalFileName);
            res.put("filePath",newFilePath);


        }catch (IllegalStateException e ) {
            //处理异常
            e.printStackTrace();
            res.put("status","500");
            res.put("message","文件上传失败！");
            res.put("fileName",originalFileName);
            res.put("filePath",newFilePath);
        }catch(IOException e1){
            //处理异常
            e1.printStackTrace();
            res.put("status","500");
            res.put("message","文件上传失败！");
            res.put("fileName",originalFileName);
            res.put("filePath",newFilePath);
        }

        return res;
    }

    /**
     * 根据服务器端的文件名显示图片
     * @param filename
     * @param response
     */
    @GetMapping("/image/{filename}")
    public void getImage(@PathVariable String filename, HttpServletResponse response) {
        System.out.println("开始加载图片...");
        File file = null;
        FileInputStream fis = null;
        String path = "C:/upload/" + filename + ".png";

        try {
            file = new File(path);
            if (!file.exists()) {
                System.out.println(path);
                return;
            }

            fis = new FileInputStream(file);
            final byte[] buf = new byte[1024];
            while (fis.read(buf) > 0) {
                response.getOutputStream().write(buf);
            }
            response.getOutputStream().flush();
            System.out.println("加载完毕");
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            System.out.println("加载出错");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (final IOException e) {
                    fis = null;
                }
            }
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            System.out.println("finally 加载完毕");
        }
    }

}
