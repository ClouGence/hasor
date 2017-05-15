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
package net.hasor.registry.client;
import net.hasor.core.EventContext;
import net.hasor.core.Hasor;
import net.hasor.core.context.ContextStartListener;
import net.hasor.registry.RsfCenterListener;
import net.hasor.registry.RsfCenterRegister;
import net.hasor.registry.RsfCenterSettings;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfModule;
import net.hasor.rsf.domain.RsfEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Client模式
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
public class RegistryClientModule extends RsfModule {
    protected static Logger logger = LoggerFactory.getLogger(RegistryClientModule.class);
    private RsfCenterSettings centerSettings;
    //
    public RegistryClientModule(RsfCenterSettings centerSettings) {
        this.centerSettings = Hasor.assertIsNotNull(centerSettings);
    }
    //
    @Override
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        RsfEnvironment environment = apiBinder.getEnvironment();
        EventContext eventContext = environment.getEventContext();
        //
        // 1.监听RSF中所有服务的相关的消息
        RsfEventTransport transport = new RsfEventTransport();
        eventContext.addListener(RsfEvent.Rsf_ProviderService, transport);
        eventContext.addListener(RsfEvent.Rsf_ConsumerService, transport);
        eventContext.addListener(RsfEvent.Rsf_DeleteService, transport);
        eventContext.addListener(RsfEvent.Rsf_Online, transport);
        eventContext.addListener(RsfEvent.Rsf_Offline, transport);
        apiBinder.bindType(ContextStartListener.class).toInstance(transport);
        //
        // 2.接受来自注册中心的消息
        apiBinder.bindType(ContextStartListener.class).toInstance(transport);
        apiBinder.rsfService(RsfCenterListener.class)//服务类型
                .toInfo(apiBinder.bindType(RegistryClientReceiver.class).uniqueName().asEagerSingleton().toInfo())//服务实现
                .bindFilter("AuthFilter", RegistryClientVerifyFilter.class)//服务安全过滤器
                .asShadow().register();
        //
        // 3.向注册中心上报服务信息的服务
        InterAddress[] centerList = this.centerSettings.getCenterServerSet();
        StringBuilder strBuilder = buildLog(centerList);
        logger.info("rsf center-client hostSet = {}  -> center enable.", strBuilder.toString());
        apiBinder.rsfService(RsfCenterRegister.class)//服务类型
                .timeout(this.centerSettings.getCenterRsfTimeout())//服务接口超时时间
                .bindFilter("AuthFilter", RegistryClientVerifyFilter.class)//服务安全过滤器
                .bindAddress(null, centerList)//静态地址，用不失效
                .asShadow().register();//注册服务
        logger.info("rsf center-client started.");
    }
    //
    private static StringBuilder buildLog(InterAddress[] centerList) {
        StringBuilder strBuilder = new StringBuilder("");
        for (InterAddress address : centerList) {
            strBuilder.append(address.getHostPort());
            strBuilder.append(" ,");
        }
        if (centerList.length != 0) {
            strBuilder.deleteCharAt(strBuilder.length() - 1);
        }
        return strBuilder;
    }
}