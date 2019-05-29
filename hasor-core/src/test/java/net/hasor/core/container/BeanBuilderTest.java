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
        this.env = new StandardEnvironment();
    }
    @Test
    public void builderTest1() {
        BeanContainer container = new BeanContainer();
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getEnvironment()).thenReturn(this.env);
        PowerMockito.when(appContext.getClassLoader()).thenReturn(this.env.getClassLoader());
        //
        assert container.createObject(List.class, null, null, appContext, null) == null;
        assert container.createObject(Byte.TYPE, null, null, appContext, null) == (byte) 0;
        assert container.createObject(AbstractMap.class, null, null, appContext, null) == null;
        assert container.createObject(SingletonMode.class, null, null, appContext, null) == null;
        assert container.createObject(int[].class, null, null, appContext, null).length == 0;
    }
}