/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
import net.hasor.Hasor;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
/**
 * Guice模块代理成AbstractHasorModule类型的工具
 * @version : 2013-7-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class GuiceModulePropxy implements Module {
    private com.google.inject.Module guiceModule = null;
    //
    public GuiceModulePropxy(com.google.inject.Module guiceModule) {
        Hasor.assertIsNotNull(guiceModule);
        this.guiceModule = guiceModule;
    }
    //
    public void init(ApiBinder apiBinder) {
        apiBinder.getGuiceBinder().install(this.guiceModule);
    }
    public void start(AppContext appContext) {}
    public void stop(AppContext appContext) {}
}