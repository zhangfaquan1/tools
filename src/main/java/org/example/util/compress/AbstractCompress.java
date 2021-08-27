package org.example.util.compress;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.example.util.io.FileUtils;
import org.example.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class AbstractCompress {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCompress.class);

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * @param strictMode 判断是否开启严格处理模式。
     *                   true-严格模式，只要有一个文件或目录打包不成功，则算打包不成功。
     *                   false-普通模式，只要有一个成功就算打包成功。
     * @return
     * @descriptions 压缩或打包接口。
     */
    public boolean compress(File source, String dest, boolean strictMode) {
        return compress(source, dest, strictMode, 1024, DEFAULT_BUFFER_SIZE);
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
            results.add(putFile(archiveOutputStream, source, FileUtils.getRelativePathByAbsolutePath(sourcePath, source.getPath()), handlingContainer, bufferSize));

            // 递归处理带文件的目录
            FileUtils.treeWalk(source, file -> {
                if (file.isDirectory())
                    results.add(putFile(archiveOutputStream, file, FileUtils.getRelativePathByAbsolutePath(sourcePath, file.getPath()), handlingContainer, bufferSize));
                if (file.isFile())
                    results.add(putFile(archiveOutputStream, file, FileUtils.getRelativePathByAbsolutePath(sourcePath, file.getPath()), handlingContainer, bufferSize));
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

    public abstract boolean unCompress(File source, String dest, boolean strictMode);

    protected void closeArchiveEntry(ArchiveOutputStream archiveOutputStream) {
        try {
            archiveOutputStream.closeArchiveEntry();
        } catch (IOException e) {
            logger.error("关闭ArchiveEntry资源出现异常。", e);
        }
    }
}
