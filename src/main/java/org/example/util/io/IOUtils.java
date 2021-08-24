package org.example.util.io;

import org.apache.commons.lang3.StringUtils;
import org.example.exception.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @descriptions: IO流工具类
 * @author: zhangfaquan
 * @date: 2021/8/24 21:39
 * @version: 1.0
 */
public class IOUtils {

    private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);

    public static void copyFile(String sourcePath, String destPath, boolean isByte) {
        File file = new File(sourcePath);
        if (isByte) {
            BufferedInputStream bufferedInputStream = null;
            try {
                bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                logger.error("指定的源文件未找到, 路径：{}", file.getPath(), e);
            }
            copyFile(bufferedInputStream, destPath);
            return;
        }
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            logger.error("指定的源文件未找到, 路径：{}", file.getPath(), e);
        }
        copyFile(bufferedReader, destPath);
    }

    public static void copyFile(InputStream inputStream, String destPath) {
        copyFile(inputStream, destPath, 1024, true);
    }

    public static void copyFile(Reader reader, String destPath) {
        copyFile(reader, destPath, 1024, true);
    }

    public static void copyFile(InputStream inputStream, String destPath, int size, boolean isMkDir) {
        if (inputStream == null || StringUtils.isBlank(destPath))
            throw new ParameterException("输入流和目标输出路径都不能为空");

        File file = new File(destPath);
        // 创建父级目录
        if (isMkDir && !FileUtils.mkdirs(file.getParentFile()))
            return;

        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            logger.error("指定的目标路径非法。", e);
        }
        if (bufferedOutputStream == null)
            return;

        if (!(inputStream instanceof BufferedInputStream))
            inputStream = new BufferedInputStream(inputStream);

        // 拷贝文件
        byte[] bytes = new byte[size];
        int len;
        try {
            while ((len = inputStream.read(bytes)) != -1) {
                bufferedOutputStream.write(bytes, 0, len);
            }
        } catch (IOException e) {
            logger.error("拷贝文件时出现异常。", e);
        } finally {
            closeOutputStream(bufferedOutputStream);
        }
    }

    public static void copyFile(Reader reader, String destPath, int size, boolean isMkDir) {
        if (reader == null || StringUtils.isBlank(destPath))
            throw new ParameterException("输入流和目标输出路径都不能为空");

        File file = new File(destPath);
        // 创建父级目录
        if (isMkDir && !FileUtils.mkdirs(file.getParentFile()))
            return;

        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            logger.error("指定的目标路径非法。", e);
        }
        if (bufferedWriter == null)
            return;

        if (!(reader instanceof BufferedReader))
            reader = new BufferedReader(reader);

        // 拷贝文件
        char[] c = new char[size];
        int len;
        try {
            while ((len = reader.read(c)) != -1) {
                bufferedWriter.write(c, 0, len);
            }
        } catch (IOException e) {
            logger.error("拷贝文件时出现异常。", e);
        } finally {
            closeWriter(bufferedWriter);
        }
    }

    // 关闭流
    public static void closeInputStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.error("字节输入流关闭异常！", e);
            }
        }
    }

    public static void closeOutputStream(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                logger.error("字节输出流关闭异常！", e);
            }
        }
    }

    public static void closeReader(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                logger.error("字符输入流关闭异常！", e);
            }
        }
    }

    public static void closeWriter(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                logger.error("字符输出流关闭异常！", e);
            }
        }
    }
}
