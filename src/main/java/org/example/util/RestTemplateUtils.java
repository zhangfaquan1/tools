package org.example.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @descriptions: 远程调用工具类
 * @author: zhangfaquan
 * @date: 2021/7/27 16:48
 * @version: 1.0
 */
public class RestTemplateUtils {

    private RestTemplateUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(RestTemplateUtils.class);

    public static String post(RestTemplate restTemplate, String url, List<Map<String, Object>> dataList) {
        ResponseEntity<String> forEntity = restTemplate.postForEntity(url, dataList, String.class);
        return forEntity.getBody();
    }

    public static String post(RestTemplate restTemplate, String url, Map<String, Object> data) {
        ResponseEntity<String> forEntity = restTemplate.postForEntity(url, data, String.class);
        return forEntity.getBody();
    }

    /**
     * @descriptions 发送请求
     * @param restTemplate http工具类
     * @param method 请求方式
     * @param url url
     * @param data 请求体
     * @param function 响应数据解析函数
     * @param predicate 判断是否继续重试的函数，true为继续重试，false为不继续重试
     * @param retryCount 重试次数
     * @return
     */
    public static Map<String, Object> sendRequest(RestTemplate restTemplate, HttpMethod method, String url, Map<String, Object> data, Function<String, Map<String, Object>> function, Predicate<Map<String, Object>> predicate, int retryCount) {

        // 获取响应体
        HttpEntity<Map<String,Object>> requestData =  new HttpEntity<>(data);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, method, requestData, String.class);
        if (logger.isDebugEnabled())
            logger.debug("请求：{}，的响应信息：{}", url, responseEntity.getBody());

        // 解析响应体数据
        Map<String, Object> processedResponse = function.apply(responseEntity.getBody());

        for (int i = 0; i < retryCount; i++) {

            // 判断是否继续重试。
            if (!predicate.test(processedResponse))
                break;
            responseEntity = restTemplate.exchange(url, method, requestData, String.class);
            processedResponse = function.apply(responseEntity.getBody());

            if (logger.isDebugEnabled())
                logger.debug("请求：{}，的响应信息：{}", url, responseEntity.getBody());
        }

        return processedResponse;
    }

    /**
     * @descriptions 404情况下无视retryCount，不断循环重试。
     * @param
     * @return
     */
    public static Map<String, Object> sendRequest(RestTemplate restTemplate, HttpMethod method, String url, Map<String, Object> data, Function<String, Map<String, Object>> function, int retryCount) {
        // 获取响应体
        HttpEntity<Map<String,Object>> requestData =  new HttpEntity<>(data);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, method, requestData, String.class);
        if (logger.isDebugEnabled())
            logger.debug("请求：{}，的响应信息：{}", url, responseEntity.getBody());

        // 解析响应体数据
        Map<String, Object> processedResponse = function.apply(responseEntity.getBody());

        boolean flag = false;
        int count = 0;
        while(flag || retryCount > 0) {

            flag = "404".equals(processedResponse.get("code"));

            if (!StringUtils.isBlank((String) processedResponse.get("chainId")))
                break;

            responseEntity = restTemplate.exchange(url, method, requestData, String.class);
            processedResponse = function.apply(responseEntity.getBody());
            retryCount--;
            count++;
            if (logger.isDebugEnabled())
                logger.debug("重试第 {} 次，请求：{}，的响应信息：{}", count, url, responseEntity.getBody());

            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                // 中断处理
            }
        }

        return null;
    }
}
