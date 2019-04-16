package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.setting.InputStreamSettings;
import net.hasor.core.setting.StreamType;
import net.hasor.utils.ResourcesUtils;
import net.hasor.web.Invoker;
import net.hasor.web.MimeType;
import net.hasor.web.invoker.beans.TestInvoker;
import net.hasor.web.invoker.beans.TestInvoker2;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.io.InputStreamReader;
//
public class CreaterTest extends AbstractWeb30BinderDataTest {
    @Test
    public void pluginTest3() throws Throwable {
        InputStreamSettings settings = new InputStreamSettings();
        settings.addReader(new InputStreamReader(ResourcesUtils.getResourceAsStream("root-creater.xml")), StreamType.Xml);
        settings.loadSettings();
        //
        Environment environment = PowerMockito.mock(Environment.class);
        PowerMockito.when(environment.getSettings()).thenReturn(settings);
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
        PowerMockito.when(appContext.getEnvironment()).thenReturn(environment);
        //
        RootInvokerCreater creater = new RootInvokerCreater(appContext);
        assert creater.createrMap.size() == 1;
        assert creater.extMapping.size() == 4;
        //
        assert creater.extMapping.containsKey(TestInvoker.class);
        assert creater.extMapping.containsKey(TestInvoker2.class);
        assert creater.extMapping.containsKey(Invoker.class);
        assert creater.extMapping.containsKey(MimeType.class);
        //
        Invoker invoker = PowerMockito.mock(Invoker.class);
        PowerMockito.when(invoker.getAppContext()).thenReturn(appContext);
        Invoker createrExt = creater.createExt(invoker);
        assert createrExt instanceof TestInvoker;
        assert createrExt instanceof TestInvoker2;
        assert createrExt instanceof Invoker;
        assert createrExt instanceof MimeType;
    }
}