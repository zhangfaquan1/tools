package org.example.util.compress;

import org.example.constant.CompressStrategyEnum;
import org.example.util.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @descriptions: 解压缩工具类
 * @author: zhangfaquan
 * @date: 2021/8/24 10:44
 * @version: 1.0
 */
public class CompressUtils {

    private CompressUtils() {
    }

    private static Logger logger = LoggerFactory.getLogger(CompressUtils.class);

    /**
     * @descriptions 以严格模式压缩指定文件或目录。
     * @author zhangfaquan
     * @date 2021/8/26 20:57
     * @param applicationContext 上下文
     * @param compressStrategyEnum 压缩策略枚举对象
     * @param source 源数据
     * @param destPath 指定压缩路径
     * @return boolean
     */
    public static boolean compress(ApplicationContext applicationContext, CompressStrategyEnum compressStrategyEnum, File source, String destPath) {

        return compress(applicationContext, compressStrategyEnum, source, destPath, true);
    }

    /**
     * @descriptions 压缩指定文件或目录，当打包失败时会执行自定义的回调函数。
     * @author zhangfaquan
     * @date 2021/8/26 20:58
     * @param applicationContext 上下文
     * @param compressStrategyEnum 压缩策略枚举对象
     * @param source 源数据
     * @param destPath 指定压缩路径
     * @param strictMode 判断是否开启严格处理模式。
     *                   true-严格模式，只要有一个文件或目录打包不成功，则算打包不成功。
     *                   false-普通模式，只要有一个成功就算打包成功。
     * @return boolean
     */
    public static boolean compress(ApplicationContext applicationContext, CompressStrategyEnum compressStrategyEnum, File source, String destPath, boolean strictMode) {
        if (applicationContext == null || compressStrategyEnum == null) {
            logger.error("上下文和策略对象都不能为null");
            return false;
        }

        AbstractCompress compress = null;
        try {
            compress = applicationContext.getInstance(compressStrategyEnum.getStrategyName(), AbstractCompress.class, true);
        } catch (Exception e) {
            logger.error("获取压缩策略单例对象失败。", e);
        }

        if (compress == null)
            return false;

        return compress.compress(source, destPath, strictMode);
    }
}
