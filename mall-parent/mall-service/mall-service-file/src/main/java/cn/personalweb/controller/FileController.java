package cn.personalweb.controller;

import cn.personalweb.file.FastDFSFile;
import cn.personalweb.util.FastDFSUtil;
import entry.Result;
import entry.StatusCode;
import org.csource.common.MyException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping(value = "/upload")
@CrossOrigin
public class FileController {


    @PostMapping
    public Result upload(@RequestParam(value = "file") MultipartFile file) throws IOException, MyException {
        // 封装文件信息
        FastDFSFile fastDFSFile = new FastDFSFile(file.getOriginalFilename(),
                file.getBytes(),
                StringUtils.getFilenameExtension(file.getOriginalFilename()));
        String[] upload = FastDFSUtil.upload(fastDFSFile);
        // 拼接访问地址 `http://192.168.211.132:8080/group1/M00/00/00/wKjThF0DBzaAP23MAAXz2mMp9oM26.jpeg`
        String url = "http://192.168.72.129:8080/" + upload[0] + "/" + upload[1];
        return new Result(true, StatusCode.OK, "上传成功", url);
    }
}
