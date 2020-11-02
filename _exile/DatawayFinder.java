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
import net.hasor.core.AppContext;
import net.hasor.core.ConstructorBy;
import net.hasor.core.Singleton;
import net.hasor.core.provider.SingleProvider;
import net.hasor.dataql.Finder;
import net.hasor.dataway.dal.ApiDataAccessLayer;
import net.hasor.dataway.dal.EntityDef;
import net.hasor.dataway.dal.FieldDef;
import net.hasor.utils.ResourcesUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Dataway 启动入口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
@Singleton
public class DatawayFinder implements Finder {
    private final Supplier<ApiDataAccessLayer> dataAccessLayer;

    @ConstructorBy
    public DatawayFinder(AppContext appContext) {
        this.dataAccessLayer = SingleProvider.of(() -> appContext.getInstance(ApiDataAccessLayer.class));
    }

    /** 负责处理 <code>import @"/net/hasor/demo.ql" as demo;</code>方式中 ‘/net/hasor/demo.ql’ 资源的加载 */
    public InputStream findResource(final String resourceName) throws IOException {
        if (resourceName.toLowerCase().startsWith("classpath:")) {
            String newResourceName = resourceName.substring("classpath:".length());
            return ResourcesUtils.getResourceAsStream(newResourceName);
        } else {
            Map<FieldDef, String> object = this.dataAccessLayer.get().getObjectBy(EntityDef.RELEASE, FieldDef.PATH, resourceName);
            if (object == null) {
                throw new NullPointerException("import compiler failed -> '" + resourceName + "' not found.");
            }
            String scriptBody = object.get(FieldDef.SCRIPT);
            return new ByteArrayInputStream(scriptBody.getBytes());
        }
    }
}