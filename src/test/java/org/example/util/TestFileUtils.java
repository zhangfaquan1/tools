package org.example.util;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestFileUtils {

    @Test
    public void testGetFilePath() {

        String[] path = new String[]{"d:\\  asssd\\   csssa   ", "ss\\    ", "\\eee"};
        String[] path2 = new String[]{"  /  asssd/   csssa   ", "  ss/    ", "  /e  ee  "};
        String[] path3 = new String[]{"  \\  asssd/   csssa   ", "  ss\\    ", "  /e  ee  "};

        String filePath = FileUtils.getFilePath(path);
        System.out.println(filePath);

        String filePath2 = FileUtils.getFilePath(path2);
        System.out.println(filePath2);

        String filePath3 = FileUtils.getFilePath(path3);
        System.out.println(filePath3);
    }

    @Test
    public void testGetAllFile() {
        // 获取指定目录下的所有文件及目录
        File file = new File("H:\\学习笔记\\web\\后端\\java\\基础\\JavaSE\\IO流");
        List<File> fileList = FileUtils.getAllFile(file);
        System.out.println(fileList);
    }

    @Test
    public void testGetSpecifiedFiles() {
        // 获取指定目录下的所有.txt文件
        File file = new File("H:\\学习笔记\\web\\后端\\java\\基础\\JavaSE\\IO流");
        List<File> fileList = FileUtils.getSpecifiedFiles(file, file1 -> file1.getName().toLowerCase().endsWith(".txt"));
        System.out.println(fileList);
    }

    @Test
    public void testCreateFile() throws IOException {
        boolean flag = FileUtils.createFile("G:\\a\\b\\c\\d.txt");
        System.out.println(flag);
    }

    @Test
    public void test4() {
        boolean b = FileUtils.deleteFile("G:\\a");
        System.out.println(b);
    }
}
