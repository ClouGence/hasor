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
import net.hasor.dataql.fx.basic.StringUdfSource;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.dal.EntityDef;
import net.hasor.dataway.dal.FieldDef;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.Get;
import net.hasor.web.objects.JsonRenderEngine;
import net.hasor.web.render.RenderType;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Swagger 导出 "http://127.0.0.1:8080/interface-ui/api/docs/swagger2.json"
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/docs/swagger2.json")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class Swagger2Controller extends BasicController {
    @Get
    public Object doSwaggerApi(Invoker invoker) throws IOException {
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        String localName = httpRequest.getHeader("Host");
        if (StringUtils.isBlank(localName)) {
            int localPort = httpRequest.getLocalPort();
            localName = httpRequest.getLocalAddr() + ((localPort == 80) ? "" : (":" + localPort));
        }
        //
        List<Map<FieldDef, String>> doList = this.dataAccessLayer.listObjectBy(EntityDef.RELEASE, emptyCondition());
        List<Map<String, String>> collectList = doList.stream().map(defMap -> {
            Map<String, String> dataMap = new HashMap<>();
            defMap.forEach((fieldDef, s) -> {
                dataMap.put(StringUdfSource.lineToHump(fieldDef.name()), s);
            });
            return dataMap;
        }).collect(Collectors.toList());
        //
        String serverHost = localName;
        String serverBasePath = invoker.getHttpRequest().getContextPath();
        return new Swagger2Query().execute(new HashMap<String, Object>() {{
            put("apiDataList", collectList);
            put("serverHost", serverHost);
            put("serverBasePath", StringUtils.isNotBlank(serverBasePath) ? serverBasePath : "/");
        }}).getData().unwrap();
    }
}