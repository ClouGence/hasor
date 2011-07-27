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
package org.more.workflow.form;
/**
 * 这是一个标记接口，任何一个POJO都可以成为流程的FormBean作为formBean需要满足下面两个条件。
 * <ul>
 * <li>必须实现FormBean接口</li>
 * <li>必须拥有一个无参的共有构造方法</li>
 * </ul>
 * WorkFlow在创建FormBean时会调用其无参的构造方法来实例化FormBean，{@link FormMetadata}在实例化
 * FormBean对象时候会自动注入其属性。
 * Date : 2010-5-16
 * @author 赵永春
 */
public interface FormBean {};