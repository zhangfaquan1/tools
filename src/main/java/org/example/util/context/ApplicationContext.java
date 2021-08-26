package org.example.util.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {

//    private Map<String, Object> singletonCache = new ConcurrentHashMap<>();

    public static <T> T getInstance(String className, Class<T> clazz, boolean isSingleton) {
        return isSingleton ? getSingletonInstance(className, clazz) : getPrototypeInstance(className, clazz);
    }

    public static <T> T getSingletonInstance(String className, Class<T> clazz) {
        return null;
    }

    public static <T> T getPrototypeInstance(String className, Class<T> clazz) {
        return null;
    }
}
