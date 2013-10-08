///*
// * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.hasor.db.jdbc;
//import java.io.Reader;
//import java.util.List;
//import java.util.Map;
//import javax.security.auth.login.Configuration;
//import javax.transaction.InvalidTransactionException;
//import org.apache.commons.dbutils.QueryRunner;
//import org.apache.commons.dbutils.handlers.MapListHandler;
//import org.omg.CORBA.SystemException;
///**
// * 
// * @version : 2013-6-14
// * @author ’‘”¿¥∫ (zyc@byshell.org)
// */
//public class MoreTransactionManager implements TransactionManager {
//    @Override
//    public void begin() throws NotSupportedException, SystemException {
//        
//        QueryRunner run = new QueryRunner(null);
//        List<Map<String, Object>>  ds=   run.query("", new MapListHandler());
//        
//        
//        
//        
//        
//        
//        Configuration cfg = null;
//        cfg.buildSessionFactory().openSession().getSessionFactory();
//        //
//        SqlSessionFactoryBuilder sb = null;
//        sb.build((Reader) null).getConfiguration();
//        // TODO Auto-generated method stub
//    }
//    @Override
//    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
//        // TODO Auto-generated method stub
//    }
//    @Override
//    public int getStatus() throws SystemException {
//        // TODO Auto-generated method stub
//        return 0;
//    }
//    @Override
//    public Transaction getTransaction() throws SystemException {
//        // TODO Auto-generated method stub
//        return null;
//    }
//    @Override
//    public void resume(Transaction tobj) throws InvalidTransactionException, IllegalStateException, SystemException {
//        // TODO Auto-generated method stub
//    }
//    @Override
//    public void rollback() throws IllegalStateException, SecurityException, SystemException {
//        // TODO Auto-generated method stub
//    }
//    @Override
//    public void setRollbackOnly() throws IllegalStateException, SystemException {
//        // TODO Auto-generated method stub
//    }
//    @Override
//    public void setTransactionTimeout(int seconds) throws SystemException {
//        // TODO Auto-generated method stub
//    }
//    @Override
//    public Transaction suspend() throws SystemException {
//        // TODO Auto-generated method stub
//        return null;
//    }
//}