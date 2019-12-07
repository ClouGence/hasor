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
package net.hasor.dataql.runtime;
import net.hasor.dataql.Finder;
import net.hasor.dataql.Query;
import net.hasor.dataql.compiler.QueryModel;
import net.hasor.dataql.compiler.ast.inst.RootBlockSet;
import net.hasor.dataql.compiler.parser.DataQLLexer;
import net.hasor.dataql.compiler.parser.DataQLParser;
import net.hasor.dataql.compiler.parser.DataQLVisitor;
import net.hasor.dataql.compiler.parser.DefaultDataQLVisitor;
import net.hasor.dataql.compiler.qil.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * DataQL 小工具。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-03
 */
public class QueryHelper {
    /** 解析 DataQL 执行脚本 */
    public static QueryModel queryParser(String queryString) throws IOException {
        return queryParser(new StringReader(queryString));
    }

    /** 解析 DataQL 执行脚本 */
    public static QueryModel queryParser(Reader queryReader) throws IOException {
        return queryParser(CharStreams.fromReader(queryReader));
    }

    /** 解析 DataQL 执行脚本 */
    public static QueryModel queryParser(InputStream queryInput) throws IOException {
        return queryParser(queryInput, StandardCharsets.UTF_8);
    }

    /** 解析 DataQL 执行脚本 */
    public static QueryModel queryParser(InputStream inputStream, Charset charset) throws IOException {
        return queryParser(CharStreams.fromStream(inputStream, charset));
    }

    /** 解析并编译 DataQL 执行脚本 */
    public static QIL queryCompiler(String queryString, Finder finder) throws IOException {
        return queryCompiler(queryParser(queryString), finder);
    }

    /** 解析并编译 DataQL 执行脚本 */
    public static QIL queryCompiler(Reader queryReader, Finder finder) throws IOException {
        return queryCompiler(queryParser(queryReader), finder);
    }

    /** 解析并编译 DataQL 执行脚本 */
    public static QIL queryCompiler(InputStream queryInput, Finder finder) throws IOException {
        return queryCompiler(queryParser(queryInput, StandardCharsets.UTF_8), finder);
    }

    /** 解析并编译 DataQL 执行脚本 */
    public static QIL queryCompiler(InputStream queryInput, Charset charset, Finder finder) throws IOException {
        return queryCompiler(queryParser(queryInput, charset), finder);
    }

    /** 编译已经解析好的 DataQL 模型 */
    public static QIL queryCompiler(QueryModel queryModel, Finder finder) throws IOException {
        RootBlockSet rootBlockSet = null;
        if (queryModel instanceof RootBlockSet) {
            rootBlockSet = (RootBlockSet) queryModel;
        } else {
            rootBlockSet = queryParser(CharStreams.fromString(queryModel.toQueryString()));
        }
        //
        InstQueue queue = new InstQueue();
        CompilerContext compilerContext = new CompilerContext(new CompilerEnvironment(finder));
        compilerContext.findInstCompilerByInst(rootBlockSet).doCompiler(queue);
        Instruction[][] queueSet = queue.buildArrays();
        return new QIL(queueSet);
    }

    private static RootBlockSet queryParser(CharStream charStream) {
        DataQLLexer lexer = new DataQLLexer(charStream);
        DataQLParser qlParser = new DataQLParser(new CommonTokenStream(lexer));
        DataQLVisitor visitor = new DefaultDataQLVisitor();
        return (RootBlockSet) visitor.visit(qlParser.rootInstSet());
    }

    /** 创建查询实例 */
    public static Query createQuery(String queryString, Finder finder) throws IOException {
        return createQuery(queryCompiler(queryString, finder), finder);
    }

    /** 创建查询实例 */
    public static Query createQuery(Reader queryReader, Finder finder) throws IOException {
        return createQuery(queryCompiler(queryReader, finder), finder);
    }

    /** 创建查询实例 */
    public static Query createQuery(InputStream queryInput, Finder finder) throws IOException {
        return createQuery(queryCompiler(queryInput, finder), finder);
    }

    /** 创建查询实例 */
    public static Query createQuery(InputStream inputStream, Charset charset, Finder finder) throws IOException {
        return createQuery(queryCompiler(inputStream, charset, finder), finder);
    }

    /** 创建查询实例 */
    public static Query createQuery(QueryModel queryModel, Finder finder) throws IOException {
        return createQuery(queryCompiler(queryModel, finder), finder);
    }

    /** 创建查询实例 */
    public static Query createQuery(QIL qil, Finder finder) {
        return new QueryImpl(qil, finder);
    }
}