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
import java.util.List;
import org.apache.zookeeper.data.Stat;
import org.more.RepeateException;
import org.more.util.ResourcesUtils;
import org.more.util.StringUtils;
import org.more.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.AppContext;
import net.hasor.core.EventContext;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.rsf.center.domain.PublishInfo;
import net.hasor.rsf.center.server.core.zktmp.ZkTmpService;
import net.hasor.rsf.center.server.core.zookeeper.ZkNodeType;
import net.hasor.rsf.center.server.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.center.server.domain.RsfCenterEvent;
import net.hasor.rsf.center.server.push.PushEvent;
import net.hasor.rsf.center.server.utils.DateCenterUtils;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.rsf.domain.RsfServiceType;
/**
 * 
 * @version : 2016年2月22日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class BaseServiceManager {
    protected Logger       logger = LoggerFactory.getLogger(RsfConstants.RsfCenter_Logger);
    @Inject
    private AppContext     appContext;
    @Inject
    private ZooKeeperNode  zooKeeperNode;
    @Inject
    protected PathManager  pathManager;
    @Inject
    protected ZkTmpService zkTmpService;
    //
    /**初始化服务的：路由脚本、流控规则。*/
    protected void initScripts(PublishInfo info) throws Throwable {
        String serviceID = info.getBindID();
        //
        //1.初始化默认流控规则
        String flowcontrolPath = pathManager.evalFlowControlPath(serviceID);
        String data = this.zooKeeperNode.readData(flowcontrolPath);
        if (StringUtils.isBlank(data)) {
            InputStream ins = ResourcesUtils.getResourceAsStream("/META-INF/rsf-center/default/flowcontrol.xml");
            String defaultData = IOUtils.toString(ins);
            this.zooKeeperNode.createNode(ZkNodeType.Persistent, flowcontrolPath);
            this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, flowcontrolPath, defaultData);
        }
        //
        //2.初始化默认服务级路由
        String serviceLevelRuleScriptPath = pathManager.evalServiceLevelRuleScriptPath(serviceID);
        String serviceLevelData = this.zooKeeperNode.readData(serviceLevelRuleScriptPath);
        if (StringUtils.isBlank(serviceLevelData)) {
            InputStream ins = ResourcesUtils.getResourceAsStream("/META-INF/rsf-center/default/service-level.groovy");
            String defaultData = IOUtils.toString(ins);
            this.zooKeeperNode.createNode(ZkNodeType.Persistent, serviceLevelRuleScriptPath);
            this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, serviceLevelRuleScriptPath, defaultData);
        }
        //
        //3.初始化默认方法级路由
        String methodLevelRuleScriptPath = pathManager.evalMethodLevelRuleScriptPath(serviceID);
        String methodLevelData = this.zooKeeperNode.readData(methodLevelRuleScriptPath);
        if (StringUtils.isBlank(methodLevelData)) {
            InputStream ins = ResourcesUtils.getResourceAsStream("/META-INF/rsf-center/default/method-level.groovy");
            String defaultData = IOUtils.toString(ins);
            this.zooKeeperNode.createNode(ZkNodeType.Persistent, methodLevelRuleScriptPath);
            this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, methodLevelRuleScriptPath, defaultData);
        }
        //
        //4.初始化默认参数级路由
        String argsLevelRuleScriptPath = pathManager.evalArgsLevelRuleScriptPath(serviceID);
        String argsLevelData = this.zooKeeperNode.readData(argsLevelRuleScriptPath);
        if (StringUtils.isBlank(argsLevelData)) {
            InputStream ins = ResourcesUtils.getResourceAsStream("/META-INF/rsf-center/default/args-level.groovy");
            String defaultData = IOUtils.toString(ins);
            this.zooKeeperNode.createNode(ZkNodeType.Persistent, argsLevelRuleScriptPath);
            this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, argsLevelRuleScriptPath, defaultData);
        }
        //
        return;
    }
    //
    /**注册服务，该方法会检测服务的注册是否冲突。*/
    protected String addServices(PublishInfo info) throws Throwable {
        String serviceID = info.getBindID();
        String servicePath = pathManager.evalServicePath(serviceID);
        String serviceInfoPath = pathManager.evalServiceInfoPath(serviceID);
        //
        String data = this.zooKeeperNode.readData(serviceInfoPath);
        if (StringUtils.isNotBlank(data)) {
            int startIndex = data.indexOf("<hashCode>");
            int endIndex = data.indexOf("</hashCode>");
            String hashCodeA = data.substring(startIndex + "<hashCode>".length(), endIndex);
            String hashCodeB = this.zkTmpService.publishInfoHashCode(info);
            if (!StringUtils.equals(hashCodeA, hashCodeB)) {
                throw new RepeateException("service " + info.getBindID() + " conflict.");
            }
        }
        //
        String serviceInfo = this.zkTmpService.serviceInfo(info);
        this.zooKeeperNode.createNode(ZkNodeType.Persistent, servicePath);
        Stat stat = this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, serviceInfoPath, serviceInfo);
        if (stat == null) {
            return null;
        }
        //
        this.initScripts(info);
        //
        return servicePath;
    }
    protected boolean removeRegister(String hostString, String serviceID, RsfServiceType rsfServiceType) throws Throwable {
        String terminalPath = null;
        if (rsfServiceType == RsfServiceType.Consumer) {
            terminalPath = pathManager.evalConsumerTermPath(serviceID, hostString);
        } else if (rsfServiceType == RsfServiceType.Provider) {
            terminalPath = pathManager.evalProviderTermPath(serviceID, hostString);
        } else {
            return false;
        }
        if (StringUtils.isBlank(terminalPath)) {
            return false;
        }
        //
        if (this.zooKeeperNode.existsNode(terminalPath) == true) {
            //
            String data = this.zooKeeperNode.readData(terminalPath);
            if (StringUtils.isNotBlank(data)) {
                int startIndex = data.indexOf("<bindID>");
                int endIndex = data.indexOf("</bindID>");
                String bindID = data.substring(startIndex + "<bindID>".length(), endIndex);
                if (StringUtils.equals(serviceID, bindID)) {
                    this.zooKeeperNode.deleteNode(terminalPath);//删除失败也无所谓，Leader会定时清理数据
                    // --引发事件，通知推送进程推送服务地址
                    logger.info("removeRegister serviceID={} ,terminalPath={} ,type={}", serviceID, terminalPath, rsfServiceType);
                    return true;
                } else {
                    logger.error("removeRegister (ids not eq) failed. -> serviceID={} ,bindID={} ,terminalPath={} ,type={}", //
                            serviceID, bindID, terminalPath, rsfServiceType);
                }
            } else {
                logger.error("removeRegister (data is empty of terminalPath) failed. -> serviceID= {} ,terminalPath= {} ,type={}", //
                        serviceID, terminalPath, rsfServiceType);
            }
        } else {
            logger.error("removeRegister (terminalPath not exists ) failed. -> serviceID= {} ,terminalPath= {} ,type={}", //
                    serviceID, terminalPath, rsfServiceType);
        }
        //
        return false;
    }
    /**处理某一个服务的心跳*/
    protected boolean serviceBeat(String hostString, String serviceID, RsfServiceType rsfServiceType) throws Throwable {
        String beatPath = null;
        if (rsfServiceType == RsfServiceType.Consumer) {
            beatPath = pathManager.evalConsumerTermBeatPath(serviceID, hostString);
        } else if (rsfServiceType == RsfServiceType.Provider) {
            beatPath = pathManager.evalProviderTermBeatPath(serviceID, hostString);
        } else {
            return false;
        }
        if (StringUtils.isBlank(beatPath)) {
            return false;
        }
        //
        if (this.zooKeeperNode.existsNode(beatPath) == false) {
            return false;
        }
        //
        return this.updateBeat(beatPath);
    }
    /**在指定位置上生成一个子节点，用于保存心跳数据。*/
    protected boolean updateBeat(String beatPath) throws Throwable {
        String beatData = DateCenterUtils.beatData();
        Stat s = this.saveOrUpdateNode(ZkNodeType.Persistent, beatPath, beatData);
        return s != null;
    }
    /**获取指定服务的提供者列表。*/
    public List<String> getProviderList(String serviceID) {
        String providerPath = this.pathManager.evalProviderPath(serviceID);
        List<String> providerList = null;
        try {
            providerList = this.zooKeeperNode.getChildrenNode(providerPath);
        } catch (Throwable e) {
            logger.error("find providerList failed ->" + e.getMessage(), e);
        }
        List<String> result = new ArrayList<String>();
        if (providerList != null) {
            for (String provider : providerList) {
                result.add(this.convertTo(provider));
            }
        }
        return result;
    }
    /**获取指定服务的消费者列表。*/
    public List<String> getConsumerList(String serviceID) {
        String consumerPath = this.pathManager.evalConsumerPath(serviceID);
        List<String> consumerList = null;
        try {
            consumerList = this.zooKeeperNode.getChildrenNode(consumerPath);
        } catch (Throwable e) {
            logger.error("find providerList failed ->" + e.getMessage(), e);
        }
        List<String> result = new ArrayList<String>();
        if (consumerList != null) {
            for (String consumer : consumerList) {
                result.add(this.convertTo(consumer));
            }
        }
        return result;
    }
    /**发布推送事件*/
    protected void pushEvent(PushEvent pushEvent) {
        EventContext ec = this.appContext.getEnvironment().getEventContext();
        ec.fireSyncEvent(RsfCenterEvent.PushEvent, pushEvent);
    }
    protected String readData(String dataPath) throws Throwable {
        return this.zooKeeperNode.readData(dataPath);
    }
    protected Stat saveOrUpdateNode(ZkNodeType persistent, String infoPath, String beatData) throws Throwable {
        return this.zooKeeperNode.saveOrUpdate(persistent, infoPath, beatData);
    }
    protected void createNode(ZkNodeType persistent, String infoPath) throws Throwable {
        this.zooKeeperNode.createNode(persistent, infoPath);
    }
    protected String convertTo(String hostString) {
        return "rsf://" + hostString.replace("@", "/");
    }
}