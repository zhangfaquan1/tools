package org.example.util.compress;

import org.junit.Test;

import java.io.File;

public class TestSevenStrategy {

    @Test
    public void testCompress() {
        SevenStrategy sevenStrategy = new SevenStrategy();
        boolean compress = sevenStrategy.compress(new File("E:\\Work\\scan-cdr"), "scan-cdr.7z", true);
//        boolean compress = sevenStrategy.compress(new File("E:\\Work\\bct"), "bct.7z", true);
        System.out.println(compress);
    }

    @Test
    public void testUncompress() {
        AbstractCompress sevenStrategy = new SevenStrategy();
        sevenStrategy.unCompress(new File("scan-cdr.7z"), "E:\\Work\\scan-7z", true);
    }
}
