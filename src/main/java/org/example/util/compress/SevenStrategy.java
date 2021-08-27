package org.example.util.compress;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.example.util.io.FileUtils;
import org.example.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SevenStrategy extends AbstractCompress {

    private static final Logger logger = LoggerFactory.getLogger(SevenStrategy.class);

    @Override
    public boolean compress(File source, String dest, boolean strictMode, int handlingContainer, int bufferSize) {
        List<Boolean> results = new ArrayList<>();
        try (SevenZOutputFile sevenZOutput = new SevenZOutputFile(new File(dest))) {
            compress(sevenZOutput, source, results, handlingContainer, bufferSize);
            sevenZOutput.finish();
        } catch (IOException e) {
            logger.error("zip方式压缩失败。", e);
        }
        return isSuccess(strictMode, results);
    }

    @Override
    public boolean unCompress(File source, String dest, boolean strictMode, int handlingContainer, int bufferSize) {
        return false;
    }

    void compress(SevenZOutputFile sevenZOutput, File source, List<Boolean> results, int handlingContainer, int bufferSize) {
        String sourcePath = source.getPath();
        if (source.isDirectory()) {
            results.add(putArchiveEntry(sevenZOutput, source, FileUtils.getRelativePathByAbsolutePath(sourcePath, source.getPath()), handlingContainer, bufferSize));

            // 递归处理带文件的目录
            FileUtils.treeWalk(source, file ->
                    results.add(putArchiveEntry(sevenZOutput, file, FileUtils.getRelativePathByAbsolutePath(sourcePath, file.getPath()), handlingContainer, bufferSize))
            );

            return;
        }
        results.add(putArchiveEntry(sevenZOutput, source, source.getName(), handlingContainer, bufferSize));
    }

    boolean putArchiveEntry(SevenZOutputFile sevenZOutput, File sourceFile, String destPath, int handlingContainer, int bufferSize) {
        boolean flag = true;
        if (sourceFile == null)
            return false;
        SevenZArchiveEntry archiveEntry = null;
        try {
            archiveEntry = sevenZOutput.createArchiveEntry(sourceFile, destPath);
        } catch (IOException e) {
            logger.error("生成sevenZArchiveEntry是出现异常。", e);
        }
        if (archiveEntry == null)
            return false;

        InputStream bufferedInputStream = null;
        try {
            sevenZOutput.putArchiveEntry(archiveEntry);
            if (sourceFile.isFile()) {
                bufferedInputStream = IOUtils.getBufferedInputStream(sourceFile, bufferSize);
                int len;
                byte[] b = new byte[handlingContainer];
                while ((len = bufferedInputStream.read(b)) != -1) {
                    sevenZOutput.write(b, 0, len);
                }
            }
        } catch (IOException e) {
            flag = false;
            logger.error("tar包中加入目录：{} 出现异常。", archiveEntry.getName(), e);
        } finally {
            closeArchiveEntry(sevenZOutput);
            IOUtils.closeInputStream(bufferedInputStream);
        }
        return flag;
    }



    void closeArchiveEntry(SevenZOutputFile sevenZOutput) {
        try {
            sevenZOutput.closeArchiveEntry();
        } catch (IOException e) {
            logger.error("关闭ArchiveEntry资源出现异常。", e);
        }
    }
}
