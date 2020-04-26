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
import net.hasor.dataql.runtime.ThrowRuntimeException;

import java.util.EventListener;

/**
 * Dataway API 调用之后的结果二次处理，常用用于对 QL 执行的结果做二次封装。（is chainSpi）
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-19
 */
public interface ResultProcessChainSpi extends EventListener {
    /** 成功完成调用 */
    public default Object callAfter(ApiInfo apiInfo, boolean fromPre, Object result) {
        return result;
    }

    /** 调用发生异常 */
    public default Object callError(ApiInfo apiInfo, Throwable e) {
        if (e instanceof ThrowRuntimeException) {
            return ((ThrowRuntimeException) e).getResult().unwrap();
        } else {
            return e.getMessage();
        }
    }
}