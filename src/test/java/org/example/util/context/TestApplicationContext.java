package org.example.util.context;

import org.example.util.compress.ZipStrategy;
import org.junit.Test;

import java.io.File;

public class TestApplicationContext {

    @Test
    public void testGetSingletonInstance() {
        ApplicationContext applicationContext = new ApplicationContext();
        ZipStrategy zipStrategy = applicationContext.getInstance("org.example.util.compress.ZipStrategy", ZipStrategy.class, true);
        boolean compress = zipStrategy.compress(new File("E:\\Work\\bct"), "bct.zip", true);
        System.out.println(compress);
    }

    @Test
    public void testGetPrototypeInstance() {
        ApplicationContext applicationContext = new ApplicationContext();
        ZipStrategy zipStrategy = applicationContext.getInstance("org.example.util.compress.ZipStrategy", ZipStrategy.class, false);
        boolean compress = zipStrategy.compress(new File("E:\\Work\\bct"), "bct.zip", true);
        System.out.println(compress);
    }
}
