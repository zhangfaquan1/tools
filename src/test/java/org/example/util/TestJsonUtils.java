package org.example.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestJsonUtils {

    @Test
    public void testBeanToString() {
        Map<String, Object> data = createData();
        Map<String, String> map = new HashMap<>();
        data.forEach((key, value)->{
            String json = JsonUtils.beanToString(value);
            map.put(key, json);
        });
        String json = JsonUtils.beanToString(map);
        System.out.println(json);
    }

    /**
     * @descriptions 测试 public static <T> T stringToBean(String str, TypeReference<T> tr)
     * @param null
     * @return
     */
    @Test
    public void testStringToBean() {
        Map<String, Object> data = createData();
        String json = JsonUtils.beanToString(data);
        System.out.println(json);

        Map<String, Object> map = JsonUtils.stringToBean(json, new TypeReference<Map<String, Object>>() {
        });
        System.out.println(map);

        String table = (String) map.get("table");
        System.out.println(table);

        Map<String, Object> after = JsonUtils.stringToBean(JsonUtils.beanToString(map.get("after")), new TypeReference<Map<String, Object>>() {
        });
        System.out.println(after);
    }

    public Map<String, Object> createData() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> after = new HashMap<>();
        after.put("CUST_CODE", "WILL");
        after.put("ORDER_DATE", "1994-09-30:15:33:00");
        after.put("PRODUCT_CODE", "CAR");
        after.put("ORDER_ID", 144);
        after.put("PRODUCT_PRICE", 17520.00);
        after.put("PRODUCT_AMOUNT", 3);
        after.put("TRANSACTION_ID", 100);
        map.put("after", after);
        map.put("table", "QASOURCE.TCUSTORD");
        return map;
    }
}
