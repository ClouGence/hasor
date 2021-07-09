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
package net.hasor.dataql.service;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.Finder;
import net.hasor.dataql.Query;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.parser.QueryModel;
import net.hasor.dataql.runtime.CompilerArguments;
import net.hasor.dataql.runtime.HintsSet;
import net.hasor.dataql.runtime.QueryHelper;
import net.hasor.utils.BeanUtils;
import org.antlr.v4.runtime.CharStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * UDF 函数定义
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class DataQLContext extends HintsSet implements DataQL {
    private final CompilerArguments        useArguments   = CompilerArguments.DEFAULT.copyAsNew();
    private final Map<String, Supplier<?>> compilerVarMap = new HashMap<>();
    private final Finder                   finder;

    public DataQLContext() {
        this(Finder.DEFAULT);
    }

    public DataQLContext(Finder finder) {
        this.finder = (finder != null) ? finder : Finder.DEFAULT;
    }

    public Finder getFinder() {
        return this.finder;
    }

    public DataQL addShareVar(String name, Class<?> implementation) {
        this.compilerVarMap.put(name, () -> this.finder.findBean(implementation));
        return this;
    }

    public DataQL addShareVar(String name, Supplier<?> provider) {
        this.compilerVarMap.put(name, provider);
        return this;
    }

    @Override
    public void configOption(ConfigOption optionKey, Object value) {
        BeanUtils.writePropertyOrField(this.useArguments, optionKey.getConfigName(), value);
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
        Query query = QueryHelper.createQuery(compilerQIL, this.finder);
        query.putShareVar(this.compilerVarMap);
        query.setHints(this);
        return query;
    }
}
