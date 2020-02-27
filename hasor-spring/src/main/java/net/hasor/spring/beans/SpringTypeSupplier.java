package net.hasor.spring.beans;
import net.hasor.core.TypeSupplier;
import org.springframework.context.ApplicationContext;

public class SpringTypeSupplier implements TypeSupplier {
    private ApplicationContext applicationContext;

    public SpringTypeSupplier(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> T get(Class<? extends T> targetType) {
        return applicationContext.getBean(targetType);
    }

    @Override
    public <T> boolean test(Class<? extends T> targetType) {
        return applicationContext.containsBean(targetType.getName());
    }
}
