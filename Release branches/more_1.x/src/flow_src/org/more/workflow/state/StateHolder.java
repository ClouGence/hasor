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
package org.more.workflow.state;
/**
 * 该接口用于模型获取其状态操作对象，该接口具有着隔离模型与操作对象的作用。<br/>
 * 模型实现该接口以提供针对模型的操作接口。
 * Date : 2010-5-16
 * @author 赵永春
 */
public interface StateHolder {
    /**获取模型的状态操作接口。*/
    public AbstractStateHolder getStateHolder();
};