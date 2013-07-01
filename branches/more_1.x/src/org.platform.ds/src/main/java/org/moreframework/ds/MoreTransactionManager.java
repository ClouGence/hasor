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
package org.moreframework.ds;
import java.io.Reader;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hibernate.cfg.Configuration;
/**
 * 
 * @version : 2013-6-14
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class MoreTransactionManager implements TransactionManager {
    @Override
    public void begin() throws NotSupportedException, SystemException {
        Configuration cfg;
        cfg.buildSessionFactory().openSession().getSessionFactory();
        //
        SqlSessionFactoryBuilder sb;
        
        
        sb.build((Reader)null).getConfiguration().
        // TODO Auto-generated method stub
    }
    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        // TODO Auto-generated method stub
    }
    @Override
    public int getStatus() throws SystemException {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public Transaction getTransaction() throws SystemException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void resume(Transaction tobj) throws InvalidTransactionException, IllegalStateException, SystemException {
        // TODO Auto-generated method stub
    }
    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        // TODO Auto-generated method stub
    }
    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        // TODO Auto-generated method stub
    }
    @Override
    public void setTransactionTimeout(int seconds) throws SystemException {
        // TODO Auto-generated method stub
    }
    @Override
    public Transaction suspend() throws SystemException {
        // TODO Auto-generated method stub
        return null;
    }
}