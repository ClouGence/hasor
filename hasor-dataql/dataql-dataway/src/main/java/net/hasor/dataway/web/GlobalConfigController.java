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
import net.hasor.core.BindInfo;
import net.hasor.core.XmlNode;
import net.hasor.core.spi.BindInfoAware;
import net.hasor.dataway.DatawayService;
import net.hasor.dataway.config.GlobalConfig;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.dataway.config.UiConfig;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.Get;
import net.hasor.web.objects.JsonRenderEngine;
import net.hasor.web.render.RenderType;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * 全局配置（不经过权限）
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/global-config")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class GlobalConfigController extends BasicController implements UiConfig, BindInfoAware {
    private static final String       DATAWAY_VERSION;
    @Inject
    private              AppContext   appContext;
    private              String       apiBaseUri;
    private              String       uiBaseUri;
    private              GlobalConfig globalConfig;

    static {
        String version = null;
        try {
            InputStream inputStream = ResourcesUtils.getResourceAsStream("/META-INF/maven/net.hasor/hasor-dataway/pom.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            version = properties.getProperty("version");
        } catch (Exception e) {
            version = DatawayService.VERSION;
        }
        DATAWAY_VERSION = version;
    }

    private static String allLocalMac() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        Set<String> macPool = new HashSet<>();
        while (interfaces.hasMoreElements()) {
            NetworkInterface nextElement = interfaces.nextElement();
            byte[] hardwareAddress = nextElement.getHardwareAddress();
            if (hardwareAddress == null) {
                continue;
            }
            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < hardwareAddress.length; i++) {
                String str = Integer.toHexString(hardwareAddress[i] & 0xff);
                strBuilder.append((str.length() == 1) ? ("0" + str) : str);
            }
            macPool.add(strBuilder.toString());
        }
        return StringUtils.join(macPool.toArray(), ",").toUpperCase();
    }

    @Override
    public void setBindInfo(BindInfo<?> bindInfo) {
        this.uiBaseUri = (String) bindInfo.getMetaData(KEY_DATAWAY_UI_BASE_URI);
        this.apiBaseUri = (String) bindInfo.getMetaData(KEY_DATAWAY_API_BASE_URI);
    }

    @PostConstruct
    public void initController() throws SocketException {
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
        //
        this.globalConfig.put("API_BASE_URL", this.apiBaseUri);
        this.globalConfig.put("ALL_MAC", allLocalMac());
        this.globalConfig.put("DATAWAY_VERSION", DATAWAY_VERSION);
    }

    @Get
    public Result<Map<String, String>> globalConfig(Invoker invoker) {
        String contextPath = invoker.getHttpRequest().getContextPath();
        String contextPathProxy = invoker.getHttpRequest().getHeader("DW_CONTEXT_PATH_PROXY");
        if (StringUtils.isBlank(contextPathProxy)) {
            if (StringUtils.isBlank(contextPath)) {
                contextPath = "/";
            }
            if (contextPath.endsWith("/")) {
                contextPath = contextPath.substring(0, contextPath.length() - 1);
            }
        } else {
            contextPath = contextPathProxy;
        }
        this.globalConfig.put("CONTEXT_PATH", contextPath);
        return Result.of(this.globalConfig);
    }
}