package org.example.util.compress;

import org.example.util.io.FileUtils;

import java.io.File;

/**
 * @descriptions: 解压缩工具类
 * @author: zhangfaquan
 * @date: 2021/8/24 10:44
 * @version: 1.0
 */
public class CompressUtils {

    public static boolean compress(String sourcePath, String destPath) {
        sourcePath = FileUtils.getFilePath(sourcePath);
        destPath = FileUtils.getFilePath(destPath);
        return compress(new File(sourcePath), destPath);
    }

    public static boolean compress(File source, String destPath) {



        return false;
    }
}
