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
package net.hasor.plugins.spring.factory;
import java.io.IOException;
import java.net.URISyntaxException;

import net.hasor.core.environment.StandardEnvironment;
import net.hasor.core.event.EventObject;
import net.hasor.core.event.StandardEventManager;
/**
 *
 * @version : 2016年2月15日
 * @author 赵永春(zyc@hasor.net)
 */
class ShareEventStandardEnvironment extends StandardEnvironment {
    private ShareEventListener shareEventListener;
    public ShareEventStandardEnvironment(ClassLoader classLoader, Object context, String mainSettings, ShareEventListener shareEventListener) throws IOException, URISyntaxException {
        super(context, mainSettings);
        this.shareEventListener = shareEventListener;
    }
    @Override
    protected StandardEventManager createEventManager(int eventThreadPoolSize) {
        return new ShareEventStandardEventManager(eventThreadPoolSize, this.shareEventListener);
    }
}
class ShareEventStandardEventManager extends StandardEventManager {
    private ShareEventListener shareEventListener;
    public ShareEventStandardEventManager(int eventThreadPoolSize, ShareEventListener shareEventListener) {
        super(eventThreadPoolSize);
        this.shareEventListener = shareEventListener;
    }
    @Override
    protected <T> void executeEvent(final EventObject<T> eventObj) {
        super.executeEvent(eventObj);
        this.shareEventListener.fireEvent(eventObj);
    }
}