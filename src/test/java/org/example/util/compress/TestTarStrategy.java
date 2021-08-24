package org.example.util.compress;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.example.util.io.FileUtils;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TestTarStrategy {

    // 打tar包
    @Test
    public void testCompress() throws IOException {
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
}
