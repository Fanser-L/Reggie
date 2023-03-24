package com.fanser.reggie.controller;

import com.fanser.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.basePath}")
    private String basePath;

    /**
     *文件上传
     * @param file 注意这个变量名不是随便取的，要跟前端的文件上传框的name保持一致
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会被删除
        log.info(file.toString());

        //1.原始文件名
        String originalFilename = file.getOriginalFilename();;
        //获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //2.使用UUID重新生成文件名，防止文件名重复造成文件覆盖
        String fileName = UUID.randomUUID()+suffix;

        //当目录不存在时，创建对应的文件夹
        File dir = new File(basePath);
        //判断当前目录是否存在，如果不存在，则创建文件
        if (!dir.exists()){
            dir.mkdirs();
        }
        try {
//            file.transferTo(new File("classpath:static/hello.jpg"));
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，通过输入流获取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));

            //输出流，通过文件输出流将文件写回浏览器，在浏览器显示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");
            response.addHeader("Access-Control-Allow-Origin","*");

            //经典的缓冲字节数组读取流文件
            int len = 0;
            byte[] bytes = new byte[1024];
            //特别注意，如果没有读取到bytes，就没读入这个文件输入流，会导致莫名其妙的IO异常
            //（java.io.IOException: 你的主机中的软件中止了一个已建立的连接。）
            while((len = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //关闭资源，自下而上
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
