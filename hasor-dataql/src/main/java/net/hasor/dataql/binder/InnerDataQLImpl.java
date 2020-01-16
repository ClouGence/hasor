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
package net.hasor.dataql.binder;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.Finder;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.Query;
import net.hasor.dataql.compiler.QueryModel;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.runtime.HintsSet;
import net.hasor.dataql.runtime.QueryHelper;
import net.hasor.dataql.runtime.VarSupplier;
import org.antlr.v4.runtime.CharStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * UDF 函数定义
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
class InnerDataQLImpl extends HintsSet implements DataQL {
    private Map<String, VarSupplier> compilerVarMap = new HashMap<>();
    //    private Map<String, QIL>         cacheQIL       = new HashMap<>();
    private AppContext               appContext;
    private Finder                   finderObject;

    public void initConfig(AppContext appContext) {
        this.appContext = appContext;
        List<ShareVar> shareVars = appContext.findBindingBean(ShareVar.class);
        shareVars.forEach(shareVar -> compilerVarMap.put(shareVar.getName(), shareVar));
        if (this.finderObject == null) {
            this.finderObject = new AppContextFinder(appContext);
        }
    }

    public void setFinder(Finder finder) {
        this.finderObject = finder;
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
    public <T extends FragmentProcess> DataQL addFragmentProcess(String name, BindInfo<T> bindInfo) {
        if (this.finderObject instanceof AppContextFinder) {
            return addFragmentProcess(name, this.appContext.getProvider(bindInfo));
        }
        throw new UnsupportedOperationException("custom Finder Unsupported. ");
    }

    @Override
    public <T extends FragmentProcess> DataQL addFragmentProcess(String name, Class<T> implementation) {
        if (this.finderObject instanceof AppContextFinder) {
            return addFragmentProcess(name, this.appContext.getProvider(implementation));
        }
        throw new UnsupportedOperationException("custom Finder Unsupported. ");
    }

    @Override
    public <T extends FragmentProcess> DataQL addFragmentProcess(String name, Supplier<T> provider) {
        if (this.finderObject instanceof AppContextFinder) {
            ((AppContextFinder) this.finderObject).addFragmentProcess(name, provider);
            return this;
        }
        throw new UnsupportedOperationException("custom Finder Unsupported. ");
    }

    @Override
    public Finder getFinder() {
        return this.finderObject;
    }

    @Override
    public QueryModel parserQuery(CharStream charStream) {
        return QueryHelper.queryParser(charStream);
    }

    @Override
    public QIL compilerQuery(QueryModel queryModel) throws IOException {
        return QueryHelper.queryCompiler(queryModel, this.compilerVarMap.keySet(), getFinder());
    }

    @Override
    public Query createQuery(QIL compilerQIL) {
        //        QIL compilerQIL = this.cacheQIL.get(hashString);
        //        if (compilerQIL == null) {
        //            QueryModel queryModel = QueryHelper.queryParser(queryString);
        //            compilerQIL = QueryHelper.queryCompiler(queryModel, this.compilerVarMap.keySet(), this.finderObject);
        //            this.cacheQIL.put(hashString, compilerQIL);
        //        }
        Query query = QueryHelper.createQuery(compilerQIL, this.finderObject);
        this.compilerVarMap.forEach(query::setCompilerVar);
        query.setHints(this);
        return query;
    }
}