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
package net.hasor.rsf.center.server.manager;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.more.bizcommon.PageResult;
import org.more.bizcommon.Result;
import org.more.bizcommon.ResultDO;
import org.more.datachain.DataChainContext;
import org.more.util.CommonCodeUtils.MD5;
import org.more.util.ResourcesUtils;
import org.more.util.StringUtils;
import org.more.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.AppContext;
import net.hasor.core.EventContext;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.center.domain.PublishInfo;
import net.hasor.rsf.center.server.data.DataAdapter;
import net.hasor.rsf.center.server.data.datafilter.PublishInfo2ServiceInfoDODataFilter;
import net.hasor.rsf.center.server.domain.RsfCenterEvent;
import net.hasor.rsf.center.server.domain.RsfErrorCode;
import net.hasor.rsf.center.server.domain.entity.RuleFeatureDO;
import net.hasor.rsf.center.server.domain.entity.ServiceDO;
import net.hasor.rsf.center.server.domain.entity.StatusEnum;
import net.hasor.rsf.center.server.domain.entity.TerminalTypeEnum;
import net.hasor.rsf.center.server.domain.query.TerminalQuery;
import net.hasor.rsf.center.server.push.PushEvent;
/**
 * 
 * @version : 2016年2月22日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class BaseServiceManager {
    protected Logger    logger = LoggerFactory.getLogger(getClass());
    @Inject
    private AppContext  appContext;
    @Inject
    private DataAdapter dataAdapter;
    //
    /**如果数据库中不存在将PublishInfo保存到数据库，数据库中存在那么查询它。*/
    protected Result<ServiceDO> tryInitService(PublishInfo info) throws Throwable {
        //
        // .计算HashCode
        String serviceID = info.getBindID();
        String serviceGroup = info.getBindGroup();
        String serviceName = info.getBindName();
        String serviceVersion = info.getBindVersion();
        String serviceType = info.getBindType();
        String hashCodeOri = serviceID + serviceGroup + serviceName + serviceVersion + serviceType;
        String hashCode = MD5.getMD5(hashCodeOri) + ":" + hashCodeOri.length();
        //
        // .根据HashCode查询Service
        Result<ServiceDO> serviceResult = this.dataAdapter.queryServiceByHashCode(hashCode);
        if (serviceResult == null || serviceResult.isSuccess() == false) {
            /*查询失败的情况*/
            return new ResultDO<ServiceDO>(serviceResult).setSuccess(false).addMessage(serviceResult.getMessageList());
        }
        ServiceDO serviceDO = serviceResult.getResult();
        if (serviceDO != null) {
            return new ResultDO<ServiceDO>(serviceDO).setSuccess(true);//匹配到记录到情况
        }
        //
        // .转换PublishInfo到ServiceDO
        DataChainContext<PublishInfo, ServiceDO> dataChainContext = new DataChainContext<PublishInfo, ServiceDO>() {};
        dataChainContext.addDataFilter("dataFilter", this.appContext.getInstance(PublishInfo2ServiceInfoDODataFilter.class));
        serviceDO = dataChainContext.doChain(info);
        //
        // .保存到数据库（考虑到有几率发生多机同写而导致主键冲突的问题，因此忽略写结果后面直接获取）
        Result<Long> insertResult = this.dataAdapter.insertService(serviceDO);
        serviceResult = this.dataAdapter.queryServiceByHashCode(hashCode);
        int tryCount = 0, tryCountMax = 3;//3次重试
        while ((serviceResult == null || serviceResult.isSuccess() == false) && tryCount <= tryCountMax) {
            tryCount++; /*重新从数据库中查询出来，刚写入的数据由于读写分离情况可能导致数据同步不及时查询不出来。多试几次。*/
            Thread.sleep(50);
            serviceResult = this.dataAdapter.queryServiceByHashCode(hashCode);
        }
        serviceDO = serviceResult.getResult();
        //
        // .取得结果
        if (serviceDO == null) {
            return new ResultDO<ServiceDO>().setSuccess(false).addMessage(RsfErrorCode.ResultEmptyError.getTemplate());
        } else {
            return new ResultDO<ServiceDO>(serviceDO).setSuccess(true);
        }
    }
    //
    /**尝试加载服务的（路由、流控）*/
    protected ServiceDO tryLoadRule(ServiceDO serviceDO) throws Throwable {
        //
        if (serviceDO.getRuleFeature() == null) {
            serviceDO.setRuleFeature(new RuleFeatureDO());
        }
        //
        //1.初始化默认流控规则
        if (StringUtils.isBlank(serviceDO.getRuleFeature().getFlowControl())) {
            InputStream ins = ResourcesUtils.getResourceAsStream("/META-INF/rsf-center/default/flowcontrol.xml");
            String defaultData = IOUtils.toString(ins);
            serviceDO.getRuleFeature().setFlowControl(defaultData);
        }
        //
        //2.初始化默认服务级路由
        if (StringUtils.isBlank(serviceDO.getRuleFeature().getServiceLevelRule())) {
            InputStream ins = ResourcesUtils.getResourceAsStream("/META-INF/rsf-center/default/service-level.groovy");
            String defaultData = IOUtils.toString(ins);
            serviceDO.getRuleFeature().setServiceLevelRule(defaultData);
        }
        //
        //3.初始化默认方法级路由
        if (StringUtils.isBlank(serviceDO.getRuleFeature().getMethodLevelRule())) {
            InputStream ins = ResourcesUtils.getResourceAsStream("/META-INF/rsf-center/default/method-level.groovy");
            String defaultData = IOUtils.toString(ins);
            serviceDO.getRuleFeature().setMethodLevelRule(defaultData);
        }
        //
        //4.初始化默认参数级路由
        if (StringUtils.isBlank(serviceDO.getRuleFeature().getArgsLevelRule())) {
            InputStream ins = ResourcesUtils.getResourceAsStream("/META-INF/rsf-center/default/args-level.groovy");
            String defaultData = IOUtils.toString(ins);
            serviceDO.getRuleFeature().setArgsLevelRule(defaultData);
        }
        //
        return serviceDO;
    }
    //
    /**注册服务，并返回服务对象。*/
    protected Result<ServiceDO> addAndGetService(PublishInfo info) throws Throwable {
        Result<ServiceDO> result = this.tryInitService(info);
        if (result.isSuccess() && result.getResult() != null) {
            ServiceDO serviceDO = result.getResult();
            serviceDO = this.tryLoadRule(serviceDO);//加载脚本
            return new ResultDO<ServiceDO>(result).setResult(serviceDO).setSuccess(true);
        }
        return result;
    }
    //
    /**标记终端为失效，如果终端一直处于失效状态，在一段时间后，回收线程会自动清除它们。*/
    protected Result<Boolean> removeTerminal(InterAddress rsfHost, String forBindID, String saltValue) throws Throwable {
        String hostPort = rsfHost.getHostPort();
        return this.dataAdapter.offlineService(forBindID, hostPort, saltValue);
    }
    //
    /**处理某一个服务的心跳*/
    protected Result<Long> serviceBeat(InterAddress rsfHost, String forBindID, String saltValue) throws Throwable {
        String hostPort = rsfHost.getHostPort();
        Date beatTime = new Date();
        return this.dataAdapter.beatOfService(forBindID, hostPort, saltValue, beatTime);
    }
    //
    /**获取指定服务的提供者列表。*/
    public PageResult<InterAddress> getProviderList(TerminalQuery query) {
        query.setStatus(StatusEnum.online);
        query.setPersona(TerminalTypeEnum.Provider);
        //
        PageResult<String> hosts = this.dataAdapter.queryTerminalByQuery(query);
        if (hosts == null || hosts.isSuccess() == false) {
            return new PageResult<InterAddress>(hosts, new ArrayList<InterAddress>(0))//
                    .addMessage(hosts.getMessageList())//
                    .setSuccess(hosts.isSuccess())//
                    .setThrowable(hosts.getThrowable());
        }
        //
        List<InterAddress> providerList = new ArrayList<InterAddress>();
        for (String provider : hosts.getResult()) {
            try {
                providerList.add(new InterAddress(provider));
            } catch (Throwable e) {
                this.logger.error(e.getMessage(), e);
            }
        }
        return new PageResult<InterAddress>(hosts, providerList).setSuccess(true);
    }
    //
    /**获取指定服务的消费者列表。*/
    public PageResult<InterAddress> getConsumerList(TerminalQuery query) {
        query.setStatus(StatusEnum.online);
        query.setPersona(TerminalTypeEnum.Consumer);
        //
        PageResult<String> hosts = this.dataAdapter.queryTerminalByQuery(query);
        if (hosts == null || hosts.isSuccess() == false) {
            return new PageResult<InterAddress>(hosts, new ArrayList<InterAddress>(0))//
                    .addMessage(hosts.getMessageList())//
                    .setSuccess(hosts.isSuccess())//
                    .setThrowable(hosts.getThrowable());
        }
        //
        List<InterAddress> consumerList = new ArrayList<InterAddress>();
        for (String provider : hosts.getResult()) {
            try {
                consumerList.add(new InterAddress(provider));
            } catch (Throwable e) {
                this.logger.error(e.getMessage(), e);
            }
        }
        return new PageResult<InterAddress>(hosts, consumerList).setSuccess(true);
    }
    //
    /**发布推送事件*/
    protected void pushEvent(PushEvent pushEvent) {
        EventContext ec = this.appContext.getEnvironment().getEventContext();
        ec.fireSyncEvent(RsfCenterEvent.PushEvent, pushEvent);
    }
}