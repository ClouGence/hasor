/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.hasor.plugins.template;
import javax.sql.DataSource;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.plugin.AbstractHasorPlugin;
import net.hasor.core.plugin.Plugin;
import net.hasor.jdbc.core.JdbcTemplate;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-12-10
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
@Plugin
public class TemplatePlugin extends AbstractHasorPlugin implements AppContextAware {
    private AppContext appContext;
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    public void loadPlugin(ApiBinder apiBinder) {
        apiBinder.registerAware(this);
        /*JdbcTemplate*/
        apiBinder.getGuiceBinder().bind(JdbcTemplate.class).toProvider(new Provider<JdbcTemplate>() {
            public JdbcTemplate get() {
                DataSource dataSource = appContext.getInstance(DataSource.class);
                return new JdbcTemplate(dataSource);
            }
        });
    }
}