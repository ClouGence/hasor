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
package net.hasor.dataway.spi;
import net.hasor.dataql.Finder;
import net.hasor.dataql.compiler.QueryModel;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.runtime.QueryHelper;

import java.io.IOException;
import java.util.EventListener;
import java.util.Set;

/**
 * DataQL 编译，默认实现了编译。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-19
 */
public interface CompilerSpiListener extends EventListener {
    public static final CompilerSpiListener DEFAULT = new CompilerSpiListener() {
    };

    public default QIL compiler(ApiInfo apiInfo, String query, Set<String> varNames, Finder finder) throws IOException {
        QueryModel queryModel = QueryHelper.queryParser(query);
        return QueryHelper.queryCompiler(queryModel, varNames, finder);
    }
}