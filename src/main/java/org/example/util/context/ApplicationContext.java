package org.example.util.context;

import org.example.util.cache.AbstractCacheSynchronizer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ApplicationContext {

    public static <T> T getInstance(String className, Class<T> clazz, boolean isSingleton) {
        return isSingleton ? getSingletonInstance(className, clazz) : getPrototypeInstance(className, clazz);
    }

    public static <T> T getSingletonInstance(String className, Class<T> clazz) {
        return null;
    }

    public static <T> T getPrototypeInstance(String className, Class<T> clazz) {
        return null;
    }

    static final class SingletonCache extends AbstractCacheSynchronizer {

        private Map<String, Object> singletonCache = new HashMap<>();

        public static <R> R getData() {

        }
    }
}
