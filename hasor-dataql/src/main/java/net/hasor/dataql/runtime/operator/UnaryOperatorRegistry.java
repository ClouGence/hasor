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
package net.hasor.dataql.runtime.operator;
/**
 * 一元运算操作对象的注册和查找。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-11
 */
public interface UnaryOperatorRegistry {
    /** 添加 操作符 实现 */
    public default void registryOperator(String symbolName, Class[] opeTypeSet, OperatorProcess process) {
        if (opeTypeSet == null || opeTypeSet.length == 0) {
            throw new NullPointerException("classSetA or classSetB is empty.");
        }
        for (Class opeType : opeTypeSet) {
            this.registryOperator(symbolName, opeType, process);
        }
    }

    /** 添加 操作符 实现 */
    public void registryOperator(String symbolName, Class<?> opeType, OperatorProcess process);

    /** 查找 操作符 实现 */
    public OperatorProcess findUnaryProcess(String symbolName, Class<?> fstType);
}