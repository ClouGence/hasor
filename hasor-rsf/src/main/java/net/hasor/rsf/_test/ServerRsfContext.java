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
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.Settings;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.runtime.RsfFilter;
import net.hasor.rsf.runtime.RsfFilterChain;
import net.hasor.rsf.runtime.RsfRequest;
import net.hasor.rsf.runtime.RsfResponse;
import net.hasor.rsf.runtime.context.AbstractRsfContext;
/**
 * 
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServerRsfContext extends AbstractRsfContext {
    Settings settings = null;
    //
    public ServerRsfContext() throws IOException, URISyntaxException {
        settings = new StandardContextSettings();
        settings.refresh();
        new Thread(new Runnable() {
            public void run() {
                print();
            }
        }).start();
        this.init();
    }
    //
    private ServiceMetaData data = null;
    public ServiceMetaData getService(String serviceName) {
        if (data == null) {
            data = new ServiceMetaData(TestServices.class);
            data.setServiceName("net.hasor.rsf._test.TestServices");
            data.setServiceVersion("1.0.0");
            data.setServiceGroup("default");
            data.setSerializeType("Hessian");
        }
        return data;
    }
    private TestServices test = null;
    public Object getBean(ServiceMetaData metaData) {
        if (test == null) {
            this.test = new TestServices();
        }
        return this.test;
    }
    public Class<?> getBeanType(ServiceMetaData metaData) {
        return TestServices.class;
    }
    RsfFilter[] filter = new RsfFilter[] { new RsfFilter() {
                           public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
                               aa(request);
                               chain.doFilter(request, response);
                           }
                       } };
    public RsfFilter[] getRsfFilters(ServiceMetaData metaData) {
        return filter;
    }
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
    public Settings getSettings() {
        return settings;
    }
}
