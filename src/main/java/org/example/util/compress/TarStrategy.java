package org.example.util.compress;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.example.util.io.FileUtils;
import org.example.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TarStrategy implements Compress {

    private static final Logger logger = LoggerFactory.getLogger(TarStrategy.class);

    @Override
    public boolean compress(File source, String destPath, boolean strictMode) {

        return compress(source, new File(destPath), strictMode);
    }

    public boolean compress(File source, File dest, boolean strictMode) {

        TarArchiveOutputStream tarArchiveOutputStream =  null;
        List<Boolean> results = new ArrayList<>();
        try {
            tarArchiveOutputStream = new TarArchiveOutputStream(new FileOutputStream(dest));
            // 若不设置此模式，当文件名超过 100 个字节时会抛出异常，但是这个模式存在兼容性问题，有些系统无法使用。
            tarArchiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            compress(tarArchiveOutputStream, source, results);
        } catch (FileNotFoundException e) {
            logger.error("打tar包：{} 时出错。", dest, e);
        } finally {
            IOUtils.closeOutputStream(tarArchiveOutputStream);
        }

        return strictMode ? results.stream().noneMatch(aBoolean -> aBoolean == null || !aBoolean) : results.stream().anyMatch(aBoolean -> aBoolean != null && aBoolean);
    }

    public void compress(TarArchiveOutputStream tarArchiveOutputStream, File source, List<Boolean> results) {
        String sourcePath = source.getPath();
        // 如果file为目录类型，会直接创建目录
        if (source.isDirectory()) {
            results.add(putDir(tarArchiveOutputStream, FileUtils.getRelativePathByAbsolutePath(sourcePath, source.getPath())));

            // 递归处理带文件的目录
            FileUtils.treeWalk(source, file -> {
                if (file.isDirectory())
                    results.add(putDir(tarArchiveOutputStream, FileUtils.getRelativePathByAbsolutePath(sourcePath, file.getPath())));
                if (file.isFile())
                    results.add(putFile(tarArchiveOutputStream, file, FileUtils.getRelativePathByAbsolutePath(sourcePath, file.getPath())));
            });

            return;
        }
        results.add(putFile(tarArchiveOutputStream, source, source.getName()));
    }

    private boolean putDir(TarArchiveOutputStream tarArchiveOutputStream, String destPath) {
        boolean flag = true;
        destPath = destPath.endsWith("/") ? destPath : destPath + "/";
        try {
            tarArchiveOutputStream.putArchiveEntry(new TarArchiveEntry(destPath));
        } catch (IOException e) {
            flag = false;
            logger.error("tar包中加入目录：{} 出现异常。", destPath, e);
        } finally {
            closeArchiveEntry(tarArchiveOutputStream);
        }
        return flag;
    }

    private boolean putFile(TarArchiveOutputStream tarArchiveOutputStream, File sourceFile, String destPath) {
        boolean flag;
        BufferedInputStream bufferedInputStream = IOUtils.getBufferedInputStream(sourceFile);
        if (bufferedInputStream == null) {
            return false;
        }
        TarArchiveEntry tarArchiveEntry = null;
        try {
            tarArchiveEntry = new TarArchiveEntry(destPath);
            tarArchiveEntry.setSize(sourceFile.length());
            tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
            flag = IOUtils.copyFile(bufferedInputStream, tarArchiveOutputStream);
        } catch (IOException e) {
            flag = false;
            closeArchiveEntry(tarArchiveOutputStream);
            logger.error("tar包中加入文件：{} 出现异常。", sourceFile.getPath(), e);
        } finally {
            closeArchiveEntry(tarArchiveOutputStream);
            IOUtils.closeInputStream(bufferedInputStream);
        }

        return flag;
    }

    public void closeArchiveEntry(TarArchiveOutputStream tarArchiveOutputStream) {
        try {
            tarArchiveOutputStream.closeArchiveEntry();
        } catch (IOException e) {
            logger.error("关闭ArchiveEntry资源出现异常。", e);
        }
    }
}
