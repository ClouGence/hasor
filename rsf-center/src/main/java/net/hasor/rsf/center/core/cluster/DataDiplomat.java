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
package net.hasor.rsf.center.core.cluster;
import static net.hasor.rsf.center.domain.constant.CenterEventType.Center_Start_Event;
import static net.hasor.rsf.center.domain.constant.CenterEventType.Center_Stop_Event;
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
@Event({ Center_Start_Event, Center_Stop_Event })
public class DataDiplomat implements EventListener {
    protected Logger      logger = LoggerFactory.getLogger(getClass());
    private Configuration configuration;
    @Inject
    private RsfCenterCfg  rsfCenterCfg;
    @Inject
    private AppContext    appContext;
    //
    //
    //
    private void initFreemarker() {
        if (this.configuration == null) {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
            configuration.setTemplateLoader(new ClassPathTemplateLoader());
            configuration.setDefaultEncoding("utf-8");// 默认页面编码UTF-8
            configuration.setOutputEncoding("utf-8");// 输出编码格式UTF-8
            configuration.setLocalizedLookup(false);// 是否开启国际化false
            configuration.setNumberFormat("0");
            configuration.setClassicCompatible(true);// null值测处理配置
            this.configuration = configuration;
        }
    }
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
            return "undefined";
        }
    }
    /** 时间戳 */
    private String nowData() {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
    }
    /** 获取服务信息，在ZK上的路径 */
    private String getZooKeeperServerPath() {
        return ZooKeeperNode.SERVER_PATH + "/" + this.rsfCenterCfg.getHostAndPort();
    }
    //
    //
    //
    @Override
    public void onEvent(String event, Object[] params) throws Throwable {
        //
        this.initFreemarker();
        /*   */if (StringUtils.equals(event, Center_Start_Event) == true) {
            this.initDiplomat();
        } else if (StringUtils.equals(event, Center_Stop_Event) == true) {
            this.shutdownDiplomat();
        }
    }
    public void initDiplomat() throws Throwable {
        logger.info("init rsf-center to zooKeeper.");
        ZooKeeperNode zkNode = appContext.getInstance(ZooKeeperNode.class);
        //
        // -必要的目录
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.ROOT_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.SERVER_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.LEADER_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.SERVICES_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.CONFIG_PATH);
        //
        // Server信息
        String serverInfoPath = getZooKeeperServerPath();
        zkNode.createNode(ZkNodeType.Persistent, serverInfoPath);
        zkNode.saveOrUpdate(ZkNodeType.Persistent, serverInfoPath + "/info", this.serverInfo());
        zkNode.saveOrUpdate(ZkNodeType.Persistent, serverInfoPath + "/version", this.getVersion());
        zkNode.saveOrUpdate(ZkNodeType.Persistent, serverInfoPath + "/auth", this.getVersion());
        zkNode.saveOrUpdate(ZkNodeType.Session, serverInfoPath + "/heartbeat", this.nowData());
    }
    public void shutdownDiplomat() throws Throwable {
        ZooKeeperNode zkNode = appContext.getInstance(ZooKeeperNode.class);
        logger.info("ZooKeeper data -> remove server info.");
        zkNode.deleteNode(getZooKeeperServerPath());
    }
}