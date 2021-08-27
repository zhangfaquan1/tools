package org.example.util.compress;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
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
    public boolean compress(File source, String destPath, boolean strictMode, int handlingContainer, int bufferSize) {

        return compress(source, new File(destPath), strictMode, handlingContainer, bufferSize);
    }

    public boolean compress(File source, File dest, boolean strictMode, int handlingContainer, int bufferSize) {

        TarArchiveOutputStream tarArchiveOutputStream =  null;
        List<Boolean> results = new ArrayList<>();
        try {
            tarArchiveOutputStream = new TarArchiveOutputStream(new FileOutputStream(dest));
            // 若不设置此模式，当文件名超过 100 个字节时会抛出异常，但是这个模式存在兼容性问题，有些系统无法使用。
            tarArchiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            compress(tarArchiveOutputStream, source, results, handlingContainer, bufferSize);
        } catch (FileNotFoundException e) {
            logger.error("打tar包：{} 时出错。", dest, e);
        } finally {
            IOUtils.closeOutputStream(tarArchiveOutputStream);
        }

        return isSuccess(strictMode, results);
    }

    @Override
    protected boolean putFile(ArchiveOutputStream tarArchiveOutputStream, File sourceFile, String destPath, int handlingContainer, int bufferSize) {
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(sourceFile, destPath);
        tarArchiveEntry.setSize(sourceFile.length());
        return putArchiveEntry(tarArchiveOutputStream, tarArchiveEntry, sourceFile, handlingContainer, bufferSize);
    }

    @Override
    public boolean unCompress(File source, String dest, boolean strictMode, int handlingContainer, int bufferSize) {
        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(IOUtils.getFileInputStream(source));
        return unCompress(tarArchiveInputStream, dest, strictMode, handlingContainer, bufferSize);
    }
}
