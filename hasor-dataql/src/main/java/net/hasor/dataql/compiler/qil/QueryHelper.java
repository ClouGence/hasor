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
package net.hasor.dataql.compiler.qil;
import net.hasor.dataql.compiler.QueryModel;
import net.hasor.dataql.compiler.ast.inst.RootBlockSet;
import net.hasor.dataql.compiler.parser.DataQLParser;
import net.hasor.dataql.compiler.parser.DataQLVisitor;
import net.hasor.dataql.compiler.parser.DefaultDataQLVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

/**
 * DataQL 小工具。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-03
 */
public class QueryHelper {
    public static QueryModel queryParser(String queryString) throws IOException {
        return queryParser(new StringReader(queryString));
    }

    public static QueryModel queryParser(Reader queryReader) throws IOException {
        return queryParser(CharStreams.fromReader(queryReader));
    }

    public static QueryModel queryParser(InputStream queryInput) throws IOException {
        return queryParser(CharStreams.fromStream(queryInput));
    }

    public static QIL queryCompiler(String queryString) throws IOException {
        return queryCompiler(queryParser(queryString));
    }

    public static QIL queryCompiler(Reader queryReader) throws IOException {
        return queryCompiler(queryParser(queryReader));
    }

    public static QIL queryCompiler(InputStream queryInput) throws IOException {
        return queryCompiler(queryParser(queryInput));
    }

    private static RootBlockSet queryParser(CharStream charStream) {
        DataQLLexer lexer = new DataQLLexer(charStream);
        DataQLParser qlParser = new DataQLParser(new CommonTokenStream(lexer));
        DataQLVisitor visitor = new DefaultDataQLVisitor();
        return (RootBlockSet) visitor.visit(qlParser.rootInstSet());
    }

    public static QIL queryCompiler(QueryModel queryModel) throws IOException {
        RootBlockSet rootBlockSet = null;
        if (queryModel instanceof RootBlockSet) {
            rootBlockSet = (RootBlockSet) queryModel;
        } else {
            rootBlockSet = queryParser(CharStreams.fromString(queryModel.buildToString()));
        }
        //
        InstQueue queue = new InstQueue();
        rootBlockSet.doCompiler(queue, new CompilerStack());
        Instruction[][] queueSet = queue.buildArrays();
        return new QIL(queueSet);
    }
}