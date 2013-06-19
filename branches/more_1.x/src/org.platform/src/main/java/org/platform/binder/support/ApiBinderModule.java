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
package org.platform.binder.support;
import org.platform.Platform;
import org.platform.binder.ApiBinder;
import org.platform.binder.FilterPipeline;
import org.platform.binder.SessionListenerPipeline;
import org.platform.context.PlatformListener;
import org.platform.context.Settings;
import com.google.inject.Binder;
import com.google.inject.Module;
/**
 * 
 * @version : 2013-4-12
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class ApiBinderModule implements Module {
    private Settings settings = null;
    private Object   context  = null;
    //
    protected ApiBinderModule(Settings settings, Object context) {
        this.settings = settings;
        this.context = context;
    }
    protected ApiBinder newApiBinder(Binder guiceBinder) {
        return new InternalApiBinder(this.settings, this.context, guiceBinder);
    }
    @Override
    public void configure(Binder binder) {
        PlatformListener[] listenerList = this.settings.getContextListeners();
        if (listenerList != null)
            for (PlatformListener listener : listenerList) {
                if (listener == null)
                    continue;
                Platform.info("send initialize to : %s", listener.getClass());
                ApiBinder apiBinder = this.newApiBinder(binder);
                listener.initialize(apiBinder);
                binder.install((Module) apiBinder);
            }
        /*Bind*/
        binder.bind(ManagedErrorPipeline.class);
        binder.bind(ManagedServletPipeline.class);
        binder.bind(FilterPipeline.class).to(ManagedFilterPipeline.class);
        binder.bind(SessionListenerPipeline.class).to(ManagedSessionListenerPipeline.class);
    }
}