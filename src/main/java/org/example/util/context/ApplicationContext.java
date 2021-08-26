package org.example.util.context;

import org.example.util.cache.AbstractCacheSynchronizer;
import org.example.util.factory.ReflectFactory;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {

    private final SingletonCache singleton = new SingletonCache();

    public <T> T getInstance(String className, Class<T> clazz, boolean isSingleton) {
        return isSingleton ? getSingletonInstance(className, clazz) : getPrototypeInstance(className, clazz);
    }

    public <T> T getSingletonInstance(String className, Class<T> clazz) {
        return singleton.getData(className, clazz);
    }

    public <T> T getPrototypeInstance(String className, Class<T> clazz) {
        return ReflectFactory.getInstance(className, clazz);
    }

    static final class SingletonCache extends AbstractCacheSynchronizer {

        private Map<String, Object> singletonCache = new HashMap<>();

        @SuppressWarnings("unchecked")
        public <R> R getData(String className, Class<R> clazz) {
            return super.getData(() -> (R) singletonCache.get(className), () -> {
                singletonCache.put(className, ReflectFactory.getInstance(className, clazz));
            });
        }
    }
}
