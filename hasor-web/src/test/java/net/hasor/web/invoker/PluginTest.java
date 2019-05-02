package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import net.hasor.web.invoker.beans.TestWebPlugin;
import org.junit.Test;

import java.util.HashMap;
//
public class PluginTest extends AbstractWeb30BinderDataTest {
    @Test
    public void pluginTest3() throws Throwable {
        AppContext appContext = hasor.build((WebModule) apiBinder -> apiBinder.addPlugin(new TestWebPlugin()));
        //
        TestWebPlugin.resetInit();
        InvokerContext invokerContext = new InvokerContext();
        invokerContext.initContext(appContext, new HashMap<String, String>() {{
        }});
        //
        assert !TestWebPlugin.isAfterFilter();
        assert !TestWebPlugin.isBeforeFilter();
        invokerContext.beforeFilter(null, null);
        invokerContext.afterFilter(null, null);
        assert TestWebPlugin.isAfterFilter();
        assert TestWebPlugin.isBeforeFilter();
    }
}