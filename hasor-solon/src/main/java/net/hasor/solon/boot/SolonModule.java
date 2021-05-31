package net.hasor.solon.boot;

import net.hasor.core.ApiBinder;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.core.TypeSupplier;
import org.noear.solon.core.Aop;

import java.util.function.Supplier;

public interface SolonModule extends Module {
    /**
     * 获取 SpringTypeSupplier
     */
    default TypeSupplier springTypeSupplier(ApiBinder apiBinder) {
        return new SolonTypeSupplier();
    }

    /**
     * 使用 Spring getBean(Class) 方式获取Bean。
     */
    default <T> Supplier<T> getSupplierOfType(ApiBinder apiBinder, Class<T> targetType) {
        return (Provider<T>) () -> Aop.get(targetType);
    }

    /**
     * 使用 Spring getBean(String) 方式获取Bean。
     */
    default <T> Supplier<T> getSupplierOfName(ApiBinder apiBinder, String beanName) {
        return (Provider<T>) () -> (T) Aop.get(beanName);
    }
}
