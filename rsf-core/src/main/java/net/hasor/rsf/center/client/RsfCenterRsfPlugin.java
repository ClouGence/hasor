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
package net.hasor.rsf.center.client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.EventContext;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfPlugin;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.center.RsfCenterListener;
import net.hasor.rsf.center.RsfCenterRegister;
import net.hasor.rsf.domain.RsfEvent;
/**
 * 注册中心插件
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfCenterRsfPlugin implements RsfPlugin {
    protected static Logger logger = LoggerFactory.getLogger(RsfCenterRsfPlugin.class);
    @Override
    public void loadRsf(RsfContext rsfContext) throws Throwable {
        boolean enable = rsfContext.getSettings().isEnableCenter();
        if (!enable) {
            logger.info("rsf center hostSet is empyt -> center disable.");
            return;
        }
        //
        RsfBinder rsfBinder = rsfContext.binder();
        //
        // 1.注册中心消息接收接口
        rsfBinder.rsfService(RsfCenterListener.class)//服务类型
                .toInstance(new RsfCenterDataReceiver(rsfContext))//服务实现
                .bindFilter("AuthFilter", new RsfCenterDataVerificationFilter(rsfContext))//服务安全过滤器
                .register();//注册服务
        //
        // 2.注册中心消息发送接口
        InterAddress[] centerList = rsfContext.getSettings().getCenterServerSet();
        int faceTimer = rsfContext.getSettings().getCenterRsfTimeout();
        StringBuilder strBuilder = new StringBuilder("");
        for (InterAddress address : centerList) {
            strBuilder.append(address.getHostPort() + " ,");
        }
        if (centerList.length != 0) {
            strBuilder.deleteCharAt(strBuilder.length() - 1);
        }
        logger.info("rsf center hostSet = {}  -> center enable.", strBuilder.toString());
        rsfBinder.rsfService(RsfCenterRegister.class)//服务类型
                .timeout(faceTimer)//服务接口超时时间
                .bindFilter("AuthFilter", new RsfCenterDataVerificationFilter(rsfContext))//服务安全过滤器
                .bindStaticAddress(null, centerList)//
                .register();
        //
        // 3.注册RSF事件监听器
        EventContext eventContext = rsfContext.getAppContext().getEnvironment().getEventContext();
        RsfEventTransport transport = new RsfEventTransport(rsfContext);
        eventContext.addListener(RsfEvent.Rsf_ProviderService, transport);
        eventContext.addListener(RsfEvent.Rsf_ConsumerService, transport);
        eventContext.addListener(RsfEvent.Rsf_DeleteService, transport);
        //
        eventContext.addListener(RsfEvent.Rsf_Started, transport);
        eventContext.addListener(RsfEvent.Rsf_Shutdown, transport);
        //
        eventContext.addListener(RsfEvent.Rsf_Online, transport);
        eventContext.addListener(RsfEvent.Rsf_Offline, transport);
    }
}