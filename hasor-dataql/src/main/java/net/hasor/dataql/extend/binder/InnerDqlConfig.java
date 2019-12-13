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
package net.hasor.dataql.extend.binder;
import net.hasor.core.AppContext;
import net.hasor.dataql.Finder;
import net.hasor.dataql.Query;
import net.hasor.dataql.compiler.QueryModel;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.runtime.HintsSet;
import net.hasor.dataql.runtime.QueryHelper;
import net.hasor.dataql.runtime.VarSupplier;
import net.hasor.utils.StringUtils;
import net.hasor.utils.resource.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.hasor.utils.CommonCodeUtils.MD5;

/**
 * UDF 函数定义
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
class InnerDqlConfig extends HintsSet implements DataQL {
    private Map<String, VarSupplier> compilerVarMap = new HashMap<>();
    private Map<String, QIL>         cacheQIL       = new HashMap<>();
    private ResourceLoader           resourceLoader;
    private Finder                   finderObject;

    public void initConfig(AppContext appContext) {
        List<ShareVar> shareVars = appContext.findBindingBean(ShareVar.class);
        shareVars.forEach(shareVar -> compilerVarMap.put(shareVar.getName(), shareVar));
        this.finderObject = new AppContextFinder(appContext) {
            public InputStream findResource(String resourceName) throws IOException {
                if (resourceLoader != null) {
                    return resourceLoader.getResourceAsStream(resourceName);
                } else {
                    return super.findResource(resourceName);
                }
            }
        };
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public <T> DataQL addShareVar(String name, Class<? extends T> implementation) {
        this.compilerVarMap.put(name, () -> finderObject.findBean(implementation));
        return this;
    }

    @Override
    public <T> DataQL addShareVar(String name, Supplier<T> provider) {
        this.compilerVarMap.put(name, provider::get);
        return this;
    }

    @Override
    public Query createQuery(String queryString) throws IOException {
        if (StringUtils.isBlank(queryString)) {
            return null;
        }
        String hashString = queryString;
        try {
            hashString = MD5.getMD5(queryString);
        } catch (Exception e) { /**/ }
        //
        QIL compilerQIL = this.cacheQIL.get(hashString);
        if (compilerQIL == null) {
            QueryModel queryModel = QueryHelper.queryParser(queryString);
            compilerQIL = QueryHelper.queryCompiler(queryModel, compilerVarMap.keySet(), this.finderObject);
            this.cacheQIL.put(hashString, compilerQIL);
        }
        //
        Query query = QueryHelper.createQuery(compilerQIL, this.finderObject);
        this.compilerVarMap.forEach(query::setCompilerVar);
        query.setHints(this);
        return query;
    }
}