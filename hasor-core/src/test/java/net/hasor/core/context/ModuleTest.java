package net.hasor.core.context;
import net.hasor.core.Environment;
import net.hasor.core.container.BeanContainer;
import net.hasor.core.environment.StandardEnvironment;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
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
    public void moduleTest1() throws IOException {
        final StandardEnvironment env = new StandardEnvironment(null, null);
        final BeanContainer container = new BeanContainer();
        TemplateAppContext appContext = new TemplateAppContext() {
            @Override
            protected BeanContainer getContainer() {
                return container;
            }
            @Override
            public Environment getEnvironment() {
                return env;
            }
        };
        //
        appContext.getEnvironment().getSettings().removeSetting("hasor.modules.module");
        appContext.getEnvironment().getSettings().setSetting("hasor.modules.module", "net.hasor.core.context.mods.ErrorModule");
        //
        assert appContext.findModules().length == 1;
        appContext.getEnvironment().getSettings().setSetting("hasor.modules.loadModule", "false");
        assert appContext.findModules().length == 0;
    }
    //
    @Test
    public void moduleTest2() throws IOException {
        final StandardEnvironment env = new StandardEnvironment(null, null);
        final BeanContainer container = new BeanContainer();
        TemplateAppContext appContext = new TemplateAppContext() {
            @Override
            protected BeanContainer getContainer() {
                return container;
            }
            @Override
            public Environment getEnvironment() {
                return env;
            }
        };
        //
        appContext.getEnvironment().getSettings().removeSetting("hasor.modules.module");
        appContext.getEnvironment().getSettings().setSetting("hasor.modules.module", "ssss.ErrorModule");
        //
        try {
            appContext.findModules();
            assert false;
        } catch (RuntimeException e) {
            assert "java.lang.ClassNotFoundException - ssss.ErrorModule".equals(e.getMessage()) && e.getCause() instanceof ClassNotFoundException;
        }
        //
        //
        appContext.getEnvironment().getSettings().setSetting("hasor.modules.throwLoadError", "false");
        try {
            appContext.findModules();
            assert true;
        } catch (RuntimeException e) {
            assert false;
        }
    }
    //
    @Test
    public void moduleTest3() throws IOException {
        try {
            appContext.installModule(null, null);
            assert true;
        } catch (Throwable throwable) {
            assert false;
        }
        //
        try {
            appContext.installModule(appContext.newApiBinder(), null);
            assert true;
        } catch (Throwable throwable) {
            assert false;
        }
    }
}