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
package net.test.hasor.db._07_ql;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.data.ql.GraphApiBinder;
import net.test.hasor.db._07_ql.udfs.UserManager;
import net.test.hasor.db._07_ql.udfs.FindUserByID;
import net.test.hasor.db._07_ql.udfs.Foo;
import net.test.hasor.db._07_ql.udfs.QueryOrder;
import org.junit.Before;
/**
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class AbstractTaskTest implements Module {
    protected static AppContext appContext;
    @Before
    public void before() {
        if (appContext == null) {
            appContext = Hasor.createAppContext(this);
        }
    }
    //
    // --------------------------------------------------------------------------------------------
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // - DataUDF
        GraphApiBinder binder = apiBinder.tryCast(GraphApiBinder.class);
        binder.addUDF("findUserByID", FindUserByID.class);
        binder.addUDF("queryOrder", QueryOrder.class);
        binder.addUDF("userManager.findUserByID", UserManager.class);
        binder.addUDF("foo", Foo.class);
    }
}