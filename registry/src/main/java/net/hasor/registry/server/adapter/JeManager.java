///*
// * Copyright 2008-2009 the original author or authors.
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
//package net.hasor.rsf.center.server.adapter;
//import com.sleepycat.bind.serial.ClassCatalog;
//import com.sleepycat.bind.serial.SerialBinding;
//import com.sleepycat.bind.serial.StoredClassCatalog;
//import com.sleepycat.bind.tuple.TupleBinding;
//import com.sleepycat.collections.StoredSortedMap;
//import com.sleepycat.je.Database;
//import com.sleepycat.je.DatabaseConfig;
//import com.sleepycat.je.Environment;
//import com.sleepycat.je.EnvironmentConfig;
//import net.hasor.core.AppContext;
//import net.hasor.core.Inject;
//import net.hasor.core.Singleton;
//import net.hasor.rsf.center.server.domain.ServerSettings;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.util.SortedMap;
///**
// * JE 内存形 KeyValue 数据库
// * @version : 2016年9月18日
// * @author 赵永春(zyc@hasor.net)
// */
//@Singleton
//public class JeManager {
//    protected Logger logger = LoggerFactory.getLogger(getClass());
//    @Inject
//    private AppContext        appContext;
//    @Inject
//    private ServerSettings rsfCenterCfg;
//    private Environment       jeEnvironment;
//    private boolean useTransactional = false;
//    private boolean isAllowCreate    = true;
//    //
//    private Database                   db;
//    private ClassCatalog               catalog;
//    private SortedMap<Integer, String> map;
//    //
//    //
//    public void init() {
//        // .JE 环境
//        String dir = "./tmp";
//        EnvironmentConfig envConfig = new EnvironmentConfig();
//        envConfig.setTransactional(this.useTransactional);
//        envConfig.setAllowCreate(this.isAllowCreate);
//        this.jeEnvironment = new Environment(new File(dir), envConfig);
//    }
//    /** Opens the database and creates the Map. */
//    private void openJE() {
//        //
//        // use a generic database configuration
//        DatabaseConfig dbConfig = new DatabaseConfig();
//        dbConfig.setTransactional(this.useTransactional);
//        dbConfig.setAllowCreate(this.isAllowCreate);
//        //
//        // catalog is needed for serial bindings (java serialization)
//        Database catalogDb = this.jeEnvironment.openDatabase(null, "catalog", dbConfig);
//        this.catalog = new StoredClassCatalog(catalogDb);
//        //
//        // use Integer tuple binding for key entries
//        TupleBinding<Integer> keyBinding = TupleBinding.getPrimitiveBinding(Integer.class);
//        //
//        // use String serial binding for data entries
//        SerialBinding<String> dataBinding = new SerialBinding<String>(catalog, String.class);
//        this.db = this.jeEnvironment.openDatabase(null, "helloworld", dbConfig);
//        //
//        // create a map view of the database
//        this.map = new StoredSortedMap<Integer, String>(db, keyBinding, dataBinding, true);
//    }
//    /** Closes the database. */
//    private void closeJE() {
//        if (this.catalog != null) {
//            this.catalog.close();
//            this.catalog = null;
//        }
//        if (this.db != null) {
//            this.db.close();
//            this.db = null;
//        }
//        if (this.jeEnvironment != null) {
//            this.jeEnvironment.close();
//            this.jeEnvironment = null;
//        }
//    }
//    //
//    public boolean putData(String key, String value) {
//        return true;
//    }
//    public String getData(String key) {
//        return null;
//    }
//}