package org.example.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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
        return isMatch("^[A-Za-z]{1}\\:{1}.*", str);
    }

    public static boolean isMatch(String pattern, String srcStr) {
        return matcher(pattern, srcStr).matches();
    }

    public static List<String> getAll(String pattern, String srcStr) {
        List<String> list = new ArrayList<>();
        Consumer<Matcher> consumer = matcher -> {
            list.add(matcher.group(0));
        };
        walk(pattern, srcStr, consumer);
        return list.size() == 0 ? null : list;
    }

    public static void walk(String pattern, String srcStr, Consumer<Matcher> consumer) {
        Matcher matcher = matcher(pattern, srcStr);
        while (matcher.find()) {
            consumer.accept(matcher);
        }
    }

    public static Matcher matcher(String pattern, String srcStr) {
        Pattern r = Pattern.compile(pattern);
        return r.matcher(srcStr);
    }
}
