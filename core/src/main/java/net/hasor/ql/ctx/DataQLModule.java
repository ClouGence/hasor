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
package net.hasor.db.ql.ctx;
import net.hasor.core.*;
import net.hasor.db.ql.DataQL;
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
        final DataQLFactory qlFactory = DataQLFactory.newInstance();
        apiBinder.bindType(DataQL.class).toInstance(qlFactory);
        //
        // .启动过程
        Hasor.addStartListener(apiBinder.getEnvironment(), new EventListener() {
            @Override
            public void onEvent(String event, Object eventData) throws Throwable {
                AppContext appContext = (AppContext) eventData;
                //
                List<UDFDefine> udfList = appContext.findBindingBean(UDFDefine.class);
                for (UDFDefine define : udfList) {
                    String defineName = define.getName();
                    qlFactory.addUDF(defineName, define);
                }
            }
        });
    }
}