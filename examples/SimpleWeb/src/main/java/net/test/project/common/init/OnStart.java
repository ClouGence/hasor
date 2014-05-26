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
package net.test.project.common.init;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.jdbc.template.core.JdbcTemplate;
import net.hasor.plugins.event.Listener;
import com.google.inject.Inject;
/**
 * ≥ı ºªØ ˝æ›ø‚
 * @version : 2013-12-23
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
@Listener(AppContext.ContextEvent_Started)
public class OnStart implements EventListener {
    @Inject
    private JdbcTemplate jdbcTemplate;
    @Inject
    Settings             settings;
    //
    public void onEvent(String event, Object[] params) throws Throwable {
        //
        jdbcTemplate.loadSQL("net/test/project/common/init/TB_User.sql");
        jdbcTemplate.loadSQL("net/test/project/common/init/TB_User_Data.sql");
        //
        XmlNode node1 = settings.getXmlNode(".");
        XmlNode node2 = settings.getXmlNode("config");
        XmlNode node3 = settings.getXmlNode("hasor");
        System.out.println(String.format("node1 : %s", node1));
        System.out.println(String.format("node2 : %s", node2));
        System.out.println(String.format("node3 : %s", node3));
    }
}