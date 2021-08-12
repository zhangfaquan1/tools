package org.example.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @descriptions: 正则匹配工具类
 * @author: zhangfaquan
 * @date: 2021/8/10 10:46
 * @version: 1.0
 */
public class RegularMatchingUtils {

    public static boolean matcherWindowsDriveLetter(String str) {
        return matcher("^[A-Za-z]{1}\\:{1}.*", str);
    }

    public static boolean matcher(String pattern, String srcStr) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(srcStr);
        return m.matches();
    }
}
