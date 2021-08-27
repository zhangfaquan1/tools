package org.example.util.compress;

import org.example.util.io.FileUtils;
import org.example.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class GzipStrategy extends AbstractCompress {

    private static final Logger logger = LoggerFactory.getLogger(TarStrategy.class);

    @Override
    public boolean compress(File source, String destPath, boolean strictMode, int handlingContainer, int bufferSize) {
        GZIPOutputStream gos = null;
        InputStream tarInputStream = null;
        boolean isCompress = false;
        String tarFile = source.getName() + ".tar";
        try {
            // 创建一个 FileOutputStream 到输出文件（.tar.gz）
            FileOutputStream fileOutputStream = new FileOutputStream(destPath);
            gos = new GZIPOutputStream(new BufferedOutputStream(fileOutputStream));
            TarStrategy tarStrategy = new TarStrategy();
            if (!tarStrategy.compress(source, tarFile, strictMode, handlingContainer, bufferSize))
                return false;

            tarInputStream = IOUtils.getBufferedInputStream(tarFile);
            isCompress = IOUtils.copyFile(tarInputStream, gos);
        } catch (IOException e) {
            logger.error("gzip方式压缩失败。", e);
        } finally {
            IOUtils.closeInputStream(tarInputStream);
            IOUtils.closeOutputStream(gos);
            FileUtils.deleteFile(tarFile);
        }
        return isCompress;
    }

    @Override
    public boolean unCompress(File source, String dest, boolean strictMode, int handlingContainer, int bufferSize) {
        return false;
    }

}
