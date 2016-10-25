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
package net.hasor.rsf;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Environment;
import net.hasor.core.Module;
import net.hasor.rsf.container.InnerRsfApiBinder;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
/**
 * Rsf 制定 Hasor Module。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class RsfModule implements Module {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public final void loadModule(final ApiBinder apiBinder) throws Throwable {
        Environment env = apiBinder.getEnvironment();
        boolean enable = env.getSettings().getBoolean("hasor.rsfConfig.enable", false);
        if (!enable) {
            logger.info("rsf framework disable -> 'hasor.rsfConfig.enable' is false");
            return;
        }
        RsfEnvironment rsfEnv = initAntGetEnvironment(apiBinder);
        this.loadModule(new InnerRsfApiBinder(apiBinder, rsfEnv));
    }
    private synchronized static RsfEnvironment initAntGetEnvironment(ApiBinder apiBinder) throws IOException {
        BindInfo<RsfEnvironment> rsfEnv = apiBinder.getBindInfo(RsfEnvironment.class);
        RsfEnvironment environment = null;
        if (rsfEnv == null) {
            environment = new DefaultRsfEnvironment(apiBinder.getEnvironment());
            rsfEnv = apiBinder.bindType(RsfEnvironment.class).toInstance(environment).toInfo();
            rsfEnv.setMetaData(RsfEnvironment.class.getName(), environment);
        } else {
            environment = (RsfEnvironment) rsfEnv.getMetaData(RsfEnvironment.class.getName());
        }
        //
        return environment;
    }
    public abstract void loadModule(RsfApiBinder apiBinder) throws Throwable;
}