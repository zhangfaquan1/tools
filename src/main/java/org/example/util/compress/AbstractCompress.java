package org.example.util.compress;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.example.util.io.FileUtils;
import org.example.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class AbstractCompress {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCompress.class);

    /**
     * @descriptions 压缩或打包接口。
     * @param strictMode 判断是否开启严格处理模式。
     *                   true-严格模式，只要有一个文件或目录打包不成功，则算打包不成功。
     *                   false-普通模式，只要有一个成功就算打包成功。
     * @return
     */
    abstract boolean compress(File source, String dest, boolean strictMode);

    void compress(ArchiveOutputStream archiveOutputStream, File source, List<Boolean> results) {
        String sourcePath = source.getPath();
        if (source.isDirectory()) {
            results.add(putDir(archiveOutputStream, FileUtils.getRelativePathByAbsolutePath(sourcePath, source.getPath())));

            // 递归处理带文件的目录
            FileUtils.treeWalk(source, file -> {
                if (file.isDirectory())
                    results.add(putDir(archiveOutputStream, FileUtils.getRelativePathByAbsolutePath(sourcePath, file.getPath())));
                if (file.isFile())
                    results.add(putFile(archiveOutputStream, file, FileUtils.getRelativePathByAbsolutePath(sourcePath, file.getPath())));
            });

            return;
        }
        results.add(putFile(archiveOutputStream, source, source.getName()));
    }

    abstract boolean putDir(ArchiveOutputStream tarArchiveOutputStream, String destPath);

    abstract boolean putFile(ArchiveOutputStream tarArchiveOutputStream, File sourceFile, String destPath);

    boolean putArchiveEntry(ArchiveOutputStream archiveOutputStream, ArchiveEntry archiveEntry, File sourceFile) {
        boolean flag = true;
        try {
            archiveOutputStream.putArchiveEntry(archiveEntry);
            if (!archiveEntry.isDirectory()) {
                InputStream fileInputStream = IOUtils.getFileInputStream(sourceFile);
                IOUtils.copyFile(fileInputStream, archiveOutputStream);
            }
        } catch (IOException e) {
            flag = false;
            logger.error("tar包中加入目录：{} 出现异常。", archiveEntry.getName(), e);
        } finally {
            closeArchiveEntry(archiveOutputStream);
        }
        return flag;
    }

    void closeArchiveEntry(ArchiveOutputStream archiveOutputStream) {
        try {
            archiveOutputStream.closeArchiveEntry();
        } catch (IOException e) {
            logger.error("关闭ArchiveEntry资源出现异常。", e);
        }
    }
}
