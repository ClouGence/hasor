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
package net.hasor.web.render;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.utils.StringUtils;
import net.hasor.web.RenderEngine;

import java.util.List;
/**
 * 渲染引擎定义。
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
class RenderDefinition {
    private List<String>                     renderSet = null;
    private BindInfo<? extends RenderEngine> bindInfo  = null;
    //
    public RenderDefinition(List<String> renderSet, BindInfo<? extends RenderEngine> bindInfo) {
        this.renderSet = renderSet;
        this.bindInfo = bindInfo;
    }
    //
    @Override
    public String toString() {
        return String.format("type %s pattern=%s ,uriPatternType=%s", //
                RenderDefinition.class, StringUtils.join(this.renderSet.toArray(), ","), this.bindInfo.toString());
    }
    //
    public String getID() {
        return this.bindInfo.getBindID();
    }
    public List<String> getRenderSet() {
        return this.renderSet;
    }
    public RenderEngine newEngine(AppContext appContext) throws Throwable {
        RenderEngine engine = appContext.getInstance(this.bindInfo);
        engine.initEngine(appContext);
        return engine;
    }
}