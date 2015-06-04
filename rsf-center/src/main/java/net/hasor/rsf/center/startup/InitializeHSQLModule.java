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
package net.hasor.rsf.center.startup;
import java.io.IOException;
import java.util.List;
import net.hasor.core.AppContext;
import net.hasor.core.Settings;
import net.hasor.core.StartModule;
import net.hasor.core.XmlNode;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
/**
 * 初始化HSQL数据库
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class InitializeHSQLModule extends WebModule implements StartModule {
    public static final String DataSource_MEM = "mem";
    //
    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        // TODO Auto-generated method stub
    }
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        Settings settings = appContext.getEnvironment().getSettings();
        XmlNode xmlNode = settings.getXmlNode("rsfCenter.memInitialize");
        if (xmlNode == null || xmlNode.getChildren("sqlScript") == null) {
            throw new IOException("read config error,`rsfCenter.memInitialize` node is not exist.");
        }
        List<XmlNode> xmlNodes = xmlNode.getChildren("sqlScript");
        if (xmlNodes != null) {
            logger.info("sqlScript count = {}", xmlNodes.size());
            JdbcTemplate jdbcTemplate = appContext.findBindingBean(DataBaseModule.DataSource_MEM, JdbcTemplate.class);
            for (XmlNode node : xmlNodes) {
                String scriptName = node.getText().trim();
                try {
                    logger.info("sqlScript `{}` do...", scriptName);
                    jdbcTemplate.loadSQL(scriptName);
                    logger.info("sqlScript `{}` finish.", scriptName);
                } catch (Throwable e) {
                    logger.error("sqlScript `{}` run error =>{}.", scriptName, e);
                    throw e;
                }
            }
        }
    }
}