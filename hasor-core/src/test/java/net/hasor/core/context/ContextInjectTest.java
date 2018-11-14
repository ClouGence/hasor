package net.hasor.core.context;
import net.hasor.core.Environment;
import net.hasor.core.container.BeanContainer;
import net.hasor.core.context.beans.ContextInjectBean;
import net.hasor.core.environment.StandardEnvironment;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
public class ContextInjectTest {
    private TemplateAppContext appContext;
    @Before
    public void testBefore() throws IOException {
        final StandardEnvironment env = new StandardEnvironment();
        final BeanContainer container = new BeanContainer();
        this.appContext = new TemplateAppContext() {
            @Override
            protected BeanContainer getContainer() {
                return container;
            }
            @Override
            public Environment getEnvironment() {
                return env;
            }
        };
    }
    //
    @Test
    public void builderTest1() throws Throwable {
        ContextInjectBean injectBean = null;
        //
        injectBean = this.appContext.getInstance(ContextInjectBean.class);
        assert injectBean.getAppContext() == null;
        assert injectBean.getEnvironment() == null;
        assert injectBean.getEventContext() == null;
        assert injectBean.getSettings() == null;
        //
        this.appContext.start();
        injectBean = this.appContext.getInstance(ContextInjectBean.class);
        assert injectBean.getAppContext() != null;
        assert injectBean.getEnvironment() != null;
        assert injectBean.getEventContext() != null;
        assert injectBean.getSettings() != null;
    }
}