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
package net.demo.hasor.core;
import com.qq.connect.utils.QQConnectConfig;
import net.demo.hasor.manager.oauth.AbstractOAuthConfig;
import net.demo.hasor.manager.oauth.TencentOAuthConfig;
import net.hasor.core.Settings;
import net.hasor.restful.RenderEngine;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
/**
 *
 * @version : 2015年12月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class StartModule extends WebModule {
    @Override
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        //
        apiBinder.filter("/*").through(0, new EncodingFilter());
        apiBinder.filter("/*").through(0, new JumpFilter());
        //
        apiBinder.installModule(new DataSourceModule());
        apiBinder.bindType(RenderEngine.class).uniqueName().toInstance(new FreemarkerRender());
        //
        //
        //
        apiBinder.bindType(AbstractOAuthConfig.class, TencentOAuthConfig.class);
        Settings settings = apiBinder.getEnvironment().getSettings();
        String hostName = settings.getString("appExample.hostName", "127.0.0.1");
        String tencentAppID = settings.getString("tencent.app_id", "");
        String tencentAppKey = settings.getString("tencent.app_key", "");
        String tencentRedirectURI = hostName + "/login_callback.do";
        QQConnectConfig.updateProperties("app_ID", tencentAppID);
        QQConnectConfig.updateProperties("app_KEY", tencentAppKey);
        QQConnectConfig.updateProperties("redirect_URI", tencentRedirectURI);
    }
}
