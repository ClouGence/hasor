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
import net.hasor.dataql.runtime.HintsSet;
import net.hasor.dataql.runtime.QueryHelper;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.ref.LinkedCaseInsensitiveMap;
import org.antlr.v4.runtime.CharStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * UDF 函数定义
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
class InnerDataQLImpl extends HintsSet implements DataQL, Finder {
    private final Map<String, Supplier<?>> compilerVarMap   = new HashMap<>();
    private final Map<String, Supplier<?>> fragmentMap      = new LinkedCaseInsensitiveMap<>();
    private final Map<String, Supplier<?>> importPrepareMap = new ConcurrentHashMap<>();
    private       AppContext               appContext;
    private       Finder                   parentFinder;
    private final CompilerArguments        useArguments     = CompilerArguments.DEFAULT.copyAsNew();

    public void initConfig(AppContext appContext) {
        this.appContext = appContext;
        if (this.parentFinder == null) {
            this.parentFinder = Finder.APP_CONTEXT.apply(appContext);
        }
        //
        List<BindInfo<FragmentProcess>> bindInfos = appContext.findBindingRegister(FragmentProcess.class);
        if (bindInfos != null) {
            bindInfos.forEach(fragmentInfo -> {
                Supplier<? extends FragmentProcess> fragmentProcess = appContext.getProvider(fragmentInfo);
                this.fragmentMap.put(fragmentInfo.getBindName().toLowerCase(), fragmentProcess);
                this.importPrepareMap.put(fragmentInfo.getBindType().getName(), fragmentProcess);
            });
        }
    }

    public void setFinder(Finder parentFinder) {
        this.parentFinder = parentFinder;
    }

    /* -------------- Finder -------------- */

    /** 负责处理 <code>import @"/net/hasor/demo.ql" as demo;</code>方式中 ‘/net/hasor/demo.ql’ 资源的加载 */
    @Override
    public InputStream findResource(String resourceName) throws IOException {
        InputStream inputStream = null;
        ClassLoader classLoader = this.appContext.getEnvironment().getClassLoader();
        if (classLoader != null) {
            resourceName = ResourcesUtils.formatResource(resourceName);
            inputStream = classLoader.getResourceAsStream(resourceName);
        } else {
            inputStream = ResourcesUtils.getResourceAsStream(resourceName);
        }
        return inputStream;
    }

    @Override
    public Object findBean(Class<?> beanType) {
        String typeName = beanType.getName();
        if (!this.importPrepareMap.containsKey(typeName)) {
            this.importPrepareMap.put(typeName, () -> {
                return this.parentFinder.findBean(beanType);
            });
        }
        return this.importPrepareMap.get(typeName).get();
    }

    @Override
    public FragmentProcess findFragmentProcess(String fragmentType) {
        Supplier<?> supplier = this.fragmentMap.get(fragmentType);
        if (supplier == null) {
            throw new IllegalStateException(fragmentType + " fragment undefine.");
        }
        FragmentProcess process = (FragmentProcess) supplier.get();
        if (process == null) {
            throw new IllegalStateException(fragmentType + " is null.");
        }
        return process;
    }

    /* -------------- DataQL -------------- */
    @Override
    public void configOption(ConfigOption optionKey, Object value) {
        BeanUtils.writePropertyOrField(this.useArguments, optionKey.getConfigName(), value);
    }

    @Override
    public Finder getFinder() {
        return this;
    }

    @Override
    public <T> DataQL addShareVar(String name, Class<? extends T> implementation) {
        this.compilerVarMap.put(name, () -> findBean(implementation));
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
        this.fragmentMap.put(name, this.appContext.getProvider(bindInfo));
        return this;
    }

    @Override
    public <T extends FragmentProcess> DataQL addFragmentProcess(String name, Class<T> implementation) {
        this.fragmentMap.put(name, () -> findBean(implementation));
        return this;
    }

    @Override
    public <T extends FragmentProcess> DataQL addFragmentProcess(String name, Supplier<T> provider) {
        this.fragmentMap.put(name, provider);
        return this;
    }

    @Override
    public QueryModel parserQuery(CharStream charStream) {
        return QueryHelper.queryParser(charStream);
    }

    @Override
    public QIL compilerQuery(QueryModel queryModel) throws IOException {
        CompilerArguments compilerArguments = this.useArguments.copyAsNew();
        compilerArguments.getCompilerVar().addAll(this.compilerVarMap.keySet());
        return QueryHelper.queryCompiler(queryModel, compilerArguments, getFinder());
    }

    @Override
    public Query createQuery(QIL compilerQIL) {
        Query query = QueryHelper.createQuery(compilerQIL, this);
        query.putShareVar(this.compilerVarMap);
        query.setHints(this);
        return query;
    }
}