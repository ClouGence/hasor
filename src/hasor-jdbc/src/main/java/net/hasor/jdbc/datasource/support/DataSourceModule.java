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
package net.hasor.jdbc.datasource.support;
import java.util.List;
import javax.sql.DataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.jdbc.datasource.DataSourceResources;
import net.hasor.jdbc.datasource.pool.C3p0_DataSourceFactory;
import net.hasor.jdbc.datasource.pool.DBCP_DataSourceFactory;
import net.hasor.jdbc.datasource.pool.Druid_DataSourceFactory;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-10-8
 * @author 赵永春(zyc@hasor.net)
 */
public class DataSourceModule implements Module {
    public void init(ApiBinder apiBinder) {
        Settings settings = apiBinder.getEnvironment().getSettings();
        //
        String defaultDS = settings.getString("dataSourceSet.default");
        XmlNode[] dataSourceSet = settings.getXmlPropertyArray("dataSourceSet");
        if (dataSourceSet == null)
            return;
        //
        for (XmlNode dsSet : dataSourceSet) {
            List<XmlNode> dataSource = dsSet.getChildren("dataSource");
            for (XmlNode ds : dataSource) {
                //1.DataSources
                String name = ds.getAttribute("name");
                DataSourceResources dsFactory = null;
                String poolType = ds.getAttribute("poolType");
                DataSource dataSourceObject = null;
                //2.创建DataSourceFactory
                if (StringUtils.equalsIgnoreCase(poolType, "c3p0"))
                    dsFactory = new C3p0_DataSourceFactory();
                else if (StringUtils.equalsIgnoreCase(poolType, "dbcp"))
                    dsFactory = new DBCP_DataSourceFactory();
                else if (StringUtils.equalsIgnoreCase(poolType, "druid"))
                    dsFactory = new Druid_DataSourceFactory();
                else
                    dsFactory = new Druid_DataSourceFactory();
                //3.注册
                try {
                    dataSourceObject = dsFactory.getDataSource(ds);
                    apiBinder.newBean(name).bindType(DataSource.class).toInstance(dataSourceObject);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
                //4.default
                if (StringUtils.equalsIgnoreCase(name, defaultDS))
                    apiBinder.bindingType(DataSource.class, dataSourceObject);
            }
        }
    }
    //
    public void start(AppContext appContext) {
        //获取指定名称的数据源，由于数据源是通过Guice绑定的因此需要通过一些手段完成。
        //        TypeLiteral<DataSource> ds = TypeLiteral.get(DataSource.class);
        //        List<Binding<DataSource>> bindingList = appContext.getGuice().findBindingsByType(ds);
        //        Named ns = Names.named("NOE_UMS");
        //        for (Binding<DataSource> bind : bindingList) {
        //            if (bind.getKey().getAnnotation().equals(ns))
        //                System.out.println();
        //        }
    }
    public void stop(AppContext appContext) {
        // TODO Auto-generated method stub
    }
}