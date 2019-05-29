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
package net.hasor.core.context;
import net.hasor.core.*;
import net.hasor.core.binder.TestBean;
import net.hasor.core.context.beans.ContextInjectBean;
import net.hasor.core.context.beans.ContextShutdownListenerBean;
import net.hasor.core.context.beans.ContextStartListenerBean;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
public class CreaterListenerTest {
    //
    @Test
    public void builderTest1() {
        //
        final Map<String, BindInfo> dataMap = new HashMap<>();
        final BeanCreaterListener createrListener = (newObject, bindInfo) -> dataMap.put(newObject.getClass().getName(), bindInfo);
        //
        AppContext appContext = Hasor.create().asTiny().build((Module) apiBinder -> {
            apiBinder.bindType(TestBean.class).toInstance(new TestBean()).whenCreate(createrListener);
            apiBinder.bindType(ContextInjectBean.class).whenCreate(createrListener);
            apiBinder.bindType(ContextShutdownListenerBean.class).whenCreate(createrListener);
        });
        //
        assert dataMap.isEmpty();
        appContext.getInstance(TestBean.class);
        appContext.getInstance(ContextInjectBean.class);
        appContext.getInstance(ContextShutdownListenerBean.class);
        appContext.getInstance(ContextStartListenerBean.class);
        //
        assert dataMap.containsKey(TestBean.class.getName());
        assert dataMap.containsKey(ContextInjectBean.class.getName());
        assert dataMap.get(ContextInjectBean.class.getName()) != null;
        assert dataMap.containsKey(ContextShutdownListenerBean.class.getName());
        assert dataMap.get(ContextShutdownListenerBean.class.getName()) != null;
        assert !dataMap.containsKey(ContextStartListenerBean.class.getName());
        assert dataMap.get(ContextStartListenerBean.class.getName()) == null;
        //
    }
}