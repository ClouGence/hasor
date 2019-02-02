package net.hasor.web.definition;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.web.MappingDiscoverer;
import net.hasor.web.invoker.beans.TestMappingDiscoverer;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.util.concurrent.atomic.AtomicBoolean;
//
public class MappingDiscovererTest {
    @Test
    public void mappingDiscovererTest1() throws Throwable {
        final AtomicBoolean initCall = new AtomicBoolean(false);
        //
        BindInfo<? extends MappingDiscoverer> bindInfo = PowerMockito.mock(BindInfo.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                initCall.set(true);
                return new TestMappingDiscoverer();
            }
        });
        MappingDiscovererDefinition definition = new MappingDiscovererDefinition(bindInfo);
        //
        definition.setAppContext(appContext);
        definition.discover(null);
        definition.toString();
        //
        assert initCall.get();
    }
}