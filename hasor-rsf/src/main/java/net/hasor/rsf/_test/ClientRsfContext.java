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
import net.hasor.rsf.metadata.ServiceMetaData.Mode;
import net.hasor.rsf.plugins.QPSPlugin;
import net.hasor.rsf.runtime.RsfFilter;
import net.hasor.rsf.runtime.context.AbstractRsfContext;
/**
 * 
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ClientRsfContext extends AbstractRsfContext {
    private RsfFilter[] plugins  = new RsfFilter[] { new QPSPlugin() /*, new LocalPrefPlugin()*/};
    private Settings    settings = new StandardContextSettings();
    //
    public ClientRsfContext() throws IOException, URISyntaxException {
        this.settings.refresh();
        this.init();
        //
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {}
                    QPSPlugin plugins = (QPSPlugin) ClientRsfContext.this.plugins[0];
                    System.out.println("QPS         :" + plugins.getQPS());
                    System.out.println("requestCount:" + plugins.getOkCount());
                    System.out.println();
                }
            }
        }).start();
    }
    public Settings getSettings() {
        return this.settings;
    }
    public RsfFilter[] getRsfFilters(ServiceMetaData metaData) {
        return this.plugins;
    }
    //
    private ServiceMetaData data = null;
    public ServiceMetaData getService(String serviceName) {
        if (data == null) {
            data = new ServiceMetaData(Mode.Provider, TestServices.class);
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
}
