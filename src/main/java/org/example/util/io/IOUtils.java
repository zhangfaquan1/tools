package org.example.util.io;

import org.apache.commons.lang3.StringUtils;
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

    public static boolean copyFile(String sourcePath, String destPath, boolean isByte) {
        File file = new File(sourcePath);
        if (isByte) {
            BufferedInputStream bufferedInputStream = null;
            try {
                bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                logger.error("指定的源文件未找到, 路径：{}", file.getPath(), e);
            }
            return copyFile(bufferedInputStream, destPath);
        }
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            logger.error("指定的源文件未找到, 路径：{}", file.getPath(), e);
        }
        return copyFile(bufferedReader, destPath);
    }

    public static boolean copyFile(InputStream inputStream, String destPath) {
        return copyFile(inputStream, destPath, 1024, true);
    }

    public static boolean copyFile(Reader reader, String destPath) {
        return copyFile(reader, destPath, 1024, true);
    }

    public static boolean copyFile(InputStream inputStream, OutputStream outputStream) {
        return copyFile(inputStream, outputStream, 1024);
    }

    public static boolean copyFile(Reader reader, Writer writer) {
        return copyFile(reader, writer, 1024);
    }

    public static boolean copyFile(InputStream inputStream, String destPath, int size, boolean isMkDir) {
        if (inputStream == null || StringUtils.isBlank(destPath)) {
            logger.error("输入流和目标输出路径都不能为空");
            return false;
        }

        File file = new File(destPath);
        // 创建父级目录
        if (isMkDir && !FileUtils.mkdirs(file.getParentFile()))
            return false;

        BufferedOutputStream bufferedOutputStream = getBufferedOutputStream(file);
        if (bufferedOutputStream == null)
            return false;

        boolean flag;
        try {
            flag = copyFile(inputStream, bufferedOutputStream, size);
        } finally {
            closeOutputStream(bufferedOutputStream);
        }
        return flag;
    }

    public static boolean copyFile(Reader reader, String destPath, int size, boolean isMkDir) {
        if (reader == null || StringUtils.isBlank(destPath)) {
            logger.error("输入流和目标输出路径都不能为空");
            return false;
        }

        File file = new File(destPath);
        // 创建父级目录
        if (isMkDir && !FileUtils.mkdirs(file.getParentFile()))
            return false;

        BufferedWriter bufferedWriter = getBufferedWriter(file);
        if (bufferedWriter == null)
            return false;

        boolean flag;
        try {
            flag = copyFile(reader, bufferedWriter, size);
        } finally {
            closeWriter(bufferedWriter);
        }
        return flag;
    }

    public static boolean copyFile(InputStream inputStream, OutputStream outputStream, int size) {
        if (inputStream == null || outputStream == null) {
            logger.error("字节输入流和字节输出流都不能为null。");
            return false;
        }
        boolean flag = false;
        // 拷贝文件
        byte[] bytes = new byte[size];
        int len;
        try {
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            outputStream.flush();
            flag = true;
        } catch (IOException e) {
            logger.error("拷贝文件时出现异常。", e);
        }
        return flag;
    }

    public static boolean copyFile(Reader reader, Writer writer, int size) {
        if (reader == null || writer == null) {
            logger.error("字符输入流和字符输出流都不能为null。");
            return false;
        }

        boolean flag = false;
        // 拷贝文件
        char[] c = new char[size];
        int len;
        try {
            while ((len = reader.read(c)) != -1) {
                writer.write(c, 0, len);
            }
            writer.flush();
            flag = true;
        } catch (IOException e) {
            logger.error("拷贝文件时出现异常。", e);
        }
        return flag;
    }

    public static InputStream getFileInputStream(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            logger.error("指定的读取路径不能为null或空。");
            return null;
        }
        return getFileInputStream(new File(filePath));
    }

    public static InputStream getFileInputStream(File file) {
        if (file == null) {
            logger.error("指定的读取路径不能为null。");
            return null;
        }

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error("指定的目标路径非法。路径：{}", file.getPath(), e);
        }
        return fileInputStream;
    }

    public static BufferedOutputStream getBufferedOutputStream(String filePath) {
        return getBufferedOutputStream(new File(filePath));
    }

    public static BufferedOutputStream getBufferedOutputStream(File file) {
        if (file == null)
            return null;

        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            logger.error("指定的目标路径非法。", e);
        }
        return bufferedOutputStream;
    }

    public static BufferedInputStream getBufferedInputStream(String filePath) {
        return getBufferedInputStream(new File(filePath));
    }

    public static BufferedInputStream getBufferedInputStream(File file) {
        if (file == null)
            return null;

        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.error("指定的目标路径非法。路径：{}", file.getPath(), e);
        }
        return bufferedInputStream;
    }

    public static BufferedWriter getBufferedWriter(File file) {
        if (file == null)
            return null;

        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            logger.error("指定的目标路径非法。路径：{}", file.getPath(), e);
        }
        return bufferedWriter;
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
