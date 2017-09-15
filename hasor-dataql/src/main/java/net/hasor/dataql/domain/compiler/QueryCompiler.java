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
package net.hasor.dataql.domain.compiler;
import net.hasor.dataql.domain.BlockSet;
import net.hasor.dataql.domain.parser.DataQLParser;
import net.hasor.dataql.domain.parser.ParseException;
/**
 * DataQL 编译器。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-03
 */
public class QueryCompiler {
    //
    public static QIL compilerQuery(String queryString) throws ParseException {
        //
        BlockSet queryModel = DataQLParser.parserDataQL(queryString);
        InstQueue queue = new InstQueue();
        queryModel.doCompiler(queue, new CompilerStack());
        Instruction[][] queueSet = queue.buildArrays();
        //
        return new QIL(queueSet);
    }
}