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
package net.hasor.dataway.dal.nacos;
import net.hasor.core.AppContext;
import net.hasor.dataway.dal.ApiDataAccessLayer;
import net.hasor.dataway.dal.ApiDataAccessLayerCreator;

/**
 * Nacos 存储层访问 ApiDataAccessLayer 接口实现。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-09-20
 */
public class NacosApiDataAccessLayerCreator implements ApiDataAccessLayerCreator {
    @Override
    public ApiDataAccessLayer create(AppContext appContext) {
        return appContext.getInstance(NacosApiDataAccessLayer.class);
    }
}