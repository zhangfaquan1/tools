package org.example.util;

import org.junit.Test;

import java.util.Map;

/**
 * @descriptions: 测试加解密工具类
 * @author: zhangfaquan
 * @date: 2021/7/27 16:44
 * @version: 1.0
 */
public class TestEncryptUtils {

    @Test
    public void testRSA() throws Exception {
        //生成公钥和私钥
        Map<String, String> rsaKey = EncryptUtils.genKeyPair();
        //加密字符串
        String message = "df723820";
        System.out.println("随机生成的公钥为:" + rsaKey.get(EncryptUtils.RSA_PUBLIC_KEY));
        System.out.println("随机生成的私钥为:" + rsaKey.get(EncryptUtils.RSA_PRIVATE_KEY));
        String messageEn = EncryptUtils.encrypt(message,rsaKey.get(EncryptUtils.RSA_PUBLIC_KEY));
        System.out.println(message + "\t加密后的字符串为:" + messageEn);
        String messageDe = EncryptUtils.decrypt(messageEn,rsaKey.get(EncryptUtils.RSA_PRIVATE_KEY));
        System.out.println("还原后的字符串为:" + messageDe);
    }

    @Test
    public void testAES() throws Exception {
        // Lxs1F42ZvgJCFqxzlX+UGcdGi6ainmfCV8q6RrQjXFs=
        String content = "url：findNames.action";
        System.out.println("加密前：" + content);

        System.out.println("加密密钥和解密密钥：" + EncryptUtils.KEY);

        String encrypt = EncryptUtils.aesEncrypt(content, EncryptUtils.KEY);
        System.out.println("加密后：" + encrypt);

        String decrypt = EncryptUtils.aesDecrypt(encrypt, EncryptUtils.KEY);

        System.out.println("解密后：" + decrypt);
    }
}
