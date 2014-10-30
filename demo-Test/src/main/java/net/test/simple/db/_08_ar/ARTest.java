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
package net.test.simple.db._08_ar;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.db.ar.support.DataBase;
import net.hasor.db.ar.support.Entity;
import net.hasor.test.junit.ContextConfiguration;
import net.hasor.test.runner.HasorUnitRunner;
import net.test.simple.db._07_datasource.warp.OneDataSourceWarp;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * 
 * @version : 2014年10月27日
 * @author 赵永春(zyc@hasor.net)
 */
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "net/test/simple/db/jdbc-config.xml", loadModules = OneDataSourceWarp.class)
public class ARTest implements AppContextAware {
    private DataBase dataBase = null;
    @Override
    public void setAppContext(AppContext appContext) {
        this.dataBase = new DataBase(appContext.getInstance(DataSource.class));
    }
    @Test
    public void ar_Test() throws Exception {
        System.out.println("--->>ar_Test<<--");
        //
        //---------------------------------------------------------
        //增：插入
        dataBase.openEntity("TB_USER").set("userName", "").saveAsNew();
        //删：删除
        dataBase.openEntity("TB_USER").setID("123").delete();
        //改：更新
        dataBase.openEntity("TB_USER").setID("123").set("userName", "").saveOrUpdate();
        //查
        dataBase.openEntity("TB_USER").setID("123").loadData();//查询单条
        //
        //---------------------------------------------------------
        //删：条件删除
        dataBase.openEntity("TB_USER").set("status", 2).deleteByExample();
        //改：条件更新
        dataBase.openEntity("TB_USER").set("status", 2).updateByExample(new HashMap<String, Object>());
        //查：条件查询
        List<Entity> listEnt = dataBase.openEntity("TB_USER").set("status", 2).listByExample();
        //取总数
        dataBase.openEntity("TB_USER").set("userName", "aac").set("password", "asdf").countByExample();
        //
        //---------------------------------------------------------
        //
        //填充另外一张表的数据
        //        dataBase.openEntity("TB_USER").set("roleID", 123).fillForm("TB_ROLE", "roleID", "id");
        //        dataBase.openEntity("TB_USER").set("roleID", 123).fillForm("TB_ROLE", "roleID");
        //
        //
        //
        //
        //
        // TODO Auto-generated method stub
    }
}
