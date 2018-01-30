package com.mmall.util;

import com.mmall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by cjq on 2018-01-30 15:11
 */
@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();
    static{
        //对象的所有字段全部列入，学习JsonSerialize.Inclusion四个枚举类型的含义
        // NON_NULL NON_DEFAULT NON_EMPTY
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);

        //取消默认转换timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);

        //忽略空Bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);

        //所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss ， 方便查看
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
        // 在json字符中有某个属性，而bean没有这个属性，忽略此错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static <T> String obj2String(T obj) {
        if(obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String)obj : objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("Parse Object to String error",e);
            return null;
        }
    }

    public static <T> String obj2StringPretty(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj :  objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to String error",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str , Class<T> clazz) {
        if(StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str , clazz);
        } catch (IOException e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str , TypeReference<T> typeReference) {
        if(StringUtils.isEmpty(str) || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str , typeReference));
        } catch (IOException e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str , Class<?> collectionClass , Class<?> ... elementClasss) {
        if(StringUtils.isEmpty(str)) {
            return null;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass , elementClasss);
        try {
            return objectMapper.readValue(str , javaType);
        } catch (IOException e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    public static void main(String[] args) {
        User user = new User();
        user.setId(2);
        user.setEmail("geely@happymmall.com");
        user.setCreateTime(new Date());
        String userJsonPretty = JsonUtil.obj2StringPretty(user);
        log.info("userJson:{}",userJsonPretty);

        User user1 = JsonUtil.string2Obj(userJsonPretty , User.class);
        System.out.println(user1.getEmail() + user1.getCreateTime());

        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user1);
        String userListStr = JsonUtil.obj2StringPretty(userList);
        log.info("userListStr:{}" , userListStr);

        userList = JsonUtil.string2Obj(userListStr, new TypeReference<List<User>>() {
        });
        userList = JsonUtil.string2Obj(userListStr , List.class , User.class);
        System.out.println("end");
    }
}
