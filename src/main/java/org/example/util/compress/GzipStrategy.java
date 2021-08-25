package org.example.util.compress;

import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class GzipStrategy implements Compress {

    private static final Logger logger = LoggerFactory.getLogger(TarStrategy.class);

    @Override
    public boolean compress(File source, String destPath, boolean strictMode) {
        TarArchiveOutputStream tarOs = null;
        try {
            // 创建一个 FileOutputStream 到输出文件（.tar.gz）
            FileOutputStream fos = new FileOutputStream(destPath);
            // 创建一个 GZIPOutputStream，用来包装 FileOutputStream 对象
            GZIPOutputStream gos = new GZIPOutputStream(fos);
            // 创建一个 TarArchiveOutputStream，用来包装 GZIPOutputStream 对象
            tarOs = new TarArchiveOutputStream(gos);
        } catch (IOException e) {
            logger.error("Generation of .tar.gz format file failed", e);
        }

        TarStrategy tarStrategy = new TarStrategy();
        List<Boolean> results = new ArrayList<>();
        tarStrategy.compress(tarOs, source, results);

        return strictMode ? results.stream().noneMatch(aBoolean -> aBoolean == null || !aBoolean) : results.stream().anyMatch(aBoolean -> aBoolean != null && aBoolean);
    }
}
