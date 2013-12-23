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
package net.project.test.common.init;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.jdbc.core.JdbcTemplate;
import net.hasor.plugins.event.Listener;
import com.google.inject.Inject;
/**
 * ≥ı ºªØ ˝æ›ø‚
 * @version : 2013-12-23
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
@Listener(AppContext.ContextEvent_Started)
public class LoadDataOnStart implements EventListener {
    @Inject
    private JdbcTemplate jdbcTemplate;
    //
    public void onEvent(String event, Object[] params) throws Throwable {
        //
        jdbcTemplate.loadSQL("net/project/test/init/data/TB_User.sql");
        jdbcTemplate.loadSQL("net/project/test/init/data/TB_User_Data.sql");
    }
}