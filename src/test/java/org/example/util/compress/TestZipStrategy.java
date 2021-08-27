package org.example.util.compress;

import org.apache.commons.compress.archivers.zip.*;
import org.junit.Test;

import java.io.*;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

public class TestZipStrategy {

    // 解压指定文件 - 随机
    @Test
    public void testUnCompress() throws IOException {
        ZipFile zipFile = new ZipFile("E:\\Work\\test1.zip");
        ZipArchiveEntry entry = zipFile.getEntry("test1/src/test/java/a.java"); // 根据文件路径，解压指定文件
        try (InputStream inputStream = zipFile.getInputStream(entry)) {
            byte[] buffer = new byte[4096];
            File outputFile = new File("E:\\Work\\a.java");
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                while (inputStream.read(buffer) > 0) {
                    fos.write(buffer);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 解压全部文件 - 随机
    @Test
    public void testUncompress2() throws IOException {
        ZipFile zipFile = new ZipFile(new File("E:\\Work\\test1.zip"));
        byte[] buffer = new byte[4096];
        ZipArchiveEntry entry;
        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries(); // 获取全部文件的迭代器
        InputStream inputStream;
        while (entries.hasMoreElements()) {
            entry = entries.nextElement();
            if (entry.isDirectory()) {
//                continue;
            }

            File outputFile = new File("E:\\Work\\output\\" + entry.getName());

            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }

            inputStream = zipFile.getInputStream(entry);
            int len;
            try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                while ((len = inputStream.read(buffer)) != 0) {
                    fos.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 解压文件 - 顺序
    @Test
    public void testUnCompress3() {

        /*
        *
        * Zip文件的头信息，实际上是在Zip文件的最后的，需要先读取Zip的最后一部分信息，然后再往前跳转着读取，如果Zip文件本身就在硬盘或者内存中的话，随机访问的效率还是很高的。
        * 对于一些IO场景，比如网络IO之类，我们倒是可以把整个Zip文件读到内存中，然后再随机访问进行解压。不过如果遇到对一些比较大的zip，或者内存敏感（比如手机），这样的成本可能就太高了。
        * */
        File file = new File("E:\\Work\\test1.zip");
        try (ZipArchiveInputStream zipInputStream = new ZipArchiveInputStream(new FileInputStream(file))) {
            ZipArchiveEntry entry = zipInputStream.getNextZipEntry();
            entry.getName(); // 这里同样可以获取包括名字在内的许多文件信息
            entry = zipInputStream.getNextZipEntry(); // 如果我们不需要读取第一个文件，可以直接跳到下一个文件
            File outputFile = new File("E:\\Work\\output\\" + entry.getName());

            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }

            byte[] buffer = new byte[4096];
            int len;
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                while ((len = zipInputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 分卷解压
    @Test
    public void testUnCompress4() throws IOException {
        // 可以通过最后一个分卷zip文件创建channel，注意需要保证所有分卷文件都在同一目录下，并且除后缀名之外文件名相同
        File lastSegmentFile = new File("/root/test.zip");
        SeekableByteChannel channel = ZipSplitReadOnlySeekableByteChannel.buildFromLastSplitSegment(lastSegmentFile);

        // 也可以通过指定所有zip分卷文件创建channel
        File firstSegmentFile = new File("/root/test.z01");
        File secondSegmentFile = new File("/root/test.z02");
        File thirdSegmentFile = new File("/root/test.zip");
        SeekableByteChannel channel2 = ZipSplitReadOnlySeekableByteChannel.forFiles(firstSegmentFile, secondSegmentFile, thirdSegmentFile);

//        然后调用ZipFile或者ZipArchiveInputStream解压
        ZipFile zipFile = new ZipFile(channel);
    }

    // 压缩
    @Test
    public void testCompress() {
        File archive = new File("E:\\Work\\test1.zip"); // 指定压缩包生成路径（含包名）。注意会覆盖已存在的同名文件。
        /*
         * 分卷压缩
         * 只需要在ZipArchiveOutputStream的构造函数中，传一个希望的分卷文件大小，其他代码完全相同，这里需要简单注意一下，zip合法的分卷大小在64kb到4gb之间，超出此范围的值会报错
         * */
        try (ZipArchiveOutputStream outputStream = new ZipArchiveOutputStream(archive)) {
            ZipArchiveEntry entry = new ZipArchiveEntry("testdata/test1.xml"); // 指定将在压缩包中创建的路径
            // 可以设置压缩等级
            outputStream.setLevel(5);
            // 可以设置压缩算法，当前支持ZipEntry.DEFLATED和ZipEntry.STORED两种
            outputStream.setMethod(ZipEntry.DEFLATED);
            // 也可以为每个文件设置压缩算法
            entry.setMethod(ZipEntry.DEFLATED);
            // 在zip中创建一个文件
            outputStream.putArchiveEntry(entry);
            // 并写入内容
            outputStream.write("abcd\n".getBytes(StandardCharsets.UTF_8));
            // 完成一个文件的写入
            outputStream.closeArchiveEntry();

            entry = new ZipArchiveEntry("testdata/test2.xml");
            entry.setMethod(ZipEntry.STORED);
            outputStream.putArchiveEntry(entry);
            outputStream.write("efgh\n".getBytes(StandardCharsets.UTF_8));
            outputStream.closeArchiveEntry();

            entry = new ZipArchiveEntry("testdata1/"); // 创建空目录。注意需要带上/，否则创建的是一个文件。
            entry.setMethod(ZipEntry.STORED);
            outputStream.putArchiveEntry(entry);
            outputStream.write("efgh\n".getBytes(StandardCharsets.UTF_8));
            outputStream.closeArchiveEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCompress2() {
        ZipStrategy zipStrategy = new ZipStrategy();
        boolean compress = zipStrategy.compress(new File("E:\\Work\\scan-cdr"), "scan-cdr.zip", true);
        System.out.println(compress);
    }

    @Test
    public void testCompress3() {
        ZipStrategy zipStrategy = new ZipStrategy();
        boolean compress = zipStrategy.compress(new File("E:\\Work\\dumplib"), "dumplib.zip", 1024*1024, true);
        System.out.println(compress);
    }

    @Test
    public void testGetEntries() throws IOException {
        ZipFile zipFile = new ZipFile("scan-cdr.zip");
        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        System.out.println(entries);
        ZipArchiveEntry a = zipFile.getEntry("a");

        System.out.println(a);
        Iterable<ZipArchiveEntry> entries1 = zipFile.getEntries("scan-cdr/src/");
        entries1.forEach(zipArchiveEntry -> {
            System.out.println(zipArchiveEntry.getName());
        });
    }

    @Test
    public void testUncompress() {
        AbstractCompress zipStrategy = new ZipStrategy();
        boolean b = zipStrategy.unCompress(new File("scan-cdr.zip"), "E:\\Work\\de", true);
        System.out.println(b);
    }

    @Test
    public void testUncompress3() {
        ZipStrategy zipStrategy = new ZipStrategy();
        boolean b = zipStrategy.unCompressSingleByRandom(new File("scan-cdr.zip"), "E:\\Work\\de", "scan-cdr/src/main/java/org/example/Test.java", 1024, AbstractCompress.DEFAULT_BUFFER_SIZE);
        System.out.println(b);
    }

    @Test
    public void testUncompress4() {
        ZipStrategy zipStrategy = new ZipStrategy();
        boolean b = zipStrategy.unCompressAllByOrder(new File("scan-cdr.zip"), "E:\\Work\\d", true, 1024, AbstractCompress.DEFAULT_BUFFER_SIZE);
        System.out.println(b);
    }
}
