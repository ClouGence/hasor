package net.hasor.core.container;
import net.hasor.core.AppContext;
import net.hasor.core.SingletonMode;
import net.hasor.core.environment.StandardEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
public class BeanBuilderTest {
    private StandardEnvironment env;
    @Before
    public void testBefore() throws IOException {
        this.env = new StandardEnvironment(null, null);
    }
    @Test
    public void builderTest1() {
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        assert container.createObject(List.class, null, null, appContext) == null;
        assert container.createObject(Byte.TYPE, null, null, appContext) == (byte) 0;
        assert container.createObject(AbstractMap.class, null, null, appContext) == null;
        assert container.createObject(SingletonMode.class, null, null, appContext) == null;
        assert container.createObject(int[].class, null, null, appContext).length == 0;
    }
}
