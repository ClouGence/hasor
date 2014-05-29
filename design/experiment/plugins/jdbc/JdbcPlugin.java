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
package net.hasor.plugins.jdbc;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.EventListener;
import net.hasor.core.Provider;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.core.plugin.AbstractHasorPlugin;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.quick.plugin.Plugin;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
@Plugin
public class JdbcPlugin extends AbstractHasorPlugin {
    public void loadPlugin(ApiBinder apiBinder) {
        /*JdbcTemplate*/
        apiBinder.bindingType(JdbcTemplate.class).toProvider(new DefaultJdbcTemplateProvider(apiBinder));
        /*带有名称的 JdbcTemplate*/
        Settings settings = apiBinder.getEnvironment().getSettings();
        XmlNode[] dataSourceSet = settings.getXmlNodeArray("hasor-jdbc.dataSourceSet");
        if (dataSourceSet == null)
            return;
        ArrayList<String> dataSourceNames = new ArrayList<String>();
        for (XmlNode dsSet : dataSourceSet) {
            List<XmlNode> dataSource = dsSet.getChildren("dataSource");
            for (XmlNode dsConfig : dataSource) {
                String name = dsConfig.getAttribute("name");
                if (!StringUtils.isBlank(name))
                    dataSourceNames.add(name);
            }
        }
        apiBinder.pushListener(AppContext.ContextEvent_Initialized, new InitializedEventListener(dataSourceNames));
    }
    /**/
    private static class InitializedEventListener implements EventListener {
        private List<String> dataSourceNames;
        public InitializedEventListener(List<String> dataSourceNames) {
            this.dataSourceNames = dataSourceNames;
        }
        public void onEvent(String event, Object[] params) throws Throwable {
            if (dataSourceNames == null || dataSourceNames.isEmpty())
                return;
            ApiBinder apiBinder = (ApiBinder) params[0];
            for (String name : dataSourceNames) {
                JdbcTemplateProvider jdbcProvider = new JdbcTemplateProvider(name, apiBinder);
                apiBinder.bindingType(name, JdbcTemplate.class).toProvider(jdbcProvider);
            }
        }
    }
    /**/
    private static class DefaultJdbcTemplateProvider implements Provider<JdbcTemplate>, AppContextAware {
        private AppContext appContext;
        public void setAppContext(AppContext appContext) {
            this.appContext = appContext;
        }
        public DefaultJdbcTemplateProvider(ApiBinder apiBinder) {
            apiBinder.registerAware(this);
        }
        public JdbcTemplate get() {
            DataSource dataSource = appContext.getInstance(DataSource.class);
            return new JdbcTemplate(dataSource);
        }
    }
    /**/
    private static class JdbcTemplateProvider implements Provider<JdbcTemplate>, AppContextAware {
        private String     name;
        private AppContext appContext;
        public void setAppContext(AppContext appContext) {
            this.appContext = appContext;
        }
        public JdbcTemplateProvider(String name, ApiBinder apiBinder) {
            this.name = name;
            apiBinder.registerAware(this);
        }
        public JdbcTemplate get() {
            DataSource dataSource = appContext.findBindingBean(name, DataSource.class);
            if (dataSource == null)
                throw new NullPointerException(name + " DataSource is not define.");
            return new JdbcTemplate(dataSource);
        }
    }
}