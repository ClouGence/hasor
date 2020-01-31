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
/**
 * DataQL 的 jsr223 支持。
 *
 * 获取参数变量操作符：@、#、$ 的实际效果如下：
 * <pre>
 * Bindings globalBindings = this.engine.getBindings(ScriptContext.GLOBAL_SCOPE);
 * Bindings engineBindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
 * Map dataMap = new HashMap<>(){{
 *   dataMap.putAll(globalBindings);
 *   dataMap.putAll(engineBindings);
 * };
 * CustomizeScope customizeScope = symbol -> {
 * if ("#".equals(symbol)) {
 *   return engineBindings;
 * } else if ("@".equals(symbol)) {
 *   return globalBindings;
 * } else {
 *   return dataMap;
 * }<pre/>
 *
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
@AopIgnore
package net.hasor.dataql.extend.jsr223;
import net.hasor.core.AopIgnore;