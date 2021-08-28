package org.example.util.compress;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.lang3.StringUtils;
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
        boolean isCompress = true;
        String tarFile = source.getName() + ".tar";
        try {
            // 创建一个 FileOutputStream 到输出文件（.tar.gz）
            FileOutputStream fileOutputStream = new FileOutputStream(destPath);
            gos = new GZIPOutputStream(new BufferedOutputStream(fileOutputStream));
            TarStrategy tarStrategy = new TarStrategy();
            if (!tarStrategy.compress(source, tarFile, strictMode, handlingContainer, bufferSize))
                isCompress = false;

            tarInputStream = IOUtils.getBufferedInputStream(tarFile);
            isCompress = isCompress && IOUtils.copyFile(tarInputStream, gos);
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
        if (source == null || StringUtils.isBlank(dest)) {
            logger.error("传入 unCompress 的参数含空值。");
            return false;
        }

        if (!FileUtils.mkdirs(dest))
            return false;

        InputStream in = null;
        File tarFile = new File(dest, source.getName());
        OutputStream out = null;
        GzipCompressorInputStream gzIn = null;
        boolean flag = false;
        try {
            in = IOUtils.getBufferedInputStream(source);
            out = IOUtils.getBufferedOutputStream(tarFile, bufferSize);
            gzIn = new GzipCompressorInputStream(in);
            IOUtils.copyFile(gzIn, out, handlingContainer);
            TarStrategy tarStrategy = new TarStrategy();
            flag = tarStrategy.unCompress(tarFile, dest, strictMode, handlingContainer, bufferSize);
        } catch (IOException e) {
            logger.error("gzip解压时出现异常。", e);
        } finally {
            IOUtils.closeInputStream(in);
            IOUtils.closeOutputStream(out);
            IOUtils.closeInputStream(gzIn);
            FileUtils.deleteFile(tarFile);
        }
        return flag;
    }

}
