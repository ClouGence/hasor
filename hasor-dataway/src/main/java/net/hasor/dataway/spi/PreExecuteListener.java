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
import java.util.EventListener;
import java.util.concurrent.Future;

/**
 * 在接口执行之前，可以通过这个 SPI 实现接口缓存。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-19
 */
public interface PreExecuteListener extends EventListener {
    /**
     * 当接口被正式调用之前触发。
     * @param apiInfo API 请求信息。
     * @param future 可以提前响应结果。
     */
    public void preExecute(ApiInfo apiInfo, Future<Object> future);
}