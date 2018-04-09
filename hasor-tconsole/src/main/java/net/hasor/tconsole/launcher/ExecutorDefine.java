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
package net.hasor.tconsole.launcher;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.tconsole.CommandExecutor;
/**
 * RSF命令
 * @version : 2016年4月3日
 * @author 赵永春 (zyc@hasor.net)
 */
class ExecutorDefine implements CommandExecutor, AppContextAware {
    private String[]                            name;
    private BindInfo<? extends CommandExecutor> bindInfo;
    private AppContext                          appContext;
    private CommandExecutor                     executorTarget;
    public ExecutorDefine(String[] name, BindInfo<? extends CommandExecutor> bindInfo) {
        this.name = name;
        this.bindInfo = bindInfo;
    }
    public String[] getNames() {
        return this.name;
    }
    private CommandExecutor getTarget() {
        if (this.executorTarget == null) {
            this.executorTarget = this.appContext.getInstance(this.bindInfo);
        }
        return this.executorTarget;
    }
    //
    @Override
    public String helpInfo() {
        return getTarget().helpInfo();
    }
    @Override
    public boolean inputMultiLine(CmdRequest request) {
        return getTarget().inputMultiLine(request);
    }
    @Override
    public String doCommand(CmdRequest request) throws Throwable {
        return getTarget().doCommand(request);
    }
    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    public String getTargetClassName() {
        Class<?> sourceType = ((AbstractBindInfoProviderAdapter) this.bindInfo).getSourceType();
        if (sourceType != null) {
            return sourceType.getName();
        }
        Provider<?> sourceProvider = ((AbstractBindInfoProviderAdapter) this.bindInfo).getCustomerProvider();
        if (sourceProvider != null && sourceProvider instanceof InstanceProvider) {
            return ((InstanceProvider) sourceProvider).get().toString();
        }
        return this.bindInfo.toString();
    }
}