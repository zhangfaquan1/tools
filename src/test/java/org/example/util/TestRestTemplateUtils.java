package org.example.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.example.util.parse.JsonUtils;
import org.example.util.remote.RestTemplateUtils;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class TestRestTemplateUtils {

    RestTemplate restTemplate = new RestTemplate();

    static final String url = "http://10.1.4.101:9095/common/pushTransaction";

    @Test
    public void test() {
        Map<String, Object> map = new HashMap<>();
        map.put("contractCode", "datacheck");
        map.put("action", "add");
        map.put("actor", "datacheck");
        Map<String, Object> args = new HashMap<>();
        args.put("data_id", 24);
        args.put("group_id", 10);
        args.put("compare_groups", "A,B");
        args.put("encrypt_data", "ASDG");
//        args.put("group_num", 2);
        map.put("args", args);

        RestTemplateUtils.sendRequest(restTemplate, HttpMethod.POST, url, map,
                this::parseResponse
                , processedResponse -> "500".equals(processedResponse.get("code")), 2
        );
    }

    @Test
    public void test2() {
        Map<String, Object> map = new HashMap<>();
        map.put("contractCode", "datacheck");
        map.put("action", "add");
        map.put("actor", "datacheck");
        Map<String, Object> args = new HashMap<>();
        args.put("data_id", 24);
        args.put("group_id", 10);
        args.put("compare_groups", "A,B");
        args.put("encrypt_data", "ASDG");
//        args.put("group_num", 2);
        map.put("args", args);

        RestTemplateUtils.sendRequest(restTemplate, HttpMethod.POST, url, map,
                this::parseResponse, 2
        );
    }

    @Test
    public void test3() {
        // {"chainId":"e703e2017e02e728dff5df189b2cf1beab9120ec9b69ef4d4de3551aea10df3c","blockNum":8200204}
        // {"error":{"code":1210099,"message":"{\"code\":500,\"message\":\"Internal Service Error\",\"error\":{\"code\":3015014,\"name\":\"pack_exception\",\"what\":\"Pack data org.example.exception\",\"details\":[{\"message\":\"Missing field 'group_num' in input object while processing struct 'add'\",\"file\":\"abi_serializer.cpp\",\"line_number\":469,\"method\":\"_variant_to_binary\"},{\"message\":\"\",\"file\":\"abi_serializer.cpp\",\"line_number\":496,\"method\":\"_variant_to_binary\"},{\"message\":\"\",\"file\":\"abi_serializer.cpp\",\"line_number\":510,\"method\":\"_variant_to_binary\"},{\"message\":\"'{\\\"data_id\\\":24,\\\"group_id\\\":10,\\\"compare_groups\\\":\\\"A,B\\\",\\\"encrypt_data\\\":\\\"ASDG\\\"}' is invalid args for action 'add' code 'datacheck'. expected '[{\\\"name\\\":\\\"data_id\\\",\\\"type\\\":\\\"uint64\\\"},{\\\"name\\\":\\\"group_id\\\",\\\"type\\\":\\\"uint32\\\"},{\\\"name\\\":\\\"encrypt_data\\\",\\\"type\\\":\\\"string\\\"},{\\\"name\\\":\\\"compare_groups\\\",\\\"type\\\":\\\"string\\\"},{\\\"name\\\":\\\"group_num\\\",\\\"type\\\":\\\"uint32\\\"}]'\",\"file\":\"chain_plugin.cpp\",\"line_number\":2040,\"method\":\"abi_json_to_bin\"},{\"message\":\"code: datacheck, action: add, args: {\\\"data_id\\\":24,\\\"group_id\\\":10,\\\"compare_groups\\\":\\\"A,B\\\",\\\"encrypt_data\\\":\\\"ASDG\\\"}\",\"file\":\"chain_plugin.cpp\",\"line_number\":2046,\"method\":\"abi_json_to_bin\"}]}}"},"result":false}
        String response = "{\"error\":{\"code\":1210099,\"message\":\"{\\\"code\\\":500,\\\"message\\\":\\\"Internal Service Error\\\",\\\"error\\\":{\\\"code\\\":3015014,\\\"name\\\":\\\"pack_exception\\\",\\\"what\\\":\\\"Pack data org.example.exception\\\",\\\"details\\\":[{\\\"message\\\":\\\"Missing field 'group_num' in input object while processing struct 'add'\\\",\\\"file\\\":\\\"abi_serializer.cpp\\\",\\\"line_number\\\":469,\\\"method\\\":\\\"_variant_to_binary\\\"},{\\\"message\\\":\\\"\\\",\\\"file\\\":\\\"abi_serializer.cpp\\\",\\\"line_number\\\":496,\\\"method\\\":\\\"_variant_to_binary\\\"},{\\\"message\\\":\\\"\\\",\\\"file\\\":\\\"abi_serializer.cpp\\\",\\\"line_number\\\":510,\\\"method\\\":\\\"_variant_to_binary\\\"},{\\\"message\\\":\\\"'{\\\\\\\"data_id\\\\\\\":24,\\\\\\\"group_id\\\\\\\":10,\\\\\\\"compare_groups\\\\\\\":\\\\\\\"A,B\\\\\\\",\\\\\\\"encrypt_data\\\\\\\":\\\\\\\"ASDG\\\\\\\"}' is invalid args for action 'add' code 'datacheck'. expected '[{\\\\\\\"name\\\\\\\":\\\\\\\"data_id\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"uint64\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"group_id\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"uint32\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"encrypt_data\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"string\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"compare_groups\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"string\\\\\\\"},{\\\\\\\"name\\\\\\\":\\\\\\\"group_num\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"uint32\\\\\\\"}]'\\\",\\\"file\\\":\\\"chain_plugin.cpp\\\",\\\"line_number\\\":2040,\\\"method\\\":\\\"abi_json_to_bin\\\"},{\\\"message\\\":\\\"code: datacheck, action: add, args: {\\\\\\\"data_id\\\\\\\":24,\\\\\\\"group_id\\\\\\\":10,\\\\\\\"compare_groups\\\\\\\":\\\\\\\"A,B\\\\\\\",\\\\\\\"encrypt_data\\\\\\\":\\\\\\\"ASDG\\\\\\\"}\\\",\\\"file\\\":\\\"chain_plugin.cpp\\\",\\\"line_number\\\":2046,\\\"method\\\":\\\"abi_json_to_bin\\\"}]}}\"},\"result\":false}";
        System.out.println(response);

        com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(response);
        System.out.println(jsonObject1);

        JSONObject error = JsonUtils.stringToBean(JsonUtils.beanToString(jsonObject1.get("error")), JSONObject.class);
        System.out.println(error);

        JSONObject message = JsonUtils.stringToBean(JsonUtils.beanToString(error.get("message")), JSONObject.class);
        System.out.println(message);

        String code = message.getString("code");
        System.out.println(code);
    }

    private Map<String, Object> parseResponse(String response) {
        Map<String, Object> map = parseSuccessResponse(response);
        if (map != null && !StringUtils.isBlank((String) map.get("chainId")))
            return map;
        return parseErrResponse(response);
    }

    private Map<String, Object> parseSuccessResponse(String response) {
        return JsonUtils.stringToBean(response, new TypeReference<Map<String, Object>>() {
        });
    }

    private Map<String, Object> parseErrResponse(String response) {
        com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(response);
        JSONObject error = JsonUtils.stringToBean(JsonUtils.beanToString(jsonObject1.get("error")), JSONObject.class);
        if (error == null)
            return null;
        JSONObject message = JsonUtils.stringToBean(JsonUtils.beanToString(error.get("message")), JSONObject.class);
        if (message == null)
            return null;
        String code = message.getString("code");
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        return map;
    }
}
