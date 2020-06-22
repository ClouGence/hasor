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
import net.hasor.dataql.runtime.CompilerArguments;
import net.hasor.dataql.runtime.CompilerVarQuery;
import net.hasor.dataql.runtime.HintsSet;
import net.hasor.dataql.runtime.QueryHelper;
import net.hasor.utils.BeanUtils;
import org.antlr.v4.runtime.CharStream;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * UDF 函数定义
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
class InnerDataQLImpl extends HintsSet implements DataQL {
    private final Map<String, Supplier<?>>   compilerVarMap       = new HashMap<>();
    private       AppContext                 appContext;
    private       Supplier<? extends Finder> finderObject;
    private final CompilerArguments          useCompilerArguments = CompilerArguments.DEFAULT;

    public void initConfig(AppContext appContext) {
        this.appContext = appContext;
        if (this.finderObject == null) {
            AppContextFinder contextFinder = new AppContextFinder(appContext);
            this.finderObject = () -> contextFinder;
        }
    }

    public void setFinder(Supplier<? extends Finder> finder) {
        this.finderObject = finder;
    }

    @Override
    public void configOption(ConfigOption optionKey, Object value) {
        BeanUtils.writePropertyOrField(this.useCompilerArguments, optionKey.getConfigName(), value);
    }

    @Override
    public <T> DataQL addShareVar(String name, Class<? extends T> implementation) {
        this.compilerVarMap.put(name, () -> finderObject.get().findBean(implementation));
        return this;
    }

    @Override
    public <T> DataQL addShareVar(String name, Supplier<T> provider) {
        this.compilerVarMap.put(name, provider);
        return this;
    }

    @Override
    public Map<String, Supplier<?>> getShareVarMap() {
        return Collections.unmodifiableMap(this.compilerVarMap);
    }

    @Override
    public <T extends FragmentProcess> DataQL addFragmentProcess(String name, BindInfo<T> bindInfo) {
        if (this.finderObject.get() instanceof AppContextFinder) {
            return addFragmentProcess(name, this.appContext.getProvider(bindInfo));
        }
        throw new UnsupportedOperationException("custom Finder Unsupported. ");
    }

    @Override
    public <T extends FragmentProcess> DataQL addFragmentProcess(String name, Class<T> implementation) {
        if (this.finderObject.get() instanceof AppContextFinder) {
            return addFragmentProcess(name, this.appContext.getProvider(implementation));
        }
        throw new UnsupportedOperationException("custom Finder Unsupported. ");
    }

    @Override
    public <T extends FragmentProcess> DataQL addFragmentProcess(String name, Supplier<T> provider) {
        if (this.finderObject.get() instanceof AppContextFinder) {
            ((AppContextFinder) this.finderObject.get()).addFragmentProcess(name, provider);
            return this;
        }
        throw new UnsupportedOperationException("custom Finder Unsupported. ");
    }

    @Override
    public Finder getFinder() {
        return this.finderObject.get();
    }

    @Override
    public QueryModel parserQuery(CharStream charStream) {
        return QueryHelper.queryParser(charStream);
    }

    @Override
    public QIL compilerQuery(QueryModel queryModel) throws IOException {
        CompilerArguments compilerArguments = this.useCompilerArguments.copyAsNew();
        compilerArguments.getCompilerVar().addAll(this.compilerVarMap.keySet());
        return QueryHelper.queryCompiler(queryModel, compilerArguments, getFinder());
    }

    @Override
    public Query createQuery(QIL compilerQIL) {
        Query query = QueryHelper.createQuery(compilerQIL, this.finderObject.get());
        if (query instanceof CompilerVarQuery) {
            CompilerVarQuery varQuery = (CompilerVarQuery) query;
            this.compilerVarMap.forEach(varQuery::setCompilerVar);
        }
        query.setHints(this);
        return query;
    }
}