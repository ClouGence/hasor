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
package net.hasor.registry.boot;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.registry.InstanceInfo;
import net.hasor.registry.RegistryCenter;
import net.hasor.rsf.RsfContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @version : 2014年11月12日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class RegistryCenterService implements RegistryCenter {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private RsfContext   rsfContext;
    private InstanceInfo instance;
    //
    //
    @Init
    public void init() {
        //
        String protocol = rsfContext.getDefaultProtocol();
        this.instance = new InstanceInfo();
        this.instance.setInstanceID(rsfContext.getInstanceID());
        this.instance.setUnitName(rsfContext.getSettings().getUnitName());
        this.instance.setDefaultProtocol(protocol);
        this.instance.setRsfAddress(rsfContext.publishAddress(protocol).toHostSchema());
        //
        List<String> runProtocols = new ArrayList<String>(rsfContext.runProtocols());
        this.instance.setRunProtocols(runProtocols);
    }
    @Override
    public InstanceInfo getInstanceInfo() {
        return this.instance;
    }
}