package org.example.util.compress;

import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.*;

public class TarStrategy implements Compress {

    @Override
    public boolean compress(File source, String destPath) {

        return compress(source, new File(destPath));
    }

    public boolean compress(File source, File dest) {

        boolean flag = false;

        TarArchiveOutputStream tarArchiveOutputStream =  null;
        try {
            tarArchiveOutputStream = new TarArchiveOutputStream(new FileOutputStream(dest));
        } catch (FileNotFoundException e) {

        }
        if (tarArchiveOutputStream == null)
            return false;

        // 如果file为目录类型，会不会直接创建？

        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(source));
        } catch (FileNotFoundException e) {

        }


        return flag;
    }
}
