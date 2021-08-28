package org.example.util.compress;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.Test;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class TestGzipStrategy {

    @Test
    public void testCompress() {
        GzipStrategy gzipStrategy = new GzipStrategy();
        boolean compress = gzipStrategy.compress(new File("E:\\Work\\scan-cdr"), "scan-cdr.tar.gz", true);
//        boolean compress = gzipStrategy.compress(new File("E:\\Work\\bct"), "bct.tar.gz", true);
        System.out.println(compress);
    }

    @Test
    public void test() {
        createTarFile("E:\\Work\\bct", "bct.tar.gz");
    }

    private static boolean createTarFile(String sourceFolder, String tarGzPath) {
        TarArchiveOutputStream tarOs = null;
        boolean flag = false;
        try {
            // 创建一个 FileOutputStream 到输出文件（.tar.gz）
            FileOutputStream fos = new FileOutputStream(tarGzPath);
            // 创建一个 GZIPOutputStream，用来包装 FileOutputStream 对象
            GZIPOutputStream gos = new GZIPOutputStream(new BufferedOutputStream(fos));
            // 创建一个 TarArchiveOutputStream，用来包装 GZIPOutputStream 对象
            tarOs = new TarArchiveOutputStream(gos);
            // 若不设置此模式，当文件名超过 100 个字节时会抛出异常
            tarOs.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            addFilesToTarGZ(sourceFolder, "", tarOs);
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (tarOs != null) tarOs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static void addFilesToTarGZ(String filePath, String parent, TarArchiveOutputStream tarArchive) throws IOException {
        File file = new File(filePath);
        // Create entry name relative to parent file path
        String entryName = parent + file.getName();
        // 添加 tar ArchiveEntry
        tarArchive.putArchiveEntry(new TarArchiveEntry(file, entryName));
        if (file.isFile()) {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            // 写入文件
            IOUtils.copy(bis, tarArchive);
            tarArchive.closeArchiveEntry();
            bis.close();
        } else if (file.isDirectory()) {
            // 因为是个文件夹，无需写入内容，关闭即可
            tarArchive.closeArchiveEntry();
            // 读取文件夹下所有文件
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    // 递归
                    addFilesToTarGZ(f.getAbsolutePath(), entryName + File.separator, tarArchive);
                }
            }
        }
    }

    @Test
    public void testUncompress() {
        AbstractCompress gzipStrategy = new GzipStrategy();
        boolean b = gzipStrategy.unCompress(new File("scan-cdr.tar.gz"), "E:\\Work\\scan", true);
        System.out.println(b);
    }
}
