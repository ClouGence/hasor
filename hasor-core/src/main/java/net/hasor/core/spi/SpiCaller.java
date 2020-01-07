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
package net.hasor.core.spi;
/**
 * 执行 Spi 调用，扩展 SpiResultCaller 接口。并提供了可以无返回值的形式。
 * @version : 2019年06月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface SpiCaller<T, R> {
    public R doResultSpi(T listener) throws Throwable;

    public interface SpiCallerWithoutResult<T> extends SpiCaller<T, Object> {
        public default Object doResultSpi(T listener) throws Throwable {
            this.doSpi(listener);
            return null;
        }

        public void doSpi(T listener) throws Throwable;
    }
}