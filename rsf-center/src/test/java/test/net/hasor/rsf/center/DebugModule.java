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
package test.net.hasor.rsf.center;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
/**
 * @version : 2015年8月13日
 * @author 赵永春(zyc@hasor.net)
 */
public class DebugModule extends WebModule implements EventListener {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //
    }
    @Override
    public void onEvent(String event, Object[] params) throws Throwable {
        this.onStart((AppContext) params[0]);
    }
    public void onStart(AppContext appContext) throws Throwable {
        logger.info("################### Dev ###################");
        // String workAt = StartAppModule.workAt();
        // if (StringUtils.equalsBlankIgnoreCase(workAt, WorkMode.Memory.getCodeString())) {
        JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
        jdbcTemplate.loadSQL("UTF-8", "init_sql.sql");
        // }
        //
    }
}