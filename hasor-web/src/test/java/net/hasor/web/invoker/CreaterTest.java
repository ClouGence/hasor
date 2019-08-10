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
package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.setting.InputStreamSettings;
import net.hasor.core.setting.StreamType;
import net.hasor.utils.ResourcesUtils;
import net.hasor.web.Invoker;
import net.hasor.web.MimeType;
import net.hasor.test.invoker.TestInvoker;
import net.hasor.test.invoker.TestInvoker2;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.io.InputStreamReader;
//
public class CreaterTest extends AbstractWeb30BinderDataTest {
    @Test
    public void pluginTest3() throws Throwable {
        InputStreamSettings settings = new InputStreamSettings();
        settings.addReader(new InputStreamReader(ResourcesUtils.getResourceAsStream("/net_hasor_web_invoker/root-creater.xml")), StreamType.Xml);
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