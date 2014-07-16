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
package net.hasor.db.transaction.interceptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.sql.DataSource;
import net.hasor.db.transaction.Manager;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.TransactionManager;
import net.hasor.db.transaction.TransactionStatus;
import net.hasor.db.transaction.interceptor._.DataSourceInfo;
import net.hasor.db.transaction.interceptor._.DataSourceSource;
import net.hasor.db.transaction.interceptor._.MatcherInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.more.RepeateException;
/**
 * 某一个数据源的事务管理器
 * @author 赵永春(zyc@hasor.net)
 * @version : 2013-10-30
 */
public class TranInterceptor implements MethodInterceptor {
    private DataSourceSource                dataSource  = null; //数据源
    private Map<String, TransactionManager> tranManager = null; //事务管理器
    private MatcherInterceptor              matcher     = null; //事务拦截匹配
    //
    /**初始化事务管理器*/
    public void initManager() {
        if (this.tranManager != null)
            return;
        //
        this.tranManager = new HashMap<String, TransactionManager>();
        int dsCount = this.dataSource.getDataSourceCount();
        for (int i = 0; i < dsCount; i++) {
            DataSourceInfo dsInfo = this.dataSource.getDataSource(i);
            String dsName = dsInfo.getName();
            DataSource dsObject = dsInfo.getDataSource();
            //
            if (this.tranManager.containsKey(dsName) == true)
                throw new RepeateException(String.format("the name ‘%s’ already exists", dsName));
            if (this.tranManager.containsValue(dsObject) == true)
                throw new RepeateException(String.format("the DataSource ‘%s’ already exists", dsObject.toString()));
            //
            TransactionManager manager = Manager.getTransactionManager(dsObject);
            this.tranManager.put(dsName, manager);
        }
    }
    //
    //
    public final Object invoke(MethodInvocation invocation) throws Throwable {
        //1.是否排除不实用事务管理器
        Method targetMethod = invocation.getMethod();
        if (this.matcher == null || this.matcher.matcherMethod(targetMethod) == false)
            return invocation.proceed();
        //2.初始化事务管理器
        this.initManager();
        //
        //2.在事务管理器的控制下进行方法调用
        Map<String, TransactionStatus> tranStatus = new HashMap<String, TransactionStatus>();
        for (Entry<String, TransactionManager> tranEntry : this.tranManager.entrySet()) {
            String dsName = tranEntry.getKey();
            
             
            
            
        }
        TransactionStatus tranStatus = null;
        TransactionManager tranManager = this.getTransactionManager();
        try {
            Propagation propagation = getPropagation(targetMethod);
            tranStatus = tranManager.getTransaction(propagation);
            return invocation.proceed();
        } catch (RollBackSQLException e) {
            /*回滚事务*/
            tranStatus.setRollbackOnly();
        } catch (Throwable e) {
            tranManager.rollBack(tranStatus);
            throw e;
        } finally {
            if (!tranStatus.isCompleted())
                tranManager.commit(tranStatus);
        }
    }
}