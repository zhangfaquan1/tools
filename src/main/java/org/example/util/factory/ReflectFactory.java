package org.example.util.factory;

public class ReflectFactory {

    /**
     * @descriptions 利用反射机制获取实例
     * @author zhangfaquan
     * @date 2021/8/26 20:32
     * @param className 全限定类名
     * @param clazz class
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(String className, Class<T> clazz) {
        T instance = null;
        try {
            instance = (T) Class.forName(className).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }
}
