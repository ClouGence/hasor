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
package net.hasor.dataql.udfs;
import net.hasor.dataql.Udf;
import net.hasor.dataql.UdfSourceAssembly;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 状态函数 <code>import 'net.hasor.dataql.udfs.StateUdfSource' as state;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
public class StateUdfSource implements UdfSourceAssembly {
    /** 返回一个自增的 int，每次调用函数获取值都会自增 1。 */
    public static Udf decInt(int initValue) {
        AtomicInteger atomicLong = new AtomicInteger(initValue);
        return (params, readOnly) -> atomicLong.incrementAndGet();
    }

    /** 返回一个自增的long，每次调用函数获取值都会自增 1。 */
    public static Udf decLong(long initValue) {
        AtomicLong atomicLong = new AtomicLong(initValue);
        return (params, readOnly) -> atomicLong.incrementAndGet();
    }

    /** 返回一个自减的 int，每次调用函数获取值都会自减 1。 */
    public static Udf incInt(int initValue) {
        AtomicInteger atomicLong = new AtomicInteger(initValue);
        return (params, readOnly) -> atomicLong.decrementAndGet();
    }

    /** 返回一个自减的long，每次调用函数获取值都会自减 1。 */
    public static Udf incLong(long initValue) {
        AtomicLong atomicLong = new AtomicLong(initValue);
        return (params, readOnly) -> atomicLong.decrementAndGet();
    }
}
