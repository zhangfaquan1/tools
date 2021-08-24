package org.example.util.io;

import org.junit.Test;

public class TestIOUtils {

    @Test
    public void testCopyFile1() {
        IOUtils.copyFile("src/main/resources/log4j2.xml", "testIOUtils/test1.xml", true);
        IOUtils.copyFile("src/main/resources/log4j2.xml", "testIOUtils/test2.xml", false);
    }
}
