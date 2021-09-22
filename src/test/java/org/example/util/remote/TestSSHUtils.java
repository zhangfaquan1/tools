package org.example.util.remote;

import ch.ethz.ssh2.Connection;
import org.junit.Test;

public class TestSSHUtils {

    @Test
    public void testUploadFile() {
        Connection root = SSHUtils.getSSHConnection("192.168.136.151", "root", "123456");
        boolean b = SSHUtils.uploadFile(root, "E:\\Work\\ops-parse-cdr\\compress\\10000000_2021-09-1711-20-47.tar.gz", "/root");
        System.out.println(b);
    }
}
