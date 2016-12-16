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
import net.hasor.core.Environment;
import net.hasor.core.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Rsf 制定 Hasor Module。
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class RsfModule implements Module, RsfPlugin {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public final void loadModule(final ApiBinder apiBinder) throws Throwable {
        // .只有Web环境才启用该功能
        if (!(apiBinder instanceof RsfApiBinder)) {
            return;
        }
        //
        Environment env = apiBinder.getEnvironment();
        boolean enable = env.getSettings().getBoolean("hasor.rsfConfig.enable", false);
        if (!enable) {
            logger.info("rsf framework disable -> 'hasor.rsfConfig.enable' is false");
            return;
        }
        this.loadModule((RsfApiBinder) apiBinder);
    }
    public static RsfModule toModule(final RsfPlugin rsfPlugin) {
        return new RsfModule() {
            public void loadModule(RsfApiBinder apiBinder) throws Throwable {
                rsfPlugin.loadModule(apiBinder);
            }
        };
    }
    public abstract void loadModule(RsfApiBinder apiBinder) throws Throwable;
}