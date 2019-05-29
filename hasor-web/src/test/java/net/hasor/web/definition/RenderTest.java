/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.web.definition;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.web.RenderEngine;
import net.hasor.web.definition.beans.TestRenderEngine;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

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
        PowerMockito.when(appContext.getInstance(bindInfo)).then((Answer<Object>) invocationOnMock -> {
            initCall.set(true);
            return new TestRenderEngine();
        });
        //
        RenderDefinition definition = new RenderDefinition("HTML", "abc", bindInfo);
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
        assert definition.getRenderInfo().name().equals("HTML");
        assert definition.getRenderInfo().specialMimeType().equals("abc");
        assert "TEST".equals(definition.getID());
    }
}