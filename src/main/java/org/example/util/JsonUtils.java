package org.example.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * @descriptions: 解析json工具类
 * @author: zhangfaquan
 * @date: 2021/7/28 11:03
 * @version: 1.0
 */
public class JsonUtils {

    private JsonUtils() {
    }

    /**
     * @descriptions 对象转json字符串
     * @param value 需要转换为json的对象
     * @return
     */
    public static <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class || clazz == long.class || clazz == Long.class) {
            return String.valueOf(value);
        } else if (clazz == String.class) {
            return (String) value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    /**
     * @descriptions 解析简单对象
     * @param str   json字符串
     * @param clazz 需要转换成的对象类型
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            return JSON.parseObject(str, clazz);
        }
    }

    /**
     * @descriptions 解析复杂对象
     * @param str json字符串
     * @param tr  描述复杂泛型的工具类
     * @return
     */
    public static <T> T stringToBean(String str, TypeReference<T> tr) {
        if (str == null || str.length() <= 0 || tr == null) {
            return null;
        }
        return JSON.parseObject(str, tr);
    }
}
