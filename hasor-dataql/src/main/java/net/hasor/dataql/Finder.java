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
        try {
            Class<?> loadClass = Thread.currentThread().getContextClassLoader().loadClass(beanName);
            return loadClass.newInstance();
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e, throwable -> new RuntimeException("load Bean failed -> '" + beanName, throwable));
        }
    }
}