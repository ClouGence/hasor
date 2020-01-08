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
package net.hasor.tconsole.binder;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.core.spi.ContextStartListener;
import net.hasor.tconsole.ConsoleApiBinder;

/**
 * tConsole Hasor 插件入口。
 * @version : 2019年10月30日
 * @author 赵永春 (zyc@hasor.net)
 */
public class ConsoleModule implements Module, ContextStartListener {
    private boolean enable;

    @Override
    public void loadModule(ApiBinder apiBinder) {
        this.enable = apiBinder.tryCast(ConsoleApiBinder.class) != null;
        apiBinder.bindSpiListener(ContextStartListener.class, this);
    }

    @Override
    public void doStart(AppContext appContext) {
        if (!this.enable) {
            return;
        }
        InnerExecutorManager manager = appContext.getInstance(InnerExecutorManager.class);
        manager.setAppContext(appContext);
        manager.init();
    }

    @Override
    public void doStartCompleted(AppContext appContext) {
        if (!this.enable) {
            return;
        }
        appContext.getInstance(InnerExecutorManager.class).doPreCommand(appContext);
    }

    @Override
    public void onStop(AppContext appContext) {
        if (!this.enable) {
            return;
        }
        appContext.getInstance(InnerExecutorManager.class).close();
    }
}