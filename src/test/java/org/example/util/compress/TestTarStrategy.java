package org.example.util.compress;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.example.util.io.FileUtils;
import org.example.util.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TestTarStrategy {

    // 打tar包
    @Test
    public void testCompress1() throws IOException {
        File dest = new File("E:\\Work\\a.tar"); // 指定输出文件。如果同级目录下存在同名文件，则就文件会被覆盖。
        FileOutputStream fileOutputStream = new FileOutputStream(dest);
        TarArchiveOutputStream archiveOutputStream = new TarArchiveOutputStream(fileOutputStream);
        // 若不设置此模式，当文件名超过 100 个字节时会抛出异常，但是若是开启这个模式那么在有的系统下可能会报错。
        archiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
        TarArchiveEntry entry = new TarArchiveEntry("test2/pom1.xml");
        entry.setSize(10); // 设置文件大小
        archiveOutputStream.putArchiveEntry(entry);
        // 追加写入
        archiveOutputStream.write("asdfa".getBytes(StandardCharsets.UTF_8));
        archiveOutputStream.write("xxasf".getBytes(StandardCharsets.UTF_8));
        archiveOutputStream.closeArchiveEntry();

        TarArchiveEntry entry2 = new TarArchiveEntry("test2/a/");
        archiveOutputStream.putArchiveEntry(entry2);
        archiveOutputStream.closeArchiveEntry();
    }

    @Test
    public void testCompress2() {
        File dest = new File("E:\\Work\\a.tar"); // 会直接覆盖同名包
        File source = new File("E:\\Work\\新建文本文档.txt");
        File source2 = new File("E:\\Work\\testDir");
        System.out.println(source2.isDirectory());
        TarArchiveOutputStream tarArchiveOutputStream =  null;
        try {
            tarArchiveOutputStream = new TarArchiveOutputStream(new FileOutputStream(dest));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (tarArchiveOutputStream == null)
            return;

        // 如果file为目录类型，会不会直接创建？
        BufferedInputStream bufferedInputStream = IOUtils.getBufferedInputStream(source);
        if (bufferedInputStream == null)
            return;

//        InputStream fileInputStream = IOUtils.getFileInputStream(source2);
//        TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(fileInputStream);
//        try {
//            TarArchiveEntry entry = tarArchiveInputStream.getNextTarEntry();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            TarArchiveEntry entry = tarArchiveInputStream.getNextTarEntry();
//            File archiveEntry = new File("z/x/y", entry.getName());
//            archiveEntry.mkdirs();
//            if (entry.isDirectory()) {
//                archiveEntry.mkdir();
//            }
//            tarArchiveOutputStream.closeArchiveEntry();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        TarArchiveEntry entry = new TarArchiveEntry("a.xml");
        System.out.println(source.length());
        entry.setSize(source.length()); // 设置文件大小
        try {
            tarArchiveOutputStream.putArchiveEntry(entry);
            IOUtils.copyFile(bufferedInputStream, tarArchiveOutputStream);
            tarArchiveOutputStream.closeArchiveEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TarArchiveEntry entry2 = new TarArchiveEntry(source2);
        try {
            tarArchiveOutputStream.putArchiveEntry(entry2);
            tarArchiveOutputStream.closeArchiveEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        List<Boolean> results = new ArrayList<>();
//        results.add(true);
//        results.add(true);
        results.add(true);
//        results.add(false);
        results.add(false);
        boolean b = results.stream().noneMatch(aBoolean -> aBoolean == null || !aBoolean);
        boolean b1 = results.stream().anyMatch(aBoolean -> aBoolean != null && aBoolean);
        System.out.println(b);
        System.out.println(b1);
    }

    @Test
    public void testCompress() {
        TarStrategy tarStrategy = new TarStrategy();
        File file = new File("E:\\Work\\test1");
        boolean compress = tarStrategy.compress(file, "E:\\Work\\test2.tar", true);
        System.out.println(compress);
    }

}
