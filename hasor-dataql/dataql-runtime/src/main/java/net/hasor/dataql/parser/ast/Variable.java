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
package net.hasor.dataql.parser.ast;
/**
 * 变量，用于表示一切 QL 中的表达式，可定义序列块（序列块 = BlockSet，可定义 = 使用 var 指令定义 lambda）
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-11-07
 */
public interface Variable extends Inst {
}
