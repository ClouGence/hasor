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
package test.net.hasor.rsf.center.cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.LifeModule;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperModule;
import net.hasor.rsf.center.domain.constant.WorkMode;
/**
 * @version : 2015年8月13日
 * @author 赵永春(zyc@hasor.net)
 */
public class TestServerB implements LifeModule {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        // WorkAt
        WorkMode workAt = apiBinder.getEnvironment().getSettings().getEnum("rsfCenter.workAt", WorkMode.class, WorkMode.Alone);
        logger.info("rsf work mode at : ({}){}", workAt.getCodeType(), workAt.getCodeString());
        //
        // Zookeeper
        apiBinder.installModule(new ZooKeeperModule(workAt));
    }
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        // TODO Auto-generated method stub
    }
    @Override
    public void onStop(AppContext appContext) throws Throwable {
        // TODO Auto-generated method stub
    }
    public static void main(String[] args) {
        Hasor.createAppContext("/cluster/rsf-server-b.xml", new TestServerB());
    }
}