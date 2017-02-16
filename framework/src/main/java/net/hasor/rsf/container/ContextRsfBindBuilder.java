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
package net.hasor.rsf.container;
import net.hasor.core.AppContextAware;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfEnvironment;
/**
 * 服务注册器
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
abstract class ContextRsfBindBuilder extends AbstractRsfBindBuilder {
    protected abstract RsfBeanContainer getContainer();

    protected abstract RsfContext getRsfContext();
    public RsfEnvironment getEnvironment() {
        return this.getRsfContext().getEnvironment();
    }
    protected <T> RsfBindInfo<T> addService(ServiceDefine<T> serviceDefine) {
        getContainer().publishService(serviceDefine);
        return serviceDefine;
    }
    protected void addShareFilter(FilterDefine filterDefine) {
        this.getContainer().publishFilter(filterDefine);
    }
    @Override
    protected <T extends AppContextAware> T makeSureAware(T aware) {
        aware.setAppContext(getRsfContext().getAppContext());
        return aware;
    }
}