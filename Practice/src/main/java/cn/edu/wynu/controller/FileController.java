package cn.edu.wynu.controller;

import cn.edu.wynu.common.AjaxResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class FileController {

    @Value("${server.port:8080}")
    private String serverPort;

    @PostMapping("/image")
    public AjaxResult uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return AjaxResult.error("上传文件不能为空！");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return AjaxResult.error("文件名不能为空！");
        }

        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!suffix.matches("\\.(jpg|jpeg|png|gif)$")) {
            return AjaxResult.error("只能上传图片文件！");
        }

        try {
            String datePath = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String saveDir = System.getProperty("user.dir") + "/uploads/" + datePath;
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String newFilename = UUID.randomUUID().toString() + suffix;
            Path filePath = Paths.get(saveDir, newFilename);
            Files.write(filePath, file.getBytes());

            String url = "http://localhost:" + serverPort + "/uploads/" + datePath + "/" + newFilename;

            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", newFilename);

            return AjaxResult.success("上传成功", result);
        } catch (IOException e) {
            return AjaxResult.error("上传失败：" + e.getMessage());
        }
    }
}
