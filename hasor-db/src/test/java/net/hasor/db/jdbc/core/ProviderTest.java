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
package net.hasor.db.jdbc.core;
import net.hasor.test.db.AbstractDbTest;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicBoolean;

/***
 *
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class ProviderTest extends AbstractDbTest {
    @Test
    public void providerTest_1() {
        DataSource dataSource = PowerMockito.mock(DataSource.class);
        //
        JdbcTemplateProvider provider1 = new JdbcTemplateProvider(dataSource);
        assert provider1.get().getDataSource() == dataSource;
        //
        //
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        assert !atomicBoolean.get();
        JdbcTemplateProvider provider2 = new JdbcTemplateProvider(() -> {
            atomicBoolean.set(true);
            return dataSource;
        });
        assert provider2.get().getDataSource() == dataSource;
        assert atomicBoolean.get();
    }
}