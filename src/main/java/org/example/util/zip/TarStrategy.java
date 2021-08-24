package org.example.util.zip;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class TarStrategy implements Compress {

    @Override
    public boolean compress(File source, String destPath) {
//        TarArchiveOutputStream tarOs = null;
//        boolean flag = false;
//        try {
//            // 创建一个 FileOutputStream 到输出文件（.tar）
//            FileOutputStream fos = new FileOutputStream(destPath);
//            // 创建一个 GZIPOutputStream，用来包装 FileOutputStream 对象
//            GZIPOutputStream gos = new GZIPOutputStream(new BufferedOutputStream(fos));
//            // 创建一个 TarArchiveOutputStream，用来包装 GZIPOutputStream 对象
//            tarOs = new TarArchiveOutputStream(gos);
//            // 若不设置此模式，当文件名超过 100 个字节时会抛出异常
//            tarOs.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
//            flag = true;
//        } catch (IOException e) {
//            logger.error("Generation of .tar.gz format file failed", e);
//        }finally{
//            try {
//                if (tarOs != null) tarOs.close();
//            } catch (IOException e) {
//                logger.error("Close Resource Exception", e);
//            }
//        }
        return false;
    }
}
