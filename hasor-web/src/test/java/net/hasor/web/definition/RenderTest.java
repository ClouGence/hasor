package net.hasor.web.definition;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.web.RenderEngine;
import net.hasor.web.definition.beans.TestRenderEngine;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
//
public class RenderTest {
    @Test
    public void webPluginTest1() throws Throwable {
        final AtomicBoolean initCall = new AtomicBoolean(false);
        //
        BindInfo<? extends RenderEngine> bindInfo = PowerMockito.mock(BindInfo.class);
        PowerMockito.when(bindInfo.getBindID()).thenReturn("TEST");
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                initCall.set(true);
                return new TestRenderEngine();
            }
        });
        //
        List<String> ends = new ArrayList<String>();
        ends.add("HTML");
        RenderDefinition definition = new RenderDefinition(ends, bindInfo);
        //
        //
        TestRenderEngine.resetCalls();
        assert !TestRenderEngine.isInitEngineCall();
        assert !TestRenderEngine.isProcessCall();
        assert !TestRenderEngine.isExistCall();
        //
        RenderEngine renderEngine = definition.newEngine(appContext);
        assert renderEngine != null;
        assert TestRenderEngine.isInitEngineCall();
        //
        definition.toString();
        assert definition.getRenderSet() == ends;
        assert "TEST".equals(definition.getID());
    }
}