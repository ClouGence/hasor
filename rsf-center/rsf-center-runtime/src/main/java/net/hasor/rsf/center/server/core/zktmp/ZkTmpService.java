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
package net.hasor.rsf.center.server.core.zktmp;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.more.util.CommonCodeUtils.MD5;
import freemarker.template.Configuration;
import freemarker.template.Template;
import net.hasor.core.AppContext;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.rsf.center.domain.ConsumerPublishInfo;
import net.hasor.rsf.center.domain.ProviderPublishInfo;
import net.hasor.rsf.center.domain.PublishInfo;
import net.hasor.rsf.center.server.domain.RsfCenterCfg;
/**
 * 负责写入ZK集群的数据生成。
 * @version : 2016年2月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZkTmpService {
    @Inject
    private AppContext    appContext;
    @Inject
    private RsfCenterCfg  rsfCenterCfg;
    private Configuration configuration;
    @Init
    public void init() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setTemplateLoader(new ClassPathTemplateLoader());
        configuration.setDefaultEncoding("utf-8");// 默认页面编码UTF-8
        configuration.setOutputEncoding("utf-8");// 输出编码格式UTF-8
        configuration.setLocalizedLookup(false);// 是否开启国际化false
        configuration.setNumberFormat("0");
        configuration.setClassicCompatible(true);// null值测处理配置
        this.configuration = configuration;
    }
    //
    /** 生成RSF-Center服务器信息 */
    public String serverInfo() throws Throwable {
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("cfg", this.rsfCenterCfg);
        String fmt = "/META-INF/zookeeper/server-info.tmp";
        Template template = this.configuration.getTemplate(fmt, "UTF-8");
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return writer.toString();
    }
    //
    public String publishInfoHashCode(PublishInfo info) throws Throwable {
        return MD5.getMD5(info.getBindID());
    }
    //
    /** 生成服务信息 */
    public String serviceInfo(PublishInfo info) throws Throwable {
        String hashCode = this.publishInfoHashCode(info);
        //
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("cfg", this.rsfCenterCfg);
        dataModel.put("info", info);
        dataModel.put("hashCode", hashCode);
        String fmt = "/META-INF/zookeeper/service-info.tmp";
        Template template = this.configuration.getTemplate(fmt, "UTF-8");
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return writer.toString();
    }
    //
    /** 生成提供者信息 */
    public String providerInfo(ProviderPublishInfo info) throws Throwable {
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("cfg", this.rsfCenterCfg);
        dataModel.put("info", info);
        String fmt = "/META-INF/zookeeper/provider-info.tmp";
        Template template = this.configuration.getTemplate(fmt, "UTF-8");
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return writer.toString();
    }
    /** 生成消费者信息 */
    public String consumerInfo(ConsumerPublishInfo info) throws Throwable {
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("cfg", this.rsfCenterCfg);
        dataModel.put("info", info);
        String fmt = "/META-INF/zookeeper/consumer-info.tmp";
        Template template = this.configuration.getTemplate(fmt, "UTF-8");
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return writer.toString();
    }
}