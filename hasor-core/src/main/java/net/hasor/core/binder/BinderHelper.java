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
package net.hasor.core.binder;
import net.hasor.core.*;
/**
 *
 * @version : 2015年11月13日
 * @author 赵永春(zyc@hasor.net)
 */
public class BinderHelper {
    public static Module onInstall(Environment env, final Module atModule) {
        if (atModule == null) {
            return atModule;
        }
        if (atModule instanceof LifeModule) {
            /*启动*/
            Hasor.pushStartListener(env, new EventListener<AppContext>() {
                public void onEvent(String event, AppContext eventData) throws Throwable {
                    ((LifeModule) atModule).onStart(eventData);
                }
            });
            /*停止*/
            Hasor.pushShutdownListener(env, new EventListener<AppContext>() {
                public void onEvent(String event, AppContext eventData) throws Throwable {
                    ((LifeModule) atModule).onStop(eventData);
                }
            });
        }
        return atModule;
    }
}