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
package net.hasor.registry.server.manager;
import net.hasor.core.AppContext;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
/**
 * 环境
 * @version : 2016年9月18日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class EnvironmentConfig {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private RsfRequest     rsfRequest;   // 当前 Rsf 请求
    @Inject
    private AppContext     appContext;   // Hasor AppContext
    @Inject
    private ServerSettings rsfCenterCfg; // Center配置
    private String         saltValue;
    //
    @Init
    public void init() {
        //
        Random random = new Random(System.currentTimeMillis());
        String saltVal_a = StringUtils.leftPad(Long.toHexString(random.nextLong()), 20, "0");
        String saltVal_b = StringUtils.leftPad(Long.toHexString(random.nextLong()), 20, "0");
        //
        this.saltValue = saltVal_a + "|" + saltVal_b;
    }
    //
    public long getConsumerExpireTime() {
        return this.rsfCenterCfg.getConsumerExpireTime();
    }
    public long getProviderExpireTime() {
        return this.rsfCenterCfg.getProviderExpireTime();
    }
    public String getSaltValue() {
        return saltValue;
    }
}