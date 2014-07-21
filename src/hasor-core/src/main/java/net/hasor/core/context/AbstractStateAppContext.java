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
import java.util.ArrayList;
import java.util.List;
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
    @Override
    public AbstractAppContext getParent() {
        return this.parent;
    }
    /**获取环境接口。*/
    @Override
    public Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = this.createEnvironment();
        }
        return this.environment;
    }
    /**创建环境对象*/
    protected abstract Environment createEnvironment();
    //
    /*-------------------------------------------------------------------------------------Module*/
    private List<Module> tempModuleSet = new ArrayList<Module>();
    /**获得所有模块*/
    @Override
    public final Module[] getModules() {
        List<Module> moduleList = this.tempModuleSet;
        Module[] infoArray = new Module[moduleList.size()];
        for (int i = 0; i < moduleList.size(); i++) {
            infoArray[i] = moduleList.get(i);
        }
        return infoArray;
    }
    /**添加模块，如果容器已经初始化那么会引发{@link IllegalStateException}异常。*/
    @Override
    public synchronized Module addModule(final Module module) {
        if (this.isStart()) {
            throw new IllegalStateException("context is started.");
        }
        this.tempModuleSet.add(module);
        return module;
    }
    /**删除模块，如果容器已经初始化那么会引发{@link IllegalStateException}异常。*/
    @Override
    public synchronized boolean removeModule(final Module hasorModule) {
        if (this.isStart()) {
            throw new IllegalStateException("context is started.");
        }
        return this.tempModuleSet.remove(hasorModule);
    }
}
