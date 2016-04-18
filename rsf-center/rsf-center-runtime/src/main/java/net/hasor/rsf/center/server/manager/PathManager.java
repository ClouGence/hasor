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
import org.more.util.StringUtils;
import net.hasor.core.Singleton;
import net.hasor.rsf.center.server.core.zookeeper.ZooKeeperNode;
/**
 * Zookeeper中地址目录Manager，所有有关Zk目录相关的计算放到这里。
 * @version : 2016年2月22日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class PathManager {
    //    /** ZK系统中的基准节点 */
    //    public static final String ROOT_PATH     = ZooKeeperNode.ROOT_PATH;    // --> /rsf-center
    //    /** servers信息保存的节点 */
    //    public static final String SERVER_PATH   = ZooKeeperNode.SERVER_PATH;  // --> /rsf-center/servers
    //    /** leader信息保存的节点 */
    //    public static final String LEADER_PATH   = ZooKeeperNode.LEADER_PATH;  // --> /rsf-center/leader
    //    /** config信息保存的节点 */
    //    public static final String CONFIG_PATH   = ZooKeeperNode.CONFIG_PATH;  // --> /rsf-center/config
    /** services信息保存的节点 */
    public static final String SERVICES_PATH = ZooKeeperNode.SERVICES_PATH;// --> /rsf-center/services
    //
    //
    public String evalArgsLevelRuleScriptPath(String serviceID) {
        return SERVICES_PATH + "/" + converPath(serviceID) + "/rule/args-level";
    }
    public String evalMethodLevelRuleScriptPath(String serviceID) {
        return SERVICES_PATH + "/" + converPath(serviceID) + "/rule/method-level";
    }
    public String evalServiceLevelRuleScriptPath(String serviceID) {
        return SERVICES_PATH + "/" + converPath(serviceID) + "/rule/service-level";
    }
    public String evalFlowControlPath(String serviceID) {
        return SERVICES_PATH + "/" + converPath(serviceID) + "/flowcontrol";
    }
    public String evalServicePath(String serviceID) {
        return SERVICES_PATH + "/" + converPath(serviceID);
    }
    public String evalServiceInfoPath(String serviceID) {
        return SERVICES_PATH + "/" + converPath(serviceID) + "/info";
    }
    public String evalConsumerPath(String serviceID) {
        return SERVICES_PATH + "/" + converPath(serviceID) + "/consumer";
    }
    public String evalConsumerTermPath(String serviceID, String hostString) {
        return SERVICES_PATH + "/" + converPath(serviceID) + "/consumer/" + hostString;
    }
    public String evalConsumerTermBeatPath(String serviceID, String hostString) {
        return SERVICES_PATH + "/" + converPath(serviceID) + "/consumer/" + hostString + "/beat";
    }
    public String evalProviderPath(String serviceID) {
        return SERVICES_PATH + "/" + converPath(serviceID) + "/provider";
    }
    public String evalProviderTermPath(String serviceID, String hostString) {
        return SERVICES_PATH + "/" + converPath(serviceID) + "/provider/" + hostString;
    }
    public String evalProviderTermBeatPath(String serviceID, String hostString) {
        return SERVICES_PATH + "/" + converPath(serviceID) + "/provider/" + hostString + "/beat";
    }
    protected static String converPath(String serviceID) {
        if (StringUtils.isBlank(serviceID) == true || serviceID.charAt(0) != '[') {
            throw new IllegalArgumentException(serviceID + " formater error, correct format is: [RSF]org.demo...demo.Service-1.0.0");
        }
        try {
            //
            int startIndex = serviceID.indexOf("[");
            int endIndex = serviceID.indexOf("]");
            int versionIndex = serviceID.indexOf("-");
            //group、name、version
            String group = serviceID.substring(startIndex + 1, endIndex);
            String name = serviceID.substring(endIndex + 1, versionIndex);
            String version = serviceID.substring(versionIndex + 1, serviceID.length());
            //
            return group + "/" + name + "/" + version;
        } catch (Exception e) {
            throw new IllegalArgumentException(serviceID + " formater error, correct format is: [RSF]org.demo...demo.Service-1.0.0");
        }
    }
}