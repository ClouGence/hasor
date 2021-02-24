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
import net.hasor.dataql.compiler.qil.*;
import net.hasor.dataql.parser.*;
import net.hasor.dataql.parser.ast.inst.RootBlockSet;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * DataQL 小工具。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-03
 */
public class QueryHelper {
    /**
     * 解析 DataQL 执行脚本
     * @param queryString 脚本字符串
     */
    public static QueryModel queryParser(String queryString) throws IOException {
        return queryParser(new StringReader(queryString));
    }

    /**
     * 解析 DataQL 执行脚本
     * @param queryReader 脚本输入流
     */
    public static QueryModel queryParser(Reader queryReader) throws IOException {
        return queryParser(CharStreams.fromReader(queryReader));
    }

    /**
     * 解析 DataQL 执行脚本
     * @param queryInput 脚本输入流，使用 UTF-8 字符集
     */
    public static QueryModel queryParser(InputStream queryInput) throws IOException {
        return queryParser(queryInput, StandardCharsets.UTF_8);
    }

    /**
     * 解析 DataQL 执行脚本
     * @param inputStream 脚本输入流
     * @param charset 读取字节流使用的字符集
     */
    public static QueryModel queryParser(InputStream inputStream, Charset charset) throws IOException {
        return queryParser(CharStreams.fromStream(Objects.requireNonNull(inputStream), charset));
    }

    public static QueryModel queryParser(CharStream charStream) throws QueryParseException {
        DataQLLexer lexer = new DataQLLexer(charStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
        //
        DataQLParser qlParser = new DataQLParser(new CommonTokenStream(lexer));
        qlParser.removeErrorListeners();
        qlParser.addErrorListener(ThrowingErrorListener.INSTANCE);
        DataQLParserVisitor visitor = new DefaultDataQLVisitor();
        return (RootBlockSet) visitor.visit(qlParser.rootInstSet());
    }

    /**
     * 解析并编译 DataQL 执行脚本
     * @param queryString 脚本字符串
     * @param importFinder import 导入用到的资源加载器。
     */
    public static QIL queryCompiler(String queryString, Finder importFinder) throws IOException {
        return queryCompiler(queryParser(queryString), CompilerArguments.DEFAULT, importFinder);
    }

    /**
     * 解析并编译 DataQL 执行脚本
     * @param queryReader 脚本输入流
     * @param importFinder import 导入用到的资源加载器。
     */
    public static QIL queryCompiler(Reader queryReader, Finder importFinder) throws IOException {
        return queryCompiler(queryParser(queryReader), CompilerArguments.DEFAULT, importFinder);
    }

    /**
     * 解析并编译 DataQL 执行脚本
     * @param queryInput 脚本输入流，使用 UTF-8 字符集
     * @param importFinder import 导入用到的资源加载器。
     */
    public static QIL queryCompiler(InputStream queryInput, Finder importFinder) throws IOException {
        return queryCompiler(queryParser(queryInput, StandardCharsets.UTF_8), CompilerArguments.DEFAULT, importFinder);
    }

    /**
     * 解析并编译 DataQL 执行脚本
     * @param queryInput 脚本输入流
     * @param charset 读取字节流使用的字符集
     * @param importFinder import 导入用到的资源加载器。
     */
    public static QIL queryCompiler(InputStream queryInput, Charset charset, Finder importFinder) throws IOException {
        return queryCompiler(queryParser(queryInput, charset), CompilerArguments.DEFAULT, importFinder);
    }

    /**
     * 编译已经解析好的 DataQL
     * @param queryModel 解析之后的 DataQL 查询模型。
     * @param compilerArguments 编译参数，编译参数中包含了编译器需要的一些列变量。例如：编译变量，相当于在脚本中预先 执行 var xxx = null;
     *                    其意义在于在编译期就把脚本中尚未定义过的变量预先进行声明从而免去通过 ${...} 或类似方式查找变量。
     * @param importFinder import 导入用到的资源加载器。
     */
    public static QIL queryCompiler(QueryModel queryModel, CompilerArguments compilerArguments, Finder importFinder) throws IOException {
        RootBlockSet rootBlockSet = null;
        if (queryModel instanceof RootBlockSet) {
            rootBlockSet = (RootBlockSet) queryModel;
        } else {
            rootBlockSet = (RootBlockSet) queryParser(CharStreams.fromString(queryModel.toQueryString()));
        }
        //
        compilerArguments = (compilerArguments == null) ? CompilerArguments.DEFAULT : compilerArguments;
        Set<String> compilerVar = compilerArguments.getCompilerVar();
        if (compilerVar == null) {
            compilerVar = Collections.emptySet();
        }
        //
        InstQueue queue = new InstQueue(compilerArguments);
        CompilerContext compilerContext = new CompilerContext(new CompilerEnvironment(importFinder));
        Map<String, Integer> compilerVarMap = new HashMap<>();
        compilerVar.forEach(var -> {
            int localIdx = compilerContext.push(var);
            compilerVarMap.put(var, localIdx);
        });
        compilerContext.findInstCompilerByInst(rootBlockSet).doCompiler(queue);
        Instruction[][] queueSet = queue.buildArrays();
        return new QIL(queueSet, compilerVarMap);
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
    public static Query createQuery(QueryModel queryModel, CompilerArguments compilerArguments, Finder finder) throws IOException {
        return createQuery(queryCompiler(queryModel, compilerArguments, finder), finder);
    }

    /** 创建查询实例 */
    public static Query createQuery(QIL qil, Finder finder) {
        return new QueryImpl(qil, finder);
    }
}
