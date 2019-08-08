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
package net.hasor.web.definition;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.spi.AppContextAware;
import net.hasor.web.Mapping;
import net.hasor.web.MappingDiscoverer;

/**
 * WebPlugin 定义
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class MappingDiscovererDefinition implements MappingDiscoverer, AppContextAware {
    private BindInfo<? extends MappingDiscoverer> bindInfo   = null;
    private AppContext                            appContext = null;

    public MappingDiscovererDefinition(final BindInfo<? extends MappingDiscoverer> bindInfo) {
        this.bindInfo = bindInfo;
    }

    protected MappingDiscoverer getTarget() {
        return this.appContext.getInstance(this.bindInfo);
    }

    @Override
    public String toString() {
        return String.format("type %s listenerKey=%s", MappingDiscovererDefinition.class, this.bindInfo);
    }

    /*--------------------------------------------------------------------------------------------------------*/

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public void discover(Mapping mappingData) {
        MappingDiscoverer plugin = this.getTarget();
        if (plugin != null) {
            plugin.discover(mappingData);
        }
    }
}