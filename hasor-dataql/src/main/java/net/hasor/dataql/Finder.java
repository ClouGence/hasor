package net.hasor.dataql;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.ResourcesUtils;

import java.io.InputStream;

public interface Finder {
    public static final Finder DEFAULT = new Finder() {
    };

    public default InputStream findResource(String resourceName) {
        // .加载资源
        InputStream inputStream = null;
        try {
            inputStream = ResourcesUtils.getResourceAsStream(resourceName);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e, throwable -> new RuntimeException("import compiler failed -> '" + resourceName + "' not found.", throwable));
        }
        return inputStream;
    }

    public default Object findBean(String beanName) {
        // .确定ClassLoader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Class c = classLoader.loadClass(Query.class.getName());
            if (c != Query.class) {
                classLoader = Query.class.getClassLoader();
            }
        } catch (ClassNotFoundException cnfe) { /* ignore */ }
        // .加载类并创建对象
        try {
            Class<?> loadClass = classLoader.loadClass(beanName);
            return loadClass.newInstance();
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e, throwable -> new RuntimeException("load Bean failed -> '" + beanName, throwable));
        }
    }
}