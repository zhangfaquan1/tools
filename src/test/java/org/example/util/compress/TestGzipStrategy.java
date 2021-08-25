package org.example.util.compress;

import org.junit.Test;

import java.io.File;

public class TestGzipStrategy {

    @Test
    public void testCompress() {
        GzipStrategy gzipStrategy = new GzipStrategy();
        boolean compress = gzipStrategy.compress(new File("E:\\Work\\test1"), "E:\\Work\\test2.tar.gz", true);
        System.out.println(compress);
    }
}
