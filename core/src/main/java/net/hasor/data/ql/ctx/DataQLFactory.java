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
package net.hasor.data.ql.ctx;
import net.hasor.data.ql.DataQL;
import net.hasor.data.ql.Query;
import net.hasor.data.ql.QueryUDF;
import net.hasor.data.ql.UDF;
import net.hasor.data.ql.dsl.QueryModel;
import net.hasor.data.ql.dsl.parser.DataQLParser;
import net.hasor.data.ql.dsl.parser.ParseException;

import java.util.HashMap;
import java.util.Map;
/**
 * DataQL 上下文。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public class DataQLFactory implements DataQL, QueryUDF {
    private Map<String, UDF> udfMap;
    protected DataQLFactory() {
        this.udfMap = new HashMap<String, UDF>();
    }
    /** 新实例 */
    public static final DataQLFactory newInstance() {
        return new DataQLFactory();
    }
    /** 添加 UDF */
    public void addUDF(String udfName, UDF udf) {
        if (this.udfMap.containsKey(udfName)) {
            throw new IllegalStateException("udf name ‘" + udfName + "’ already exist.");
        }
        this.udfMap.put(udfName, udf);
    }
    //
    @Override
    public boolean containsUDF(String udfName) {
        return this.udfMap.containsKey(udfName);
    }
    @Override
    public UDF findUDF(String udfName) {
        return this.udfMap.get(udfName);
    }
    //
    @Override
    public Query createQuery(String qlString) throws ParseException {
        return this.createQuery(qlString, null);
    }
    @Override
    public Query createQuery(String qlString, QueryUDF temporaryUDF) throws ParseException {
        if (temporaryUDF == null) {
            temporaryUDF = EmptyQueryUDF.Instance;
        }
        //
        QueryModel queryModel = DataQLParser.parserQL(qlString);
        return new QueryImpl(this, queryModel, temporaryUDF);
    }
}