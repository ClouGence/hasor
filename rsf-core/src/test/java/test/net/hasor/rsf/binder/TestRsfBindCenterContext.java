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
package test.net.hasor.rsf.binder;
import io.netty.channel.EventLoopGroup;
import java.util.concurrent.Executor;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.AddressPool;
import net.hasor.rsf.binder.RsfBindCenter;
import net.hasor.rsf.rpc.client.RsfRequestManager;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * 
 * @version : 2015年4月17日
 * @author 赵永春(zyc@hasor.net)
 */
public class TestRsfBindCenterContext extends AbstractRsfContext {
    DefaultRsfSettings rsfSettings;
    AddressPool        addressPool;
    RsfBindCenter      bindCenter;
    public TestRsfBindCenterContext(DefaultRsfSettings rsfSettings, String unitName ) {
        this.rsfSettings = rsfSettings;
        this.bindCenter = new RsfBindCenter(this);
        this.addressPool = new AddressPool(unitName, bindCenter, rsfSettings);
    }
    @Override
    public RsfBindCenter getBindCenter() {
        return bindCenter;
    }
    @Override
    public AddressPool getAddressPool() {
        // TODO Auto-generated method stub
        return addressPool;
    }
    @Override
    public RsfSettings getSettings() {
        return rsfSettings;
    }
    //
    //
    //
    public <T> Provider<T> getProvider(RsfBindInfo<T> bindInfo) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public RsfClient getRsfClient() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void shutdown() {
        // TODO Auto-generated method stub
    }
    @Override
    public Executor getCallExecute(String serviceName) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public SerializeFactory getSerializeFactory() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public EventLoopGroup getLoopGroup() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public RsfRequestManager getRequestManager() {
        // TODO Auto-generated method stub
        return null;
    }
}