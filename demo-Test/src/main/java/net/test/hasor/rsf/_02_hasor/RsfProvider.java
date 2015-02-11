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
package net.test.hasor.rsf._02_hasor;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.plugins.hasor.RsfApiBinder;
import net.hasor.rsf.plugins.hasor.RsfModule;
import net.test.hasor.rsf.EchoService;
import net.test.hasor.rsf.EchoServiceImpl;
/**
 * 
 * @version : 2014年9月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfProvider extends RsfModule {
    private int usePort = 0;
    public RsfProvider(int port) {
        this.usePort = port;
    }
    protected int bindPort() {
        return usePort;
    }
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        RsfBinder rsfBinder = apiBinder.getRsfBinder();
        rsfBinder.rsfService(EchoService.class, new EchoServiceImpl()).register();
    }
}