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
package net.test.hasor.core._04_scope;
import com.alibaba.fastjson.JSON;
import net.hasor.core.*;
import net.test.hasor.core._01_bean.pojo.PojoBean;
import net.test.hasor.core._01_bean.pojo.PojoInfo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 本示列演示如何使用 Hasor 的Scope隔离Bean。
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class ScopeTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Test
    public void threadScopeTest() {
        System.out.println("--->>threadScopeTest<<--");
        AppContext appContext = Hasor.createAppContext(new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                MyScope threadScope = new MyScope();
                apiBinder.bindType(PojoBean.class).toScope(threadScope);
                apiBinder.bindType(MyScope.class).toInstance(threadScope);
            }
        });
        logger.debug("---------------------------------------------");
        //
        //
        PojoInfo objectA = appContext.getInstance(PojoBean.class);
        PojoInfo objectB = appContext.getInstance(PojoBean.class);
        //
        logger.debug("objectBody :" + JSON.toJSONString(objectA));
        logger.debug("objectA eq objectB = " + (objectA == objectB));
        assert objectA == objectB;
        //
        BindInfo<?> info = appContext.getBindInfo(PojoBean.class);
        MyScope scope = appContext.getInstance(MyScope.class);
        Provider<Object> provider = scope.scope(info, null);
        assert provider != null;
    }
}