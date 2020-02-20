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
import net.hasor.dataql.CustomizeScope;
import net.hasor.dataql.Finder;
import net.hasor.dataql.FragmentProcess;
import net.hasor.dataql.Query;
import net.hasor.dataql.runtime.operator.OperatorManager;
import net.hasor.dataql.runtime.operator.OperatorProcess;

import java.util.Map;

/**
 * 指令执行器接口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-07-14
 */
public class InsetProcessContext extends HintsSet implements CustomizeScope {
    private final static OperatorManager opeManager = OperatorManager.defaultManager();
    private final        CustomizeScope  customizeScope;
    private final        Finder          finder;

    InsetProcessContext(CustomizeScope customizeScope, Finder finder) {
        if (finder == null) {
            finder = new Finder() {
            };
        }
        this.customizeScope = customizeScope;
        this.finder = finder;
    }

    public Finder getFinder() {
        return finder;
    }

    /** 查找一元运算执行器 */
    public OperatorProcess findUnaryOperator(String unarySymbol, Class<?> fstType) {
        return opeManager.findUnaryProcess(unarySymbol, fstType);
    }

    /** 查找二元运算执行器 */
    public OperatorProcess findDyadicOperator(String dyadicSymbol, Class<?> fstType, Class<?> secType) {
        return opeManager.findDyadicProcess(dyadicSymbol, fstType, secType);
    }

    /** 获取环境数据，symbol 可能的值有：@、#、$。其中 # 为默认 */
    public Map<String, ?> findCustomizeEnvironment(String symbol) {
        if (this.customizeScope == null) {
            return null;
        }
        return this.customizeScope.findCustomizeEnvironment(symbol);
    }

    public Object loadObject(String udfType) throws ClassNotFoundException {
        // .确定ClassLoader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> c = classLoader.loadClass(Query.class.getName());
        if (c != Query.class) {
            classLoader = Query.class.getClassLoader();
        }
        Class<?> loadClass = classLoader.loadClass(udfType);
        return this.finder.findBean(loadClass);
    }

    public FragmentProcess findFragmentProcess(String fragmentType) {
        return this.finder.findFragmentProcess(fragmentType);
    }
}
