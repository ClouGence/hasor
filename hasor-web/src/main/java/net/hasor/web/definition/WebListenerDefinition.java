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
import net.hasor.core.spi.AppContextAware;

import java.util.EventListener;

/**
 *
 * @version : 2013-4-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class WebListenerDefinition implements AppContextAware {
    private BindInfo<? extends EventListener> listenerRegister = null;
    private EventListener                     target           = null;

    public WebListenerDefinition(final BindInfo<? extends EventListener> listenerRegister) {
        this.listenerRegister = listenerRegister;
    }

    @Override
    public String toString() {
        return String.format("type %s listenerKey=%s", WebListenerDefinition.class, this.listenerRegister);
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.target = appContext.getInstance(this.listenerRegister);
    }

    /*--------------------------------------------------------------------------------------------------------*/

    public <T extends EventListener> T getWebListener(Class<T> targetType) {
        if (this.target == null) {
            return null;
        }
        if (targetType.isInstance(target)) {
            return (T) target;
        }
        return null;
    }
}