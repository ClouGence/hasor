/* Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License. */
package net.hasor.rsf.center.core.cluster;
import static net.hasor.rsf.center.core.startup.StartAppModule.RSFCenterCluster_StartEvent;
import static net.hasor.rsf.center.core.startup.StartAppModule.RSFCenterCluster_StopEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.util.ResourcesUtils;
import org.more.util.StringUtils;
import org.more.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.Inject;
import net.hasor.plugins.event.Event;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.center.domain.constant.RsfCenterCfg;
import net.hasor.rsf.center.domain.constant.ZkNodeType;
/**
 * 集群数据协调器，负责读写zk集群数据信息。
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
@Event({ RSFCenterCluster_StartEvent, RSFCenterCluster_StopEvent })
public class DataDiplomat implements EventListener {
    protected Logger      logger = LoggerFactory.getLogger(getClass());
    @Inject
    private ZooKeeperNode zkNode;
    @Inject
    private RsfCenterCfg  rsfCenterCfg;
    @Inject
    private AppContext    appContext;
    private Configuration configuration;
    private String        serverInfo;
    private String        serverInfoPath;
    //
    public void initDiplomatEnv() throws IOException {
        //
        this.serverInfo = this.rsfCenterCfg.getHostAndPort();
        this.serverInfoPath = ZooKeeperNode.SERVER_PATH + "/" + this.serverInfo;
        //
        this.configuration = new Configuration(Configuration.VERSION_2_3_22);
        this.configuration.setTemplateLoader(new ClassPathTemplateLoader());
        //
        this.configuration.setDefaultEncoding("utf-8");// 默认页面编码UTF-8
        this.configuration.setOutputEncoding("utf-8");// 输出编码格式UTF-8
        this.configuration.setLocalizedLookup(false);// 是否开启国际化false
        this.configuration.setNumberFormat("0");
        this.configuration.setClassicCompatible(true);// null值测处理配置
    }
    //
    /** 获取RSF-Center服务器信息 */
    public String serverInfo() throws Throwable {
        String fmt = "/META-INF/zookeeper/server-info.tmp";
        Template template = this.configuration.getTemplate(fmt, "UTF-8");
        StringWriter writer = new StringWriter();
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("cfg", this.rsfCenterCfg);
        template.process(dataModel, writer);
        return writer.toString();
    }
    /** 获取RSF-Center服务器版本 */
    public String getVersion() {
        try {
            InputStream verIns = ResourcesUtils.getResourceAsStream("/META-INF/rsf-center.version");
            List<String> dataLines = IOUtils.readLines(verIns, "UTF-8");
            return !dataLines.isEmpty() ? dataLines.get(0) : null;
        } catch (Throwable e) {
            return null;
        }
    }
    /** 时间戳 */
    private String nowData() {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
    }
    //
    //
    //
    public void shutdownDiplomat() throws Throwable {
        ZooKeeperNode zkNode = appContext.getInstance(ZooKeeperNode.class);
        logger.info("ZooKeeper data -> remove server info.");
        //
        zkNode.deleteNode(this.serverInfoPath);
    }
    //
    private void initCenterInfo() throws Throwable {
        logger.info("init rsf-center to zooKeeper.");
        this.initDiplomatEnv();
        //
        // -必要的目录
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.ROOT_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.SERVER_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.LEADER_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.SERVICES_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.CONFIG_PATH);
        //
        // Server信息
        zkNode.createNode(ZkNodeType.Persistent, this.serverInfoPath);
        zkNode.saveOrUpdate(ZkNodeType.Persistent, this.serverInfoPath + "/info", this.serverInfo());
        zkNode.saveOrUpdate(ZkNodeType.Persistent, this.serverInfoPath + "/version", this.getVersion());
        zkNode.saveOrUpdate(ZkNodeType.Persistent, this.serverInfoPath + "/auth", this.getVersion());
        zkNode.saveOrUpdate(ZkNodeType.Session, this.serverInfoPath + "/heartbeat", this.nowData());
        //
    }
    @Override
    public void onEvent(String event, Object[] params) throws Throwable {
        /*   */if (StringUtils.equals(event, RSFCenterCluster_StartEvent) == true) {
            this.initCenterInfo();
            //
        } else if (StringUtils.equals(event, RSFCenterCluster_StopEvent) == true) {
            this.shutdownDiplomat();
            //
        }
    }
}