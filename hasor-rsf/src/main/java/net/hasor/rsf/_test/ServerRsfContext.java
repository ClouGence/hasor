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
package net.hasor.rsf._test;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;
import net.hasor.core.Settings;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.executes.ExecutesManager;
import net.hasor.rsf.general.ProtocolVersion;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.runtime.RsfFilter;
import net.hasor.rsf.runtime.RsfFilterChain;
import net.hasor.rsf.runtime.RsfRequest;
import net.hasor.rsf.runtime.RsfResponse;
import net.hasor.rsf.runtime.context.DefaultRsfContext;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * 
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServerRsfContext extends DefaultRsfContext {
    static int               minCorePoolSize  = 2;
    static int               maxCorePoolSize  = 10;
    static int               queueSize        = 4096;
    static long              keepAliveTime    = 300L;
    private SerializeFactory serializeFactory = null;
    private ExecutesManager  manager          = null;
    //
    //
    public ServerRsfContext() throws IOException, URISyntaxException {
        manager = new ExecutesManager(minCorePoolSize, maxCorePoolSize, queueSize, keepAliveTime);
        Settings settings = new StandardContextSettings();
        settings.refresh();
        serializeFactory = SerializeFactory.createFactory(settings);
    }
    private EventLoopGroup group = new NioEventLoopGroup();
    public EventLoopGroup getLoopGroup() {
        return group;
    }
    //
    @Override
    public ServiceMetaData getService(String serviceName) {
        ServiceMetaData data = new ServiceMetaData();
        data.setServiceName(serviceName);
        return data;
    }
    @Override
    public Executor getCallExecute(String serviceName) {
        return manager.getExecute(serviceName);
    }
    @Override
    public SerializeFactory getSerializeFactory() {
        return this.serializeFactory;
    }
    @Override
    public Object getBean(ServiceMetaData metaData) {
        return new TestServices();
    }
    @Override
    public Class<?> getBeanType(ServiceMetaData metaData) {
        return TestServices.class;
    }
    @Override
    public RsfFilter[] getRsfFilters(ServiceMetaData metaData) {
        return new RsfFilter[] { new RsfFilter() {
            public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
                aa(request);
                chain.doFilter(request, response);
            }
        } };
    }
    //
    //
    //
    private static volatile long requestCount = 0;
    private static volatile long start        = System.currentTimeMillis();
    public static void print() {
        while (true) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long duration = System.currentTimeMillis() - start;
            long qps = requestCount * 1000 / duration;
            System.out.println("QPS         :" + qps);
            System.out.println("requestCount:" + requestCount);
            System.out.println();
        }
    }
    public static void aa(RsfRequest request) {
        requestCount++;
    }
    @Override
    public byte getVersion() {
        return ProtocolVersion.V_1_0.value();
    }
}
