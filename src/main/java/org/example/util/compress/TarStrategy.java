package org.example.util.compress;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.example.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TarStrategy extends AbstractCompress {

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

    @Override
    boolean putDir(ArchiveOutputStream tarArchiveOutputStream, String destPath) {
        destPath = destPath.endsWith("/") ? destPath : destPath + "/";
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(destPath);
        return putArchiveEntry(tarArchiveOutputStream, tarArchiveEntry, null);
    }

    @Override
    boolean putFile(ArchiveOutputStream tarArchiveOutputStream, File sourceFile, String destPath) {
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(destPath);
        tarArchiveEntry.setSize(sourceFile.length());
        return putArchiveEntry(tarArchiveOutputStream, tarArchiveEntry, sourceFile);
    }
}
