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
package org.platform.action._;
import java.util.Map;
/**
 * 代表一个action执行时的参数堆栈，每当请求执行Action方法时候submit都会自动创建一个新的堆栈。
 * @version : 2011-7-14
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionStack extends Map<String, Object> {};