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
import java.util.function.Supplier;

/**
 * 为 compilerVar 提供一个延迟装载的机制，当 LOAD 指令加载到 compilerVar 时。会自动对该接口对象进行展开。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-11
 */
public interface VarSupplier extends Supplier<Object> {
}