package org.example.util.factory;

public class ReflectFactory {

    public static <T> T getInstance(String className, Class<T> clazz) {
        T instance = null;
        try {
            // 获取类的全限定类名
            instance = (T) Class.forName(className).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }
}
