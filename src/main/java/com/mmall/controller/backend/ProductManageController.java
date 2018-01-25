package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    IProductService iProductService;

    @Autowired
    IFileService iFileService;

    @Autowired
    IUserService iUserService;

    @RequestMapping(value = "list.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageSize" , defaultValue = "10") Integer pageSize ,
                                         @RequestParam(value = "pageNum" , defaultValue = "1") Integer pageNum ,
                                         HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , "用户未登陆，请登陆");
        }
        if(iUserService.checkAdminRole(user.getId()) != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
        return iProductService.list(pageNum , pageSize);
    }

    @RequestMapping(value = "search.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse search(@RequestParam(value = "pageSize" , defaultValue = "10") Integer pageSize ,
                               @RequestParam(value = "pageNum" , defaultValue = "1") Integer pageNum ,
                                 String productName ,
                                 Integer productId ,
                                 HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , "用户未登陆，请登陆");
        }
        if(iUserService.checkAdminRole(user.getId()) != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
        return iProductService.search(pageNum , pageSize , productId , productName);
    }

    @RequestMapping(value = "upload.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(@RequestParam(value = "upload_file" , required = false) MultipartFile file ,
                                 HttpSession session ,
                                 HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , "用户未登陆，请登陆");
        }
        if(iUserService.checkAdminRole(user.getId()) != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
        // 临时上传文件目录
        String tempPath = request.getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file , tempPath);

        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
        Map fileMap = Maps.newHashMap();
        fileMap.put("url" , url); // 完整资源
        fileMap.put("uri" , targetFileName); // 资源名字
        return ServerResponse.createBySuccess(fileMap);
    }

    @RequestMapping(value = "richtext_img_upload.do" , method = RequestMethod.POST)
    @ResponseBody
    public Map<String , Object> richtextImgUpload(@RequestParam(value = "upload_file" , required = false) MultipartFile file ,
                                                  HttpSession session ,
                                                  HttpServletRequest request ,
                                                  HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "请登录管理员");
            return resultMap;
        }
        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        if (iUserService.checkAdminRole(user.getId()) == Const.Role.ROLE_ADMIN) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            if (StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            return resultMap;
        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限操作");
            return resultMap;
        }
    }

    @RequestMapping(value = "detail.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(HttpSession session , Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , "用户未登陆，请登陆");
        }
        if(iUserService.checkAdminRole(user.getId()) != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
        ServerResponse<ProductDetailVo> response = iProductService.manageDetail(productId);
        return response;
    }

    @RequestMapping(value = "set_sale_status.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> setSaleStatus(HttpSession session , Integer productId , Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , "用户未登陆，请登陆");
        }
        if(iUserService.checkAdminRole(user.getId()) != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
        ServerResponse<String> response = iProductService.updateSaleStatus(productId , status);
        return response;
    }

    // TODO: 2018/1/13 暂时开放get接口，方便测试 
    @RequestMapping(value = "save.do" , method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> save(HttpSession session , Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode() , "用户未登陆，请登陆");
        }
        if(iUserService.checkAdminRole(user.getId()) != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
        ServerResponse<String> response = iProductService.updateOrSave(product);
        return response;
    }
}
