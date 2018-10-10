package net.hasor.core.context;
import net.hasor.core.Environment;
import net.hasor.core.container.BeanContainer;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.context.mods.SimpleModule;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
public class ModuleTest {
    private TemplateAppContext appContext;
    @Before
    public void testBefore() throws IOException {
        final StandardEnvironment env = new StandardEnvironment(null, null);
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
    public void builderTest1() {
        SimpleModule simpleModule = new SimpleModule();


//        appContext.doInitialize();


        List<String> says = appContext.findBindingBean(String.class);

    }
}