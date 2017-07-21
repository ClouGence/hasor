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
import net.hasor.dataql.domain.compiler.QueryCompiler;
import net.hasor.dataql.domain.compiler.QueryType;
import net.hasor.dataql.domain.parser.ParseException;
import net.hasor.dataql.runtime.QueryRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 * 提供 <code>DataQL</code> 初始化功能。
 * @version : 2017-6-08
 * @author 赵永春 (zyc@byshell.org)
 */
public class DataQLModule implements Module {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //
        // .初始化 DataQL
        final QueryRuntime runtime = new QueryRuntime();
        apiBinder.bindType(DataQL.class).toInstance(new DataQL() {
            @Override
            public Query createQuery(String qlString) throws ParseException {
                QueryType queryType = QueryCompiler.compilerQuery(qlString);
                return runtime.createEngine(queryType).newQuery();
            }
        });
        //
        // .启动过程
        Hasor.addStartListener(apiBinder.getEnvironment(), new EventListener() {
            @Override
            public void onEvent(String event, Object eventData) throws Throwable {
                AppContext appContext = (AppContext) eventData;
                //
                List<DefineUDF> udfList = appContext.findBindingBean(DefineUDF.class);
                for (DefineUDF define : udfList) {
                    String defineName = define.getName();
                    runtime.addShareUDF(defineName, define);
                }
            }
        });
    }
}