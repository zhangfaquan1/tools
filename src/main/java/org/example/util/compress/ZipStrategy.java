package org.example.util.compress;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ZipStrategy extends AbstractCompress {

    private static final Logger logger = LoggerFactory.getLogger(ZipStrategy.class);

    /**
     * @descriptions 整卷压缩
     * @param
     * @return
     */
    @Override
    boolean compress(File source, String dest, boolean strictMode) {

        List<Boolean> results = new ArrayList<>();
        try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(new File(dest))) {
            compress(zipArchiveOutputStream, source, results);
        } catch (IOException e) {
            logger.error("zip方式压缩失败。", e);
        }
        return strictMode ? results.stream().noneMatch(aBoolean -> aBoolean == null || !aBoolean) : results.stream().anyMatch(aBoolean -> aBoolean != null && aBoolean);
    }

    /**
     * @descriptions 分卷压缩
     * @param size 设置分卷大小，注意zip合法的分卷大小在64kb到4gb之间，超出此范围的值会抛 java.lang.IllegalArgumentException 异常
     * @return
     */
    boolean compress(File source, String dest, int size, boolean strictMode) {
        List<Boolean> results = new ArrayList<>();
        try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(new File(dest), size)) {
            compress(zipArchiveOutputStream, source, results);
        } catch (Exception e) {
            logger.error("zip方式压缩失败。", e);
        }
        return strictMode ? results.stream().noneMatch(aBoolean -> aBoolean == null || !aBoolean) : results.stream().anyMatch(aBoolean -> aBoolean != null && aBoolean);
    }

    @Override
    boolean putFile(ArchiveOutputStream archiveOutputStream, File sourceFile, String destPath) {
        if (sourceFile == null)
            return false;
        ZipArchiveEntry entry = new ZipArchiveEntry(sourceFile, destPath);
        return putArchiveEntry(archiveOutputStream, entry, sourceFile);
    }
}