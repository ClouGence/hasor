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
package net.hasor.rsf.center.server;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.more.RepeateException;
import org.more.util.CommonCodeUtils.MD5;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.Inject;
import net.hasor.rsf.center.RsfCenterRegister;
import net.hasor.rsf.center.core.zktmp.ZkTmpService;
import net.hasor.rsf.center.core.zookeeper.ZkNodeType;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.center.domain.ConsumerPublishInfo;
import net.hasor.rsf.center.domain.ProviderPublishInfo;
import net.hasor.rsf.center.domain.PublishInfo;
import net.hasor.rsf.center.utils.DateCenterUtils;
/**
 * 客户端注册中心接口{@link RsfCenterRegister}实现，负责将来自客户端的RSF注册请求发到zk集群。
 * @version : 2015年6月8日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfCenterRegisterProvider implements RsfCenterRegister {
    protected Logger      logger = LoggerFactory.getLogger(getClass());
    @Inject
    private ZooKeeperNode zooKeeperNode;
    @Inject
    private ZkTmpService  zkTmpService;
    //
    //
    /**注册服务，该方法会检测服务的注册是否冲突。*/
    private String addServices(String hostString, PublishInfo info) throws Throwable, KeeperException, InterruptedException {
        String servicePath = "/rsf-center/services/" + info.getBindGroup() + "/" + info.getBindName() + "/" + info.getBindVersion();
        String serviceInfoPath = servicePath + "/info";
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
        Stat s1 = this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, serviceInfoPath, serviceInfo);
        if (s1 == null) {
            return null;
        }
        return servicePath;
    }
    //
    /**发布服务*/
    @Override
    public String publishService(String hostString, ProviderPublishInfo info) {
        try {
            //
            //1.注册服务：/rsf-center/services/group/name/version/info
            String servicePath = this.addServices(hostString, info);
            if (servicePath == null) {
                logger.error("publishService failed -> save providerInfo to zk failed, hostString ={} ,serviceID ={}", hostString, info.getBindID());
                return null;
            }
            //
            //2.登记提供者：/rsf-center/services/group/name/version/provider/192.168.1.11:2180
            String providerInfo = this.zkTmpService.providerInfo(info);
            String providerInfoPath = servicePath + "/provider/" + hostString;
            this.zooKeeperNode.createNode(ZkNodeType.Persistent, providerInfoPath);
            Stat s2 = this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, providerInfoPath, providerInfo);
            if (s2 == null) {
                logger.error("publishService failed -> save providerInfo to zk failed, hostString ={} ,serviceID ={}", hostString, info.getBindID());
                return null;
            }
            //
            //3.服务注册信息
            String registerID = (s2 == null) ? null : MD5.getMD5(servicePath + providerInfoPath);
            String registerPath = ZooKeeperNode.REGISTERS_PATH + "/" + registerID;
            String registerBody = DateCenterUtils.timestamp() + "@provider@" + providerInfoPath;
            this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, registerPath, registerBody);
            //
            logger.info("publishService {} form {} -> {}", info.getBindID(), hostString, registerID);
            return registerID;
        } catch (Throwable e) {
            logger.error("publishService -> " + e.getMessage(), e);
            return null;
        }
    }
    @Override
    public String repairPublishService(String hostString, String oldRegisterID, ProviderPublishInfo info) {
        return this.publishService(hostString, info);
    }
    //
    /**订阅服务*/
    @Override
    public String receiveService(String hostString, ConsumerPublishInfo info) {
        try {
            //
            //1.注册服务：/rsf-center/services/group/name/version/info
            String servicePath = this.addServices(hostString, info);
            if (servicePath == null) {
                logger.error("receiveService failed -> save consumerInfo to zk failed, hostString ={} ,serviceID ={}", hostString, info.getBindID());
                return null;
            }
            //
            //2.登记消费者：/rsf-center/services/group/name/version/consumer/192.168.1.11:2180
            String consumerInfo = this.zkTmpService.consumerInfo(info);
            String consumerInfoPath = servicePath + "/consumer/" + hostString;
            this.zooKeeperNode.createNode(ZkNodeType.Persistent, consumerInfoPath);
            Stat s2 = this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, consumerInfoPath, consumerInfo);
            if (s2 == null) {
                logger.error("receiveService failed -> save consumerInfo to zk failed, hostString ={} ,serviceID ={}", hostString, info.getBindID());
                return null;
            }
            //
            //3.服务注册信息
            String registerID = (s2 == null) ? null : MD5.getMD5(servicePath + consumerInfoPath);
            String registerPath = ZooKeeperNode.REGISTERS_PATH + "/" + registerID;
            String registerBody = DateCenterUtils.timestamp() + "@consumer@" + consumerInfoPath;
            this.zooKeeperNode.saveOrUpdate(ZkNodeType.Persistent, registerPath, registerBody);
            //
            String returnData = (s2 == null) ? null : MD5.getMD5(servicePath + consumerInfoPath);
            logger.info("receiveService {} form {} -> {}", info.getBindID(), hostString, returnData);
            return returnData;
        } catch (Throwable e) {
            logger.error("receiveService -> " + e.getMessage(), e);
            return null;
        }
    }
    @Override
    public String repairReceiveService(String hostString, String oldRegisterID, ConsumerPublishInfo info) {
        return this.receiveService(hostString, info);
    }
    //
    //
    //
    @Override
    public boolean removeRegister(String hostString, String registerID) {
        return true; // TODO Auto-generated method stub
    }
    @Override
    public boolean[] serviceBeat(String hostString, String[] registerID) {
        boolean[] res = new boolean[registerID.length];
        for (int i = 0; i < res.length; i++)
            res[i] = true;
        return res;// TODO Auto-generated method stub
    }
}