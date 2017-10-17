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
import net.hasor.dataql.Query;
import net.hasor.dataql.UdfManager;
import net.hasor.dataql.UdfSource;
import net.hasor.dataql.domain.compiler.QIL;
import net.hasor.dataql.domain.compiler.QueryCompiler;
import net.hasor.dataql.domain.parser.ParseException;
import net.hasor.dataql.runtime.QueryEngine;
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
    public void loadModule(final ApiBinder apiBinder) throws Throwable {
        final SimpleUdfManager udfManager = new SimpleUdfManager();
        //
        // .启动过程
        Hasor.addStartListener(apiBinder.getEnvironment(), new EventListener() {
            @Override
            public void onEvent(String event, Object eventData) throws Throwable {
                AppContext appContext = (AppContext) eventData;
                //
                List<DefineUDF> udfList = appContext.findBindingBean(DefineUDF.class);
                for (DefineUDF define : udfList) {
                    if (define == null || StringUtils.isBlank(define.getName())) {
                        continue;
                    }
                    UdfSource sourceByName = udfManager.getSourceByName(UdfManager.DefaultSource);
                    if (sourceByName == null) {
                        sourceByName = new SimpleUdfSource();
                        udfManager.addDefaultSource(sourceByName);
                    }
                    sourceByName.addUdf(define.getName(), define);
                }
                //
                List<DefineSource> udfSourceList = appContext.findBindingBean(DefineSource.class);
                for (DefineSource define : udfSourceList) {
                    String defineName = define.getName();
                    BindInfo<? extends UdfSource> bindInfo = define.getTarget();
                    if (bindInfo == null) {
                        continue;
                    }
                    UdfSource instance = appContext.getInstance(bindInfo);
                    if (instance == null) {
                        continue;
                    }
                    //
                    UdfSource source = udfManager.getSourceByName(defineName);
                    if (source != null) {
                        source.putAll(instance);
                    } else {
                        if (UdfManager.DefaultSource.equalsIgnoreCase(defineName)) {
                            udfManager.addDefaultSource(instance);
                        } else {
                            udfManager.addSource(defineName, instance);
                        }
                    }
                }
            }
        });
        //
        // .初始化 DataQL
        apiBinder.bindType(DataQL.class).toInstance(new DataQL() {
            @Override
            public Query createQuery(String qlString) throws ParseException {
                QIL queryType = QueryCompiler.compilerQuery(qlString);
                QueryEngine queryEngine = new QueryEngine(udfManager, queryType);
                queryEngine.setClassLoader(apiBinder.getEnvironment().getClassLoader());
                return queryEngine.newQuery();
            }
        });
        //
        // .UDFs(内置集合函数)
        DataApiBinder dataBinder = apiBinder.tryCast(DataApiBinder.class);
        if (dataBinder == null) {
            return;
        }
        //
        dataBinder.addDefaultUdfSource(new TypeUdfSource<CollectionUDFs>(CollectionUDFs.class, null, null));
    }
}