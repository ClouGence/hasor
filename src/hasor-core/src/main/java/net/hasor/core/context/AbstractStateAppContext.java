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
package net.hasor.core.context;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Module;
/**
 * {@link AppContext}接口默认实现。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractStateAppContext extends AbstractAppContext {
    private AbstractAppContext parent;
    private Environment        environment;
    //
    public AbstractAppContext getParent() {
        return this.parent;
    }
    /**获取环境接口。*/
    public Environment getEnvironment() {
        if (this.environment == null)
            this.environment = this.createEnvironment();
        return this.environment;
    }
    /**创建环境对象*/
    protected abstract Environment createEnvironment();
    //
    @Override
    public Module addModule(Module hasorModule) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public boolean removeModule(Module hasorModule) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public Module[] getModules() {
        // TODO Auto-generated method stub
        return null;
    }s
}