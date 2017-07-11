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
package net.hasor.plugins.data;
import net.hasor.core.*;
import net.hasor.core.binder.ApiBinderCreater;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.dataql.UDF;
import net.hasor.db.DBModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
/**
 * DataQL 扩展接口。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class DataApiBinderCreater implements ApiBinderCreater {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public ApiBinder createBinder(final ApiBinder apiBinder) {
        return new DataApiBinderImpl(apiBinder);
    }
    //
    //
    private static class UDFDefine implements AppContextAware, UDF {
        private String                  udfName;
        private BindInfo<? extends UDF> udfInfo;
        private AppContext              appContext;
        private UDF                     target;
        //
        public UDFDefine(String udfName, BindInfo<? extends UDF> udfInfo) {
            this.udfName = udfName;
            this.udfInfo = udfInfo;
        }
        @Override
        public void setAppContext(AppContext appContext) {
            this.appContext = appContext;
        }
        @Override
        public Object call(Object[] values) {
            if (this.target == null) {
                synchronized (this) {
                    if (this.target == null) {
                        this.target = this.appContext.getInstance(this.udfInfo);
                    }
                }
            }
            return this.target;
        }
    }
    //
    private static class DataApiBinderImpl extends ApiBinderWrap implements DataApiBinder {
        protected Logger logger = LoggerFactory.getLogger(getClass());
        public DataApiBinderImpl(ApiBinder apiBinder) {
            super(apiBinder);
        }
        //
        @Override
        public void addUDF(String name, Class<? extends UDF> udfType) {
            this.addUDF(name, bindType(UDF.class).uniqueName().to(udfType).toInfo());
        }
        @Override
        public void addUDF(String name, UDF dataUDF) {
            this.addUDF(name, bindType(UDF.class).uniqueName().toInstance(dataUDF).toInfo());
        }
        @Override
        public void addUDF(String name, Provider<? extends UDF> udfProvider) {
            this.addUDF(name, bindType(UDF.class).uniqueName().toProvider(udfProvider).toInfo());
        }
        //
        @Override
        public void addDataSource(DataSource dataSource) throws Throwable {
            this.addDataSource(null, dataSource);
        }
        @Override
        public void addDataSource(Provider<DataSource> dataSource) throws Throwable {
            this.addDataSource(null, dataSource);
        }
        @Override
        public void addDataSource(String dataSourceID, DataSource dataSource) throws Throwable {
            this.addDataSource(dataSourceID, new InstanceProvider<DataSource>(dataSource));
        }
        //
        @Override
        public void addUDF(String name, BindInfo<? extends UDF> udfInfo) {
            UDFDefine define = Hasor.autoAware(getEnvironment(), new UDFDefine(name, udfInfo));
            this.bindType(UDFDefine.class).uniqueName().toInstance(define);
        }
        @Override
        public void addDataSource(String dataSourceID, Provider<DataSource> dataSource) throws Throwable {
            this.installModule(new DBModule(dataSourceID, dataSource));
        }
    }
    //
}