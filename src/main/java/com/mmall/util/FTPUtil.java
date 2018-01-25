package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {
    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    public FTPUtil(String ip , int port , String user , String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    public static boolean uploadFile(List<File> fileList) {
        FTPUtil ftpUtil = new FTPUtil(ftpIp , 21 , ftpUser , ftpPass);
        logger.info("开始连接服务器");
        boolean result = ftpUtil.uploadFile("img" , fileList);
        logger.info("结束上传，上传结果：{}" , result);
        return result;
    }

    private boolean uploadFile(String remotePath , List<File> fileList) {
        boolean uploaded = true;
        FileInputStream fis = null;
        if(connectionServer(ip , port , user ,pwd)) {
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                // 设置缓冲区
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                // 避免产生乱码问题
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                /*ftpClient.enterRemotePassiveMode();*/
                /*调用FTPClient.enterLocalPassiveMode();这个方法的意思就是每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据。为什么要这样做呢，因为ftp server可能每次开启不同的端口来传输数据，但是在linux上，由于安全限制，可能某些端口没有开启，所以就出现阻塞。*/
                ftpClient.enterLocalPassiveMode();
                for(File fileItem : fileList) {
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName() , fis);
                }
            } catch (IOException e) {
                uploaded = false;
                logger.error("上传文件异常" , e);
            } finally {
                if(fis != null) {
                    try {
                        fis.close();
                        ftpClient.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return uploaded;
    }

    private boolean connectionServer(String ip , int port , String user , String pwd) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user , pwd);
        } catch (IOException e) {
            logger.error("连接ftp服务器异常" , e);
        }
        return isSuccess;
    }

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
