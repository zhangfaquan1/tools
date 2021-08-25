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
import java.util.function.Function;

public class TarStrategy implements Compress {

    private static final Logger logger = LoggerFactory.getLogger(TarStrategy.class);

    @Override
    public boolean compress(File source, String destPath, boolean strictMode) {

        return compress(source, new File(destPath), strictMode);
    }

    public boolean compress(File source, File dest, boolean strictMode) {

        TarArchiveOutputStream tarArchiveOutputStream =  null;
        try {
            tarArchiveOutputStream = new TarArchiveOutputStream(new FileOutputStream(dest));
        } catch (FileNotFoundException e) {
            logger.error("指定的输出路径非法。", e);
        }
        if (tarArchiveOutputStream == null)
            return false;

        List<Boolean> results = new ArrayList<>();
        compress(tarArchiveOutputStream, source, results);

        return strictMode ? results.stream().noneMatch(aBoolean -> aBoolean == null || !aBoolean) : results.stream().anyMatch(aBoolean -> aBoolean != null && aBoolean);
    }

    public void compress(TarArchiveOutputStream tarArchiveOutputStream, File source, List<Boolean> results) {
        String sourcePath = source.getPath();
        // 如果file为目录类型，会直接创建目录
        if (source.isDirectory()) {
            Function<File, Boolean> function = file -> {
                File[] files = file.listFiles();
                if (files == null || files.length == 0) {
                    // 处理空目录
                    results.add(putDir(tarArchiveOutputStream, FileUtils.getRelativePathByAbsolutePath(sourcePath, file.getPath())));
                    return true;
                }
                return false;
            };
            if (function.apply(source))
                return;

            // 递归处理带文件的目录
            FileUtils.treeWalk(source, file -> {
                if (file.isDirectory())
                    function.apply(file);
                if (file.isFile())
                    results.add(putFile(tarArchiveOutputStream, file, FileUtils.getRelativePathByAbsolutePath(sourcePath, file.getPath())));
            });

            return;
        }
        results.add(putFile(tarArchiveOutputStream, source, source.getPath()));
    }

    private boolean putDir(TarArchiveOutputStream tarArchiveOutputStream, String dirPath) {
        boolean flag = true;
        dirPath = dirPath.endsWith("/") ? dirPath : dirPath + "/";

        try {
            tarArchiveOutputStream.putArchiveEntry(new TarArchiveEntry(dirPath));
        } catch (IOException e) {
            flag = false;
            logger.error("tar包中加入ArchiveEntry出现异常。", e);
        } finally {
            closeArchiveEntry(tarArchiveOutputStream);
        }
        return flag;
    }

    private boolean putFile(TarArchiveOutputStream tarArchiveOutputStream, File sourceFile, String destPath) {
        boolean flag = true;
        BufferedInputStream bufferedInputStream = IOUtils.getBufferedInputStream(sourceFile);
        if (bufferedInputStream == null) {
            return false;
        }
        TarArchiveEntry tarArchiveEntry = null;
        try {
            tarArchiveEntry = new TarArchiveEntry(destPath);
            tarArchiveEntry.setSize(sourceFile.length());
            tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
        } catch (IOException e) {
            flag = false;
            closeArchiveEntry(tarArchiveOutputStream);
            logger.error("tar包中加入ArchiveEntry出现异常。", e);
        }

        if (!flag)
            return false;

        try {
            flag = IOUtils.copyFile(bufferedInputStream, tarArchiveOutputStream);
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
