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
package net.hasor.dataway.service;
import net.hasor.core.Inject;
import net.hasor.core.InjectSettings;
import net.hasor.core.Singleton;
import net.hasor.dataway.daos.impl.ApiDataAccessLayer;
import net.hasor.dataway.daos.impl.EntityDef;
import net.hasor.dataway.daos.impl.FieldDef;
import net.hasor.utils.StringUtils;

import java.util.Map;

/**
 * 检测服务。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
@Singleton
public class CheckService {
    @InjectSettings("${HASOR_DATAQL_DATAWAY_API_URL}")
    private String             apiUrl;
    @Inject
    private ApiDataAccessLayer dataAccessLayer;

    public void checkApi(String apiPath) {
        if (StringUtils.isBlank(this.apiUrl)) {
            throw new IllegalArgumentException("The API path is empty.");
        }
        if (!apiPath.startsWith(this.apiUrl)) {
            throw new IllegalArgumentException("The API prefix must be " + this.apiUrl);
        }
        if (!apiPath.matches("[\\$\\(\\)\\*\\+\\-\\.!',/:;=@_~0-9a-zA-Z]+")) {
            throw new IllegalArgumentException("Allowed characters： !  $  '  (  )  *  +  ,  -  .  /  :  ;  =  @  _  ~  0-9  a-z  A-Z");
        }
        //
        Map<FieldDef, String> object = this.dataAccessLayer.getObjectBy(EntityDef.INFO, FieldDef.PATH, apiPath);
        if (object != null) {
            throw new IllegalArgumentException("this API path has been used.");
        }
    }
}