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
package net.hasor.dataql.binder;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.spi.AppContextAware;
import net.hasor.dataql.Finder;
import net.hasor.dataql.Udf;
import net.hasor.dataql.UdfSource;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
class UdfSourceDefine implements UdfSource, AppContextAware {
    private final String      varName;
    private final BindInfo<?> bindInfo;
    private       AppContext  appContext;

    public UdfSourceDefine(String varName, BindInfo<?> bindInfo) {
        this.varName = varName;
        this.bindInfo = bindInfo;
    }

    public String getVarName() {
        return this.varName;
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public Supplier<Map<String, Udf>> getUdfResource(Finder finder) {
        return () -> ((UdfSource) this.appContext.getInstance(this.bindInfo)).getUdfResource(finder).get();
    }
}
