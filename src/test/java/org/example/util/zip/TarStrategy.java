package org.example.util.zip;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TarStrategy {

    // 打tar包
    @Test
    public void testCompress() throws IOException {
        File source = new File("E:\\Work\\test2.tar");
        File dest = new File("E:\\Work\\test1\\pom.xml");
        FileOutputStream fileInputStream = new FileOutputStream(source);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileInputStream);
        TarArchiveOutputStream archiveOutputStream = new TarArchiveOutputStream(bufferedOutputStream);
        // 若不设置此模式，当文件名超过 100 个字节时会抛出异常
        archiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
        TarArchiveEntry entry = new TarArchiveEntry("test1/pom.xml");
        entry.setSize(4);
        archiveOutputStream.putArchiveEntry(entry);
        archiveOutputStream.write("asdf".getBytes(StandardCharsets.UTF_8));
        archiveOutputStream.closeArchiveEntry();
    }
}
