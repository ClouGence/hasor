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
package org.platform.api.context;
import java.util.HashMap;
import org.platform.api.event.PlatformEvent;
import com.google.inject.Binder;
/**
 * 在平台初始化时会引发这个事件，在整个init过程{@link ContextEvent}事件对象只有一个。
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class ContextEvent extends HashMap<String, Object> implements PlatformEvent {
    private static final long     serialVersionUID = -5713833368806822664L;
    private transient InitContext initContext      = null;
    /**构建InitEvent对象。*/
    protected ContextEvent(InitContext initContext) {
        this.initContext = initContext;
    }
    /**获取Config*/
    public InitContext getConfig() {
        return initContext;
    }
    /**获取用于初始化Guice的Binder。*/
    public abstract Binder getBinder();
}