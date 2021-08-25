package org.example.util.compress;

import java.io.File;

public interface Compress {

    /**
     * @descriptions 压缩或打包接口。
     * @param strictMode 设置处理模式。
     *                   true-严格模式，只要有一个文件或目录打包不成功，则算打包不成功。
     *                   false-普通模式，只要有一个成功就算打包成功。
     * @return
     */
    boolean compress(File source, String dest, boolean strictMode);
}
