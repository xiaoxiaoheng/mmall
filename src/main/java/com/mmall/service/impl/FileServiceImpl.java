package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
@Slf4j
public class FileServiceImpl implements IFileService{


    @Override
    public String upload(MultipartFile file, String tempPath) {
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID() + "." + fileExtensionName;
        log.info("开始上传文件，上传文件的文件名，{},上传的路径:{},新文件名:{}" , fileName , tempPath , uploadFileName);

        File fileDir = new File(tempPath);
        if(!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(tempPath , uploadFileName);
        try {
            // 将文件上传到tomcat服务器,从内存写入磁盘
            file.transferTo(targetFile);

            // 上传到ftp服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            // 删除临时目录的文件
            targetFile.delete();
        } catch (IOException e) {
            log.error("上传文件异常" , e);
            return null;
        }
        return targetFile.getName();
    }
}
