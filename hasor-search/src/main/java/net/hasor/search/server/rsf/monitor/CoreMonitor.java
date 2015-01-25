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
package net.hasor.search.server.rsf.monitor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.BindInfo;
import net.hasor.core.EventListener;
import net.hasor.core.Provider;
import net.hasor.core.Settings;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfBinder.RegisterReference;
import net.hasor.rsf.RsfContext;
import net.hasor.search.server.rsf.service.ReadOptionFilter;
import org.apache.solr.core.CoreContainer;
import org.more.logger.LoggerHelper;
/**
 * 监视CoreNames变化。当发生变化时，同步注册和解除对应的RSF服务。
 * @version : 2015年1月24日
 * @author 赵永春(zyc@hasor.net)
 */
public class CoreMonitor<T> extends Thread implements AppContextAware, EventListener {
    private BindInfo<ReadOptionFilter>        rsfFilter     = null;
    private BindInfo<T>                       serviceInfo   = null;
    private AppContext                        appContext    = null;
    private CoreContainer                     coreContainer = null;
    private Map<String, RegisterReference<T>> bindNames     = null;
    //
    public CoreMonitor(BindInfo<ReadOptionFilter> rsfFilter, BindInfo<T> serviceInfo) {
        this.rsfFilter = rsfFilter;
        this.serviceInfo = serviceInfo;
        this.bindNames = new HashMap<String, RegisterReference<T>>();
    }
    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
        this.coreContainer = appContext.getInstance(CoreContainer.class);
    }
    @Override
    public void onEvent(String event, Object[] params) throws Throwable {
        this.setDaemon(true);
        this.setName(String.format("CoreMonitor-{%s}", serviceInfo.getBindID()));
        this.start();
    }
    @Override
    public void run() {
        Settings settings = this.appContext.getEnvironment().getSettings();
        while (true) {
            int refreshTime = settings.getInteger("searchConfig.coreMonitor.refreshTime", 5);
            try {
                Thread.sleep(refreshTime * 1000);
            } catch (InterruptedException e) {
                LoggerHelper.logSevere(e.getMessage(), e);
            }
            this.refresh();
        }
    }
    public void refresh() {
        final Provider<T> serviceProvider = this.appContext.getProvider(this.serviceInfo);
        final Provider<ReadOptionFilter> filterProvider = this.appContext.getProvider(this.rsfFilter);
        final RsfContext rsfContext = this.appContext.getInstance(RsfContext.class);
        final RsfBinder rsfBinder = rsfContext.getBindCenter().getRsfBinder();
        final Class<T> serviceType = this.serviceInfo.getBindType();
        final String version = rsfContext.getSettings().getDefaultVersion();
        LoggerHelper.logInfo("CoreMonitor.refresh , ServiceType = " + serviceType);
        //
        //1.提取差异使用
        Collection<String> nowNames = this.coreContainer.getCoreNames();
        //
        //2.注册新的Core
        for (String nowName : nowNames) {
            if (this.bindNames.containsKey(nowName) == false) {
                RegisterReference<T> register = rsfBinder.rsfService(serviceType, serviceProvider)//
                        .ngv(nowName, serviceType.getName(), version)//
                        .bindFilter("ReadCoreNameFilter", filterProvider)//
                        .register();
                this.bindNames.put(nowName, register);
                LoggerHelper.logInfo("register NewCore[%s].", register);
            }
        }
        //
        //3.删除不存在的Core
        List<String> delete = new ArrayList<String>();
        for (String bindName : this.bindNames.keySet()) {
            if (nowNames.contains(bindName) == false) {
                delete.add(bindName);
            }
        }
        for (String bindName : delete) {
            this.bindNames.remove(bindName).unRegister();
            LoggerHelper.logInfo("remove register oldCore[%s].", bindName);
        }
    }
}