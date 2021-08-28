package org.example.util.compress;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.*;
import org.apache.commons.lang3.StringUtils;
import org.example.util.io.FileUtils;
import org.example.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ZipStrategy extends AbstractCompress {

    private static final Logger logger = LoggerFactory.getLogger(ZipStrategy.class);

    /**
     * @descriptions 整卷压缩
     * @param
     * @return
     */
    @Override
    public boolean compress(File source, String dest, boolean strictMode, int handlingContainer, int bufferSize) {

        List<Boolean> results = new ArrayList<>();
        try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(new File(dest))) {
            compress(zipArchiveOutputStream, source, results, handlingContainer, bufferSize);
        } catch (IOException e) {
            logger.error("zip方式压缩失败。", e);
        }
        return isSuccess(strictMode, results);
    }

    /**
     * @descriptions 分卷压缩
     * @param size 设置分卷大小，注意zip合法的分卷大小在64kb到4gb之间，超出此范围的值会抛 java.lang.IllegalArgumentException 异常
     * @return
     */
    public boolean compress(File source, String dest, int size, boolean strictMode, int handlingContainer, int bufferSize) {
        List<Boolean> results = new ArrayList<>();
        try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(new File(dest), size)) {
            compress(zipArchiveOutputStream, source, results, handlingContainer, bufferSize);
        } catch (Exception e) {
            logger.error("zip方式压缩失败。", e);
        }
        return isSuccess(strictMode, results);
    }

    @Override
    protected boolean putFile(ArchiveOutputStream archiveOutputStream, File sourceFile, String destPath, int handlingContainer, int bufferSize) {
        if (sourceFile == null)
            return false;
        ZipArchiveEntry entry = new ZipArchiveEntry(sourceFile, destPath);
        return putArchiveEntry(archiveOutputStream, entry, sourceFile, handlingContainer, bufferSize);
    }

    @Override
    public boolean unCompress(File source, String dest, boolean strictMode, int handlingContainer, int bufferSize) {
        return unCompressAllByRandom(source, dest, strictMode, handlingContainer, bufferSize);
    }

    // 实现随机方式解压缩
    public boolean unCompressAllByRandom(File source, String dest, boolean strictMode, int handlingContainer, int bufferSize) {
        boolean flag = false;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(source);
            flag = unCompress(zipFile, dest, strictMode, handlingContainer, bufferSize);
        } catch (IOException e) {
            logger.error("使用随机方式解压全部文件时出现异常。", e);
        } finally {
            closeZipFile(zipFile);
        }
        return flag;
    }

    // 实现分卷解压缩。
    public boolean unCompressVolumeByRandom(File source, String dest, boolean strictMode, int handlingContainer, int bufferSize) {
        boolean flag = false;
        ZipFile zipFile = null;
        try {
            SeekableByteChannel channel = ZipSplitReadOnlySeekableByteChannel.buildFromLastSplitSegment(source);
            zipFile = new ZipFile(channel);
            flag = unCompress(zipFile, dest, strictMode, handlingContainer, bufferSize);
        } catch (IOException e) {
            logger.error("使用随机方式解压全部文件时出现异常。", e);
        } finally {
            closeZipFile(zipFile);
        }
        return flag;
    }

    // 仅支持解压出单文件。
    public boolean unCompressSingleByRandom(File source, String dest, String entryName, int handlingContainer, int bufferSize) {
        ZipFile zipFile = null;
        InputStream inputStream = null;
        try {
            zipFile = new ZipFile(source);
            ZipArchiveEntry entry = zipFile.getEntry(entryName);
            if (entry.isDirectory())
                return false;

            inputStream = zipFile.getInputStream(entry);
            return unCompressFile(inputStream, dest, entry.getName(), handlingContainer, bufferSize);
        } catch (IOException e) {
            logger.error("使用随机方式解压全部文件时出现异常。", e);
        } finally {
            IOUtils.closeInputStream(inputStream);
            closeZipFile(zipFile);
        }
        return false;
    }

    public boolean unCompress(ZipFile zipFile, String dest, boolean strictMode, int handlingContainer, int bufferSize) {
        if (zipFile == null || StringUtils.isBlank(dest)) {
            logger.error("传入unCompress方法的参数中含空值");
            return false;
        }
        if (!FileUtils.mkdirs(dest))
            return false;

        List<Boolean> results = new ArrayList<>();
        InputStream inputStream = null;
        try {
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            ZipArchiveEntry entry;
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                if (entry.isDirectory()) {
                    results.add(unCompressDir(dest, entry.getName()));
                } else {
                    inputStream = zipFile.getInputStream(entry);
                    results.add(unCompressFile(inputStream, dest, entry.getName(), handlingContainer, bufferSize));
                }
            }
        } catch (IOException e) {
            logger.error("使用随机方式解压全部文件时出现异常。", e);
        } finally {
            IOUtils.closeInputStream(inputStream);
        }
        return isSuccess(strictMode, results);
    }

    // 实现顺序方式解压缩
    public boolean unCompressAllByOrder(File source, String dest, boolean strictMode, int handlingContainer, int bufferSize) {
        ZipArchiveInputStream zipInputStream = new ZipArchiveInputStream(IOUtils.getFileInputStream(source));
        return unCompress(zipInputStream, dest, strictMode, handlingContainer, bufferSize);
    }

    public static ZipFile getZipFile(String compressFilePath) {

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(compressFilePath);
        } catch (IOException e) {
            logger.error("构建 ZipFile 对象失败。", e);
        }
        return zipFile;
    }

    public static ZipArchiveInputStream getZipArchiveInputStream(String compressFilePath) {
        ZipArchiveInputStream zipArchiveInputStream = null;
        try {
            zipArchiveInputStream = new ZipArchiveInputStream(new FileInputStream(compressFilePath));
        } catch (IOException e) {
            logger.error("构建 ZipFile 对象失败。", e);
        }
        return zipArchiveInputStream;
    }

    public static boolean isArchiveEntryExit(String compressFilePath, String name) {
        try (ZipFile zipFile = new ZipFile(compressFilePath)){
            return isArchiveEntryExit(zipFile, name);
        } catch (IOException e) {
            logger.error("构建 ZipFile 对象失败。", e);
        }
        return false;
    }

    public static boolean isArchiveEntryExit(ZipFile zipFile, String name) {
        return zipFile.getEntry(name) != null;
    }

    public void closeZipFile(ZipFile zipFile) {
        if (zipFile != null) {
            try {
                zipFile.close();
            } catch (IOException e) {
                logger.error("关闭zipFile对象时出现异常。", e);
            }
        }
    }
}
