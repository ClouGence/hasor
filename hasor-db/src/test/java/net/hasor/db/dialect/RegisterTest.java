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
package net.hasor.db.dialect;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.aop.DynamicClass;
import net.hasor.test.db.AbstractDbTest;
import net.hasor.test.db.dialect.TestDialect;
import org.junit.Test;

/***
 * 方言注册器
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class RegisterTest extends AbstractDbTest {
    @Test
    public void dialectRegisterTest_1() {
        assert SqlDialectRegister.findOrCreate("") == SqlDialect.DEFAULT;
        assert SqlDialectRegister.findOrCreate(null) == SqlDialect.DEFAULT;
    }

    @Test
    public void dialectRegisterTest_2() {
        try {
            SqlDialectRegister.clearDialectCache();
            SqlDialectRegister.findOrCreate("abc");
        } catch (IllegalStateException e) {
            assert e.getMessage().equals("load dialect 'abc' class not found");
        }
    }

    @Test
    public void dialectRegisterTest_3() {
        SqlDialectRegister.clearDialectCache();
        SqlDialect dialect = SqlDialectRegister.findOrCreate("net.hasor.test.db.dialect.TestDialect");
        assert dialect != null;
        assert dialect instanceof TestDialect;
        assert !(dialect instanceof DynamicClass);
    }

    @Test
    public void dialectRegisterTest_4() {
        try (AppContext appContext = Hasor.create().build()) {
            SqlDialectRegister.clearDialectCache();
            SqlDialect dialect = SqlDialectRegister.findOrCreate("net.hasor.test.db.dialect.TestDialect", appContext);
            assert dialect != null;
            assert dialect instanceof TestDialect;
            assert dialect instanceof DynamicClass;
        }
    }

    @Test
    public void dialectRegisterTest_5() {
        try (AppContext appContext = Hasor.create().build()) {
            SqlDialectRegister.clearDialectCache();
            SqlDialect dialect1 = SqlDialectRegister.findOrCreate("net.hasor.test.db.dialect.TestDialect");
            assert dialect1 != null;
            assert dialect1 instanceof TestDialect;
            assert !(dialect1 instanceof DynamicClass);
            //
            SqlDialect dialect2 = SqlDialectRegister.findOrCreate("net.hasor.test.db.dialect.TestDialect", appContext);
            assert dialect2 != null;
            assert dialect2 instanceof TestDialect;
            assert !(dialect1 instanceof DynamicClass); // in cache
        }
    }

    @Test
    public void dialectRegisterTest_6() {
        Module module = apiBinder -> apiBinder.bindType(SqlDialect.class).nameWith("abc").to(TestDialect.class);
        //
        try (AppContext appContext = Hasor.create().build(module)) {
            SqlDialectRegister.clearDialectCache();
            SqlDialect dialect1 = SqlDialectRegister.findOrCreate("abc", appContext);
            assert dialect1 != null;
            assert dialect1 instanceof TestDialect;
            assert dialect1 instanceof DynamicClass;
        }
    }
}
