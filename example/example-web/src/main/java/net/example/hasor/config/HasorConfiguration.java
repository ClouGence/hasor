package net.example.hasor.config;
import net.hasor.core.AppContext;
import net.hasor.core.DimModule;
import net.hasor.core.Hasor;
import net.hasor.core.TypeSupplier;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.web.startup.RuntimeFilter;
import net.hasor.web.startup.RuntimeListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import java.util.Collections;

@Configuration
public class HasorConfiguration implements InitializingBean, ApplicationContextAware {
    private AppContext         appContext;
    private ApplicationContext applicationContext;
    private TypeSupplier       typeSupplier;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.typeSupplier = applicationContext::getBean;
    }

    @Override
    public void afterPropertiesSet() {
        ServletContext sc = applicationContext.getBean(ServletContext.class);
        this.appContext = Hasor.create(sc).build(apiBinder -> {
            apiBinder.loadModule(apiBinder.findClass(DimModule.class), Matchers.anyClass(), typeSupplier);
        });
    }

    @Bean
    public FilterRegistrationBean<RuntimeFilter> hasorRuntimeFilter() {
        FilterRegistrationBean<RuntimeFilter> filterBean = //
                new FilterRegistrationBean<>(new RuntimeFilter());
        filterBean.setUrlPatterns(Collections.singletonList("/*"));
        //filterBean.setAsyncSupported(true);
        return filterBean;
    }

    @Bean
    public ServletListenerRegistrationBean<RuntimeListener> hasorRuntimeListener() {
        ServletListenerRegistrationBean<RuntimeListener> listenerBean = //
                new ServletListenerRegistrationBean<>(new RuntimeListener(this::getAppContext));
        return listenerBean;
    }

    @Bean
    public AppContext getAppContext() {
        return this.appContext;
    }
}
