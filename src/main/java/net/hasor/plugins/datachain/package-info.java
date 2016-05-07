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
 * 数据对象转换工具，提供 A 类型对象到 B 类型对象转换功能。并使开发者在转换过程中可以实现更加高级别的控制协调能力。
 * 使用场景：
 *  如，DO 到 TO or VO，以及各种 O 之间的数据转换，这些数据对象随着业务和团队组成，无法简单的 Bean copy 去解决数据转换问题。
 *  另外，随着业务模型的复杂度增加，类型转换可能会遍布应用程序的各个角落，DataChain可以帮你归类整理类型转换。使其可以从用复用。
 */
package net.hasor.plugins.datachain;