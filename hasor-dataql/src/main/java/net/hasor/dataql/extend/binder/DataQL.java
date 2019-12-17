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
package net.hasor.dataql.extend.binder;
import net.hasor.core.BindInfo;
import net.hasor.dataql.Finder;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.Query;
import net.hasor.dataql.compiler.QueryModel;
import net.hasor.dataql.compiler.qil.QIL;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

/**
 * DataQL 上下文。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface DataQL {
    /** 添加全局变量（等同于 compilerVar） */
    public default DataQL addShareVarInstance(String name, Object instance) {
        return this.addShareVar(name, () -> instance);
    }

    /** 添加全局变量（等同于 compilerVar） */
    public <T> DataQL addShareVar(String name, Class<? extends T> implementation);

    /** 添加全局变量（等同于 compilerVar） */
    public <T> DataQL addShareVar(String name, Supplier<T> provider);

    /** 添加一个外部代码片段执行器 */
    public default DataQL addFragmentProcess(String name, FragmentProcess instance) {
        return this.addShareVar(name, () -> instance);
    }

    /** 添加一个外部代码片段执行器 */
    public <T extends FragmentProcess> DataQL addFragmentProcess(String name, BindInfo<T> implementation);

    /** 添加一个外部代码片段执行器 */
    public <T extends FragmentProcess> DataQL addFragmentProcess(String name, Class<T> implementation);

    /** 添加一个外部代码片段执行器 */
    public <T extends FragmentProcess> DataQL addFragmentProcess(String name, Supplier<T> provider);

    /** 获取默认的 Finder */
    public Finder getFinder();

    /**
     * 解析 DataQL 执行脚本
     * @param queryString 脚本字符串
     */
    public default QueryModel parserQuery(String queryString) throws IOException {
        return parserQuery(new StringReader(queryString));
    }

    /**
     * 解析 DataQL 执行脚本
     * @param queryReader 脚本输入流
     */
    public default QueryModel parserQuery(Reader queryReader) throws IOException {
        return parserQuery(CharStreams.fromReader(queryReader));
    }

    /**
     * 解析 DataQL 执行脚本
     * @param queryInput 脚本输入流，使用 UTF-8 字符集
     */
    public default QueryModel parserQuery(InputStream queryInput) throws IOException {
        return parserQuery(queryInput, StandardCharsets.UTF_8);
    }

    /**
     * 解析 DataQL 执行脚本
     * @param inputStream 脚本输入流
     * @param charset 读取字节流使用的字符集
     */
    public default QueryModel parserQuery(InputStream inputStream, Charset charset) throws IOException {
        return parserQuery(CharStreams.fromStream(inputStream, charset));
    }

    /**
     * 解析 DataQL 执行脚本
     * @param charStream 脚本输入流
     */
    public QueryModel parserQuery(CharStream charStream);

    /**
     * 解析并编译 DataQL 执行脚本
     * @param queryString 脚本字符串
     */
    public default QIL compilerQuery(String queryString) throws IOException {
        return compilerQuery(parserQuery(queryString));
    }

    /**
     * 解析并编译 DataQL 执行脚本
     * @param queryReader 脚本输入流
    `     */
    public default QIL compilerQuery(Reader queryReader) throws IOException {
        return compilerQuery(parserQuery(queryReader));
    }

    /**
     * 解析并编译 DataQL 执行脚本
     * @param queryInput 脚本输入流，使用 UTF-8 字符集
     */
    public default QIL compilerQuery(InputStream queryInput) throws IOException {
        return compilerQuery(parserQuery(queryInput, StandardCharsets.UTF_8));
    }

    /**
     * 解析并编译 DataQL 执行脚本
     * @param queryInput 脚本输入流
     * @param charset 读取字节流使用的字符集
     */
    public default QIL compilerQuery(InputStream queryInput, Charset charset) throws IOException {
        return compilerQuery(parserQuery(queryInput, charset));
    }

    /**
     * 编译已经解析好的 DataQL
     * @param queryModel 解析之后的 DataQL 查询模型。
     */
    public QIL compilerQuery(QueryModel queryModel) throws IOException;

    /** 创建查询实例 */
    public default Query createQuery(String queryString) throws IOException {
        return createQuery(compilerQuery(queryString));
    }

    /** 创建查询实例 */
    public default Query createQuery(Reader queryReader) throws IOException {
        return createQuery(compilerQuery(queryReader));
    }

    /** 创建查询实例 */
    public default Query createQuery(InputStream queryInput) throws IOException {
        return createQuery(compilerQuery(queryInput));
    }

    /** 创建查询实例 */
    public default Query createQuery(InputStream inputStream, Charset charset) throws IOException {
        return createQuery(compilerQuery(inputStream, charset));
    }

    /** 创建查询实例 */
    public Query createQuery(QIL compilerQIL);
}