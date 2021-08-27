package org.example.util.compress;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.lang3.StringUtils;
import org.example.util.io.FileUtils;
import org.example.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCompress {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCompress.class);

    public static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * @param strictMode 判断是否开启严格处理模式。
     *                   true-严格模式，只要有一个文件或目录打包不成功，则算打包不成功。
     *                   false-普通模式，只要有一个成功就算打包成功。
     * @return
     * @descriptions 压缩或打包接口。
     */
    public boolean compress(File source, String dest, boolean strictMode) {
        return compress(source, dest, strictMode, 64, DEFAULT_BUFFER_SIZE);
    }

    public boolean compress(File source, String dest, boolean strictMode, int handlingContainer) {
        return compress(source, dest, strictMode, handlingContainer, DEFAULT_BUFFER_SIZE);
    }

    public boolean compress(File source, String dest, int bufferSize, boolean strictMode) {
        return compress(source, dest, strictMode, 1024, bufferSize);
    }

    public abstract boolean compress(File source, String dest, boolean strictMode, int handlingContainer, int bufferSize);

    protected void compress(ArchiveOutputStream archiveOutputStream, File source, List<Boolean> results, int handlingContainer, int bufferSize) {
        String sourcePath = source.getPath();
        if (source.isDirectory()) {
            results.add(putFile(archiveOutputStream, source, FileUtils.getRelativePathByAbsolutePath(sourcePath, source.getAbsolutePath()), handlingContainer, bufferSize));

            // 递归处理带文件的目录
            FileUtils.treeWalk(source, file -> {
                if (file.isDirectory())
                    results.add(putFile(archiveOutputStream, file, FileUtils.getRelativePathByAbsolutePath(sourcePath, file.getAbsolutePath()), handlingContainer, bufferSize));
                if (file.isFile())
                    results.add(putFile(archiveOutputStream, file, FileUtils.getRelativePathByAbsolutePath(sourcePath, file.getAbsolutePath()), handlingContainer, bufferSize));
            });

            return;
        }
        results.add(putFile(archiveOutputStream, source, source.getName(), handlingContainer, bufferSize));
    }

    protected boolean putFile(ArchiveOutputStream archiveOutputStream, File sourceFile, String destPath, int handlingContainer, int bufferSize) {
        return true;
    }

    protected boolean putArchiveEntry(ArchiveOutputStream archiveOutputStream, ArchiveEntry archiveEntry, File sourceFile, int handlingContainer, int bufferSize) {
        boolean flag = true;
        InputStream bufferedInputStream = null;
        try {
            archiveOutputStream.putArchiveEntry(archiveEntry);
            if (sourceFile.isFile()) {
                bufferedInputStream = IOUtils.getBufferedInputStream(sourceFile, bufferSize);
                IOUtils.copyFile(bufferedInputStream, archiveOutputStream, handlingContainer);
            }
        } catch (IOException e) {
            flag = false;
            logger.error("tar包中加入目录：{} 出现异常。", archiveEntry.getName(), e);
        } finally {
            IOUtils.closeInputStream(bufferedInputStream);
            closeArchiveEntry(archiveOutputStream);
        }
        return flag;
    }

    public boolean unCompress(File source, String dest, boolean strictMode) {
        return unCompress(source, dest, strictMode, 1024, DEFAULT_BUFFER_SIZE);
    }

    public boolean unCompress(File source, String dest, boolean strictMode, int handlingContainer) {
        return unCompress(source, dest, strictMode, handlingContainer, DEFAULT_BUFFER_SIZE);
    }

    public boolean unCompress(File source, String dest, int bufferSize, boolean strictMode) {
        return unCompress(source, dest, strictMode, 1024, bufferSize);
    }

    public abstract boolean unCompress(File source, String dest, boolean strictMode, int handlingContainer, int bufferSize);

    protected boolean unCompress(ArchiveInputStream archiveInputStream, String dest, boolean strictMode, int handlingContainer, int bufferSize) {
        if (!FileUtils.mkdirs(dest))
            return false;

        List<Boolean> results = new ArrayList<>();
        try {
            ArchiveEntry entry;
            while ((entry = archiveInputStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    results.add(unCompressDir(dest, entry.getName()));
                } else {
                    results.add(unCompressFile(archiveInputStream, dest, entry.getName(), handlingContainer, bufferSize));
                }
            }
        } catch (Exception e) {
            logger.error("使用顺序方式解压全部文件时出现异常。", e);
        } finally {
            IOUtils.closeInputStream(archiveInputStream);
        }

        return isSuccess(strictMode, results);
    }

    protected boolean unCompressFile(InputStream inputStream, String dest, String relativePath, int handlingContainer, int bufferSize) {
        if (inputStream == null || StringUtils.isBlank(dest) || StringUtils.isBlank(relativePath)) {
            logger.error("传入 unCompressFile 的参数含空值");
            return false;
        }
        File outputFile = new File(dest, relativePath);
        if (!outputFile.getParentFile().exists()) {
            FileUtils.mkdirs(outputFile.getParentFile());
        }
        return IOUtils.copyFile(inputStream, IOUtils.getBufferedOutputStream(outputFile, bufferSize), handlingContainer);
    }

    protected boolean unCompressDir(String dest, String relativePath) {
        File outputFile = new File(dest, relativePath);
        return outputFile.exists() || FileUtils.mkdirs(outputFile);
    }

    protected void closeArchiveEntry(ArchiveOutputStream archiveOutputStream) {
        try {
            archiveOutputStream.closeArchiveEntry();
        } catch (IOException e) {
            logger.error("关闭ArchiveEntry资源出现异常。", e);
        }
    }

    protected boolean isSuccess(boolean strictMode, List<Boolean> results) {
        return strictMode ? results.stream().noneMatch(aBoolean -> aBoolean == null || !aBoolean) : results.stream().anyMatch(aBoolean -> aBoolean != null && aBoolean);
    }
}
