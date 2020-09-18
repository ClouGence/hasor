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
package net.hasor.dataql.fx;
import net.hasor.core.Environment;
import net.hasor.core.XmlNode;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.QueryApiBinder;
import net.hasor.dataql.QueryModule;
import net.hasor.dataql.fx.web.FxWebInterceptor;
import net.hasor.utils.ResourcesUtils;
import net.hasor.web.WebApiBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fx 函数包的自动配置
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-29
 */
public class FxModule implements QueryModule {
    protected static Logger logger = LoggerFactory.getLogger(FxModule.class);

    @Override
    public void loadModule(QueryApiBinder apiBinder) throws Throwable {
        Environment environment = apiBinder.getEnvironment();
        // .注册外部执行器
        XmlNode[] nodeArray = environment.getSettings().getXmlNodeArray("hasor.dataqlFx.bindFragmentSet.bindFragment");
        if (nodeArray != null) {
            for (XmlNode xmlNode : nodeArray) {
                if (!"bindFragment".equalsIgnoreCase(xmlNode.getName())) {
                    continue;
                }
                String fragmentName = xmlNode.getAttribute("name");
                String fragmentType = xmlNode.getText();
                Class<?> loadClass = environment.getClassLoader().loadClass(fragmentType);
                logger.info("bindFragment '" + fragmentName + "' to " + loadClass.getName());
                apiBinder.bindFragment(fragmentName, (Class<? extends FragmentProcess>) loadClass);
            }
        }
        //
        // .如果是 Web 环境那么初始化 web 相关的 函数包。
        if (ResourcesUtils.getResource("/net/hasor/web/WebApiBinder.class") != null) {
            WebApiBinder webApiBinder = apiBinder.tryCast(WebApiBinder.class);
            if (webApiBinder != null) {
                webApiBinder.filter("/*").through(Integer.MAX_VALUE - 1, FxWebInterceptor.class);
            }
        }
    }
}