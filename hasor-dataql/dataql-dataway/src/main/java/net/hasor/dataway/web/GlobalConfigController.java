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
package net.hasor.dataway.web;
import net.hasor.core.AppContext;
import net.hasor.core.XmlNode;
import net.hasor.dataway.config.GlobalConfig;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.web.annotation.Get;
import net.hasor.web.objects.JsonRenderEngine;
import net.hasor.web.render.RenderType;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Map;

/**
 * 全局配置（不经过权限）
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/global-config")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class GlobalConfigController extends BasicController {
    @Inject
    private AppContext   appContext;
    private GlobalConfig globalConfig;

    @PostConstruct
    public void initController() {
        this.globalConfig = this.appContext.getInstance(GlobalConfig.class);
        XmlNode xmlNode = this.appContext.getEnvironment().getSettings().getXmlNode("hasor.dataway.globalConfig");
        if (xmlNode != null) {
            Map<String, String> globalConfigMap = xmlNode.toSettingMap();
            globalConfigMap.forEach((key, val) -> {
                if (!globalConfig.containsKey(key)) {
                    globalConfig.put(key, val);
                }
            });
        }
    }

    @Get
    public Result<Map<String, String>> globalConfig() {
        return Result.of(globalConfig);
    }
}