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
package net.hasor.rsf.rpc.context;
import io.netty.util.TimerTask;
import net.hasor.core.Environment;
import net.hasor.core.environment.EnvironmentWrap;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.SerializeCoder;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.utils.TimerManager;

import java.io.IOException;
/**
 *
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultRsfEnvironment extends EnvironmentWrap implements RsfEnvironment {
    private RsfSettings      rsfSettings  = null;
    private SerializeFactory factory      = null;
    private TimerManager     timerManager = null;
    //
    public DefaultRsfEnvironment(Environment environment) throws IOException {
        super(environment);
        this.rsfSettings = new DefaultRsfSettings(environment.getSettings());
        this.factory = SerializeFactory.createFactory(this);
        int connectTimeout = this.rsfSettings.getConnectTimeout();
        this.timerManager = new TimerManager(connectTimeout, "RSF-Timer", environment.getClassLoader());
    }
    @Override
    public RsfSettings getSettings() {
        return this.rsfSettings;
    }
    @Override
    public SerializeCoder getSerializeCoder(String codeName) {
        return this.factory.getSerializeCoder(codeName);
    }
    @Override
    public void atTime(TimerTask timeTask, int timeout) {
        this.timerManager.atTime(timeTask, timeout);
    }
    @Override
    public void atTime(TimerTask timerTask) {
        this.timerManager.atTime(timerTask);
    }
}