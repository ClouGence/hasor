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
package org.platform.api.binder;
import java.util.ArrayList;
import java.util.List;
import org.platform.api.context.ContextListener;
import org.platform.runtime.Platform;
import com.google.inject.Binder;
import com.google.inject.Module;
/**
 * 
 * @version : 2013-4-12
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class SystemModule implements Module {
    private List<ContextListener> listenerList = null;
    protected SystemModule(List<ContextListener> listenerList) {
        if (listenerList == null)
            this.listenerList = new ArrayList<ContextListener>();
    }
    @Override
    public void configure(Binder binder) {
        ApiBinder apiBinder = null;
        for (ContextListener listener : listenerList) {
            if (listener == null)
                continue;
            Platform.info("send initialize to : " + Platform.logString(listener.getClass()));
            apiBinder = this.getApiBinder(binder);
            listener.initialize(apiBinder);
        }
        /*执行ApiBinder的configure，使其完成配置任务。*/
        if (apiBinder instanceof Module)
            binder.install((Module) apiBinder);
    }
    protected abstract ApiBinder getApiBinder(Binder guiceBinder);
}