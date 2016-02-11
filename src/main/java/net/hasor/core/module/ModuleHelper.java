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
package net.hasor.core.module;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.core.LifeModule;
import net.hasor.core.Module;
/**
 * 
 * @version : 2015年11月13日
 * @author 赵永春(zyc@hasor.net)
 */
public class ModuleHelper {
    public static Module onInstall(Environment env, final Module atModule) {
        if (atModule == null) {
            return atModule;
        }
        if (atModule instanceof LifeModule) {
            /*启动*/Hasor.pushStartListener(env, new EventListener() {
                public void onEvent(String event, Object[] params) throws Throwable {
                    ((LifeModule) atModule).onStart((AppContext) params[0]);
                }
            });
            /*停止*/Hasor.pushShutdownListener(env, new EventListener() {
                public void onEvent(String event, Object[] params) throws Throwable {
                    ((LifeModule) atModule).onStop((AppContext) params[0]);
                }
            });
        }
        return atModule;
    }
}