package org.example.util.io;

import org.apache.commons.lang3.StringUtils;
import org.example.util.RegularMatchingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @descriptions: 操作文件工具类
 * @author: zhangfaquan
 * @date: 2021/8/2 17:25
 * @version: 1.0
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * @descriptions 判断文件路径是否为绝对路径，true-绝对路径，false-相对路径
     * @author zhangfaquan
     * @date 2021/8/9 21:42
     * @param file 文件对象
     * @return boolean
     */
    public static boolean isAbsolutePath(File file) {
        return isAbsolutePath(file.getPath());
    }

    /**
     * @descriptions 判断文件路径是否为绝对路径，true-绝对路径，false-相对路径
     * @author zhangfaquan
     * @date 2021/8/9 21:41
     * @param filePath 文件路径
     * @return boolean
     */
    public static boolean isAbsolutePath(String filePath) {
        if (StringUtils.isBlank(filePath))
            return false;
        filePath = StringUtils.strip(filePath);
        return RegularMatchingUtils.matcherWindowsDriveLetter(filePath) || filePath.startsWith("/");
    }

    /**
     * For example:
     *     "d:\\  asssd\\   csssa   ", "ss\\    ", "\\eee"        -> "d:/asssd/csssa/ss/eee"
     *     "  /  asssd/   csssa   ", "  ss/    ", "  /e  ee  "    -> "/asssd/csssa/ss/eee"
     *     "  \\  asssd/   csssa   ", "  ss\\    ", "  /e  ee  "  -> "asssd/csssa/ss/eee"
     *
     * @descriptions 封装文件路径
     * @param path 文件部分路径
     * @return
     */
    public static String getFilePath(String... path) {
        path = Arrays.stream(path).map(StringUtils::deleteWhitespace).toArray(String[]::new);
        String[] dealPath = Arrays.stream(path).map(s -> new File(s).getPath()).toArray(String[]::new);
        String filePath = Arrays.stream(dealPath)
                .map(s -> StringUtils.split(s, "\\"))
                .flatMap(Arrays::stream)
                .map(s -> StringUtils.split(s, "/"))
                .flatMap(Arrays::stream)
                .collect(Collectors.joining("/"));

        return path[0].startsWith("/") ? "/" + filePath : filePath;
    }

    /**
     * @descriptions 遍历目录下的所有文件
     * @author zhangfaquan
     * @date 2021/8/9 20:18
     * @param path 文件路径
     * @return java.util.List<java.io.File>
     */
    public static List<File> getAllFile(String path) {
        return getAllFile(new File(path));
    }

    /**
     * @descriptions 遍历目录下的所有文件
     * @author zhangfaquan
     * @date 2021/8/9 20:19
     * @param file 文件对象
     * @return java.util.List<java.io.File>
     */
    public static List<File> getAllFile(File file) {
        List<File> fileList = new ArrayList<>();
        treeWalk(file, fileList::add);
        return fileList;
    }

    /**
     * @descriptions 获取指定的文件对象。
     * @author zhangfaquan
     * @date 2021/8/9 20:16
     * @param file 文件对象
     * @param predicate 判断条件
     * @return java.util.List<java.io.File>
     */
    public static List<File> getSpecifiedFiles(File file, Predicate<File> predicate) {
        List<File> fileList = new ArrayList<>();
        treeWalk(file, file1 -> {
            if (file1.isFile())
                fileList.add(file1);
        }, predicate);
        return fileList;
    }

    /**
     * @descriptions 遍历目录时执行自定义操作。
     * @author zhangfaquan
     * @date 2021/8/9 20:19
     * @param file 文件对象
     * @param consumer 自定义操作
     * @return void
     */
    public static void treeWalk(File file, Consumer<File> consumer) {
        treeWalk(file, consumer, null);
    }

    /**
     * @descriptions 遍历目录时执行自定义操作
     * @author zhangfaquan
     * @date 2021/8/9 20:21
     * @param file 文件对象
     * @param consumer 自定义操作
     * @param predicate 判断条件
     * @return void
     */
    public static void treeWalk(File file, Consumer<File> consumer, Predicate<File> predicate) {

        Predicate<File> finalPredicate = predicate == null ? file1 -> true : predicate;
        File[] files = file.listFiles(fileTmp -> fileTmp.isDirectory() || finalPredicate.test(fileTmp));
        if (files == null)
            return;
        for (File item : files) {
            if (item.isDirectory())
                treeWalk(item, consumer, predicate);
            if (consumer != null)
                consumer.accept(item);
        }
    }

    /**
     * @descriptions 递归创建文件。true-创建成功，false-文件已存在。
     * @author zhangfaquan
     * @date 2021/8/9 21:59
     * @param filePath 文件对象
     * @return boolean
     */
    public static boolean createFile(String filePath) throws IOException {
        if (filePath == null)
            return false;
        String processedFilePath = getFilePath(filePath);
        return createFile(new File(processedFilePath));
    }

    /**
     * @descriptions 递归创建文件。true-创建成功，false-文件已存在。
     * @author zhangfaquan
     * @date 2021/8/9 21:59
     * @param file 文件对象
     * @return boolean
     */
    public static boolean createFile(File file) throws IOException {
        File parentFile = file.getParentFile();
        boolean flag = true;
        if (file.exists())
            return false;
        else if (!parentFile.exists())
            flag = parentFile.mkdirs();
        if (flag)
            flag = file.createNewFile();
        return flag;
    }

    /**
     * @descriptions 递归删除目录或文件，true-删除成功，false-目录或文件不存在
     * @author zhangfaquan
     * @date 2021/8/10 0:19
     * @param filePath 文件路径
     * @return boolean
     */
    public static boolean deleteFile(String filePath) {
        return deleteFile(new File(filePath));
    }

    /**
     * @descriptions 递归删除目录或文件，true-删除成功，false-目录或文件不存在
     * @author zhangfaquan
     * @date 2021/8/10 0:19
     * @param file 文件对象
     * @return boolean
     */
    public static boolean deleteFile(File file) {
        if (file == null)
            return false;
        treeWalk(file, File::delete);
        return file.delete();
    }

    public static boolean mkdirs(String filePath) {
        return mkdirs(new File(filePath));
    }

    public static boolean mkdirs(File file) {
        boolean isSuccessMk = true;
        try {
            file.mkdirs();
        } catch (Exception e) {
            isSuccessMk = false;
            logger.error("递归创建目录失败。路径：{}", file.getParent(), e);
        }
        return isSuccessMk;
    }

    public static void getRelativePath(String referencePath, String comparisonPath) {
        getRelativePathByAbsolutePath(referencePath, comparisonPath);
    }

    public static String getRelativePathByAbsolutePath(String referencePath, String comparisonPath) {
        if (!isAbsolutePath(referencePath) || !isAbsolutePath(comparisonPath))
            return null;
        comparisonPath = FileUtils.getFilePath(comparisonPath);
        referencePath = FileUtils.getFilePath(referencePath);
        String[] comparisonPathPart = StringUtils.split(comparisonPath, "/");
        String[] referencePathPart = StringUtils.split(referencePath, "/");
        return Arrays.stream(comparisonPathPart).skip(referencePathPart.length-1).collect(Collectors.joining("/"));
    }

    public static String getFileHeader(String filePath) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[28];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    /**
     * 将byte数组转换成十六进制字符串形式
     *
     * @param src 要读取文件头信息的文件的byte数组
     * @return 文件头信息
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        System.out.println(builder.toString());
        return builder.toString();
    }

    public static boolean isText(String file) {
        String contentType = new MimetypesFileTypeMap().getContentType(file);
        return "text/plain".equalsIgnoreCase(contentType);
    }
}
