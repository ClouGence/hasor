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
package net.hasor.dataql.fx.basic;
import net.hasor.core.Singleton;
import net.hasor.dataql.Udf;
import net.hasor.dataql.UdfSourceAssembly;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 状态函数 <code>import 'net.hasor.dataql.fx.basic.StateUdfSource' as state;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
@Singleton
public class StateUdfSource implements UdfSourceAssembly {
    /** 返回一个Udf，每次调用这个UDF，都会返回一个 Number。Number值较上一次会自增 1。 */
    public static Udf decNumber(long initValue) {
        AtomicLong atomicLong = new AtomicLong(initValue);
        return (params, readOnly) -> atomicLong.incrementAndGet();
    }

    /** 返回一个Udf，每次调用这个UDF，都会返回一个 Number。Number值较上一次会自减 1。 */
    public static Udf incNumber(long initValue) {
        AtomicLong atomicLong = new AtomicLong(initValue);
        return (params, readOnly) -> atomicLong.decrementAndGet();
    }

    /** 返回一个完整格式的 UUID 字符串。  */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /** 返回一个不含"-" 符号的 UUID 字符串 */
    public static String uuidToShort() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
