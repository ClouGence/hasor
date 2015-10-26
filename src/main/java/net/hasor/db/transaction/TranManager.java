/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.db.transaction;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import net.hasor.core.Hasor;
import net.hasor.db.transaction.support.JdbcTransactionManager;
import org.more.util.ContextClassLoaderLocal;
/**
 * 某一个数据源的事务管理器
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
public class TranManager {
    private final static ContextClassLoaderLocal<Map<DataSource, DefaultTransactionManager>> managerMap;
    static {
        managerMap = new ContextClassLoaderLocal<Map<DataSource, DefaultTransactionManager>>() {
            @Override
            protected Map<DataSource, DefaultTransactionManager> initialValue() {
                return new HashMap<DataSource, DefaultTransactionManager>();
            }
        };
    }
    private static synchronized DefaultTransactionManager getDefaultTransactionManager(final DataSource dataSource) {
        Hasor.assertIsNotNull(dataSource);
        DefaultTransactionManager manager = TranManager.managerMap.get().get(dataSource);
        manager = new DefaultTransactionManager(dataSource);
        TranManager.managerMap.get().put(dataSource, manager);
        return manager;
    }
    public static synchronized TransactionManager getManager(DataSource dataSource) {
        return getDefaultTransactionManager(dataSource);
    }
    public static synchronized TransactionTemplate getTemplate(final DataSource dataSource) {
        DefaultTransactionManager manager = getDefaultTransactionManager(dataSource);
        if (manager == null) {
            return null;
        }
        return manager.getTransactionTemplate();
    }
}
class DefaultTransactionManager extends JdbcTransactionManager {
    public DefaultTransactionManager(final DataSource dataSource) {
        super(dataSource);
    }
}