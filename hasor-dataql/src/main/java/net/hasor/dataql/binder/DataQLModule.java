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
import net.hasor.core.*;
import net.hasor.dataql.UdfManager;
import net.hasor.dataql.UdfSource;
import net.hasor.dataql.domain.compiler.QIL;
import net.hasor.dataql.domain.compiler.QueryCompiler;
import net.hasor.dataql.runtime.QueryEngineImpl;
import net.hasor.dataql.udf.LoaderUdfSource;
import net.hasor.dataql.udf.SimpleUdfManager;
import net.hasor.dataql.udf.SimpleUdfSource;
import net.hasor.dataql.udf.funs.CollectionUDFs;
import net.hasor.dataql.udf.source.TypeUdfSource;
import net.hasor.utils.StringUtils;

import java.util.List;

/**
 * 提供 <code>DataQL</code> 初始化功能。
 * @version : 2017-6-08
 * @author 赵永春 (zyc@byshell.org)
 */
public class DataQLModule implements Module {
    private void loadUDF(AppContext appContext, UdfManager udfManager) {
        SimpleUdfSource simpleUdfSource = new SimpleUdfSource();
        List<DefineUDF> udfList = appContext.findBindingBean(DefineUDF.class);
        for (DefineUDF define : udfList) {
            if (define == null || StringUtils.isBlank(define.getName())) {
                continue;
            }
            simpleUdfSource.addUdf(define.getName(), define);
        }
        udfManager.addSource(simpleUdfSource);
        //
        List<DefineSource> udfSourceList = appContext.findBindingBean(DefineSource.class);
        for (DefineSource define : udfSourceList) {
            BindInfo<? extends UdfSource> bindInfo = define.getTarget();
            if (bindInfo == null) {
                continue;
            }
            UdfSource instance = appContext.getInstance(bindInfo);
            if (instance == null) {
                continue;
            }
            udfManager.addSource(instance);
        }
    }

    public void loadModule(final ApiBinder apiBinder) {
        SimpleUdfManager udfManager = new SimpleUdfManager();
        //
        // .启动过程
        HasorUtils.pushStartListener(apiBinder.getEnvironment(), (event, eventData) -> {
            loadUDF((AppContext) eventData, udfManager);
        });
        //
        // .初始化 DataQL
        apiBinder.bindType(DataQL.class).toInstance(qlString -> {
            QIL queryType = QueryCompiler.compilerQuery(qlString);
            QueryEngineImpl queryEngine = new QueryEngineImpl(udfManager, queryType);
            queryEngine.setClassLoader(apiBinder.getEnvironment().getClassLoader());
            return queryEngine.newQuery();
        });
        //
        // .UDFs(内置集合函数)
        DataApiBinder dataBinder = apiBinder.tryCast(DataApiBinder.class);
        if (dataBinder == null) {
            return;
        }
        dataBinder.addUdfSource(new LoaderUdfSource(apiBinder.getEnvironment().getClassLoader()));
        dataBinder.addUdfSource(new TypeUdfSource<>(CollectionUDFs.class, null, null));
    }
}