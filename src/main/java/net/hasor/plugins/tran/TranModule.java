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
package net.hasor.plugins.tran;
import javax.sql.DataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.Provider;
import net.hasor.core.binder.InstanceProvider;
/**
 * 
 * @author 赵永春(zyc@hasor.net)
 * @version : 2013-10-30
 */
public class TranModule implements Module {
    private String               dataSourceID = null;
    private Provider<DataSource> dataSource   = null;
    //
    public TranModule(DataSource dataSource) {
        this(null, new InstanceProvider<DataSource>(Hasor.assertIsNotNull(dataSource, "dataSource is null.")));
    }
    public TranModule(Provider<DataSource> dataSource) {
        this(null, dataSource);
    }
    public TranModule(String dataSourceID, DataSource dataSource) {
        this(dataSourceID, new InstanceProvider<DataSource>(Hasor.assertIsNotNull(dataSource, "dataSource is null.")));
    }
    public TranModule(String dataSourceID, Provider<DataSource> dataSource) {
        Hasor.assertIsNotNull(dataSource, "dataSource is null.");
        this.dataSourceID = dataSourceID;
        this.dataSource = dataSource;
    }
    //
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        
        
        TranInterceptor
        
        apiBinder.bindInterceptor(matcherClass, matcherMethod, interceptor);
    }
}