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
package net.hasor.rsf.center.domain.dao;
import net.hasor.core.AppContext;
import net.hasor.core.InjectMembers;
import net.hasor.rsf.center.domain.dao.db.AppDOMySqlDao;
import net.hasor.rsf.center.domain.dao.db.ServiceInfoDOMySqlDao;
import net.hasor.rsf.center.domain.dao.mem.AppDOMemDao;
import net.hasor.rsf.center.domain.dao.mem.ServiceInfoDOMemDao;
import net.hasor.rsf.center.domain.dao.mem.ServiceJoinPortDOMemDao;
import net.hasor.rsf.center.domain.dao.mem.TerminalDOMemDao;
/**
 * 
 * @version : 2015年6月30日
 * @author 赵永春(zyc@hasor.net)
 */
@Dao
public class DaoProvider implements InjectMembers {
    private AppDOMySqlDao           appDOMySqlDao;
    private ServiceInfoDOMySqlDao   serviceInfoDOMySqlDao;
    //
    private AppDOMemDao             appDOMemDao;
    private ServiceInfoDOMemDao     serviceInfoDOMemDao;
    private ServiceJoinPortDOMemDao serviceJoinPortDOMemDao;
    private TerminalDOMemDao        terminalDOMemDao;
    //
    public void doInject(AppContext appContext) {
        appDOMySqlDao = appContext.getInstance(AppDOMySqlDao.class);
        serviceInfoDOMySqlDao = appContext.getInstance(ServiceInfoDOMySqlDao.class);
        //
        appDOMemDao = appContext.getInstance(AppDOMemDao.class);
        serviceInfoDOMemDao = appContext.getInstance(ServiceInfoDOMemDao.class);
        serviceJoinPortDOMemDao = appContext.getInstance(ServiceJoinPortDOMemDao.class);
        terminalDOMemDao = appContext.getInstance(TerminalDOMemDao.class);
    }
    public AppDOMySqlDao getAppDOMySqlDao() {
        return appDOMySqlDao;
    }
    public ServiceInfoDOMySqlDao getServiceInfoDOMySqlDao() {
        return serviceInfoDOMySqlDao;
    }
    public AppDOMemDao getAppDOMemDao() {
        return appDOMemDao;
    }
    public ServiceInfoDOMemDao getServiceInfoDOMemDao() {
        return serviceInfoDOMemDao;
    }
    public ServiceJoinPortDOMemDao getServiceJoinPortDOMemDao() {
        return serviceJoinPortDOMemDao;
    }
    public TerminalDOMemDao getTerminalDOMemDao() {
        return terminalDOMemDao;
    }
}