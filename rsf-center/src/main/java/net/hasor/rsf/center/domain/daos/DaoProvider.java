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
package net.hasor.rsf.center.domain.daos;
import net.hasor.core.AppContext;
import net.hasor.core.InjectMembers;
import net.hasor.rsf.center.core.dao.Dao;
/**
 * 
 * @version : 2015年6月30日
 * @author 赵永春(zyc@hasor.net)
 */
@Dao
public class DaoProvider implements InjectMembers {
    private AppDOMemDao             appDOMemDao;
    private ServiceInfoDOMemDao     serviceInfoDOMemDao;
    private ServiceJoinPortDOMemDao serviceJoinPortDOMemDao;
    private TerminalDOMemDao        terminalDOMemDao;
    //
    public void doInject(AppContext appContext) {
        this.appDOMemDao = appContext.getInstance(AppDOMemDao.class);
        this.serviceInfoDOMemDao = appContext.getInstance(ServiceInfoDOMemDao.class);
        this.serviceJoinPortDOMemDao = appContext.getInstance(ServiceJoinPortDOMemDao.class);
        this.terminalDOMemDao = appContext.getInstance(TerminalDOMemDao.class);
    }
    public AppDOMemDao getAppDOMemDao() {
        return this.appDOMemDao;
    }
    public ServiceInfoDOMemDao getServiceInfoDOMemDao() {
        return this.serviceInfoDOMemDao;
    }
    public ServiceJoinPortDOMemDao getServiceJoinPortDOMemDao() {
        return this.serviceJoinPortDOMemDao;
    }
    public TerminalDOMemDao getTerminalDOMemDao() {
        return this.terminalDOMemDao;
    }
}