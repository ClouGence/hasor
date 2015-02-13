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
package net.test.hasor.db._08_ar;
import javax.sql.DataSource;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.db.orm.PageResult;
import net.hasor.test.junit.ContextConfiguration;
import net.hasor.test.runner.HasorUnitRunner;
import net.hasor.test.utils.HasorUnit;
import net.test.hasor.db._07_datasource.warp.OneDataSourceWarp;
import net.test.hasor.db._08_ar.dao.UserDao;
import net.test.hasor.db._08_ar.entity.TB_User;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * 
 * @version : 2014年10月27日
 * @author 赵永春(zyc@hasor.net)
 */
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "net/test/simple/db/jdbc-config.xml", loadModules = OneDataSourceWarp.class)
public class AR_SelectTest implements AppContextAware {
    private DataSource dataSource = null;
    @Override
    public void setAppContext(AppContext appContext) {
        this.dataSource = appContext.getInstance(DataSource.class);
    }
    @Test
    public void ar_Test() throws Exception {
        System.out.println("--->>ar_Test<<--");
        //
        UserDao userDao = new UserDao(this.dataSource);
        PageResult<TB_User> userEnt1 = userDao.queryList1();
        PageResult<TB_User> userEnt2 = userDao.queryList2();
        //
        HasorUnit.printObjectList(userEnt1.getResult(), true);
        HasorUnit.printObjectList(userEnt2.getResult(), true);
    }
}
