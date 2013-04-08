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
package org.platform.runtime.startup;
import org.platform.api.context.ContextEvent;
import org.platform.api.context.ContextListener;
import org.platform.api.context.InitListener;
/**
 * 支持Service等注解功能。
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@InitListener(displayName = "PlatformContextListener", description = "用于支持整个平台的功能。", startIndex = 0)
public class StartupContextListener implements ContextListener {
    @Override
    public void onContextInitialized(ContextEvent event) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onContextDestroyed() {
        // TODO Auto-generated method stub
        a
    }
}