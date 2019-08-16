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
package net.hasor.web.binder;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.web.RenderEngine;
import net.hasor.web.annotation.Render;

import java.lang.annotation.Annotation;

/**
 * 渲染引擎定义。
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class RenderDef {
    private Render                           renderInfo = null;
    private BindInfo<? extends RenderEngine> bindInfo   = null;

    public RenderDef(String renderName, String specialMimeType, BindInfo<? extends RenderEngine> bindInfo) {
        this.renderInfo = new Render() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Render.class;
            }

            @Override
            public String name() {
                return renderName;
            }

            @Override
            public String specialMimeType() {
                return specialMimeType;
            }
        };
        this.bindInfo = bindInfo;
    }

    @Override
    public String toString() {
        return String.format("rendName=%s specialMimeType=%s ,toBindID=%s", //
                this.renderInfo.name(), this.renderInfo.specialMimeType(), this.bindInfo.getBindID());
    }

    public String getID() {
        return this.bindInfo.getBindID();
    }

    public Render getRenderInfo() {
        return this.renderInfo;
    }

    public RenderEngine newEngine(AppContext appContext) throws Throwable {
        RenderEngine engine = appContext.getInstance(this.bindInfo);
        engine.initEngine(appContext);
        return engine;
    }
}