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
package org.more.workflow.metadata;
/**
 * 该接口用于获取其元信息对象，提供获取其元信息对象则需要实现该接口。
 * 在workflow系统中元信息对象需要继承{@link ObjectMetadata}类型对象。
 * 泛型参数T可以决定实现MetadataHolder接口的类可以返回的具体元信息对象类型。
 * Date : 2010-5-16
 * @author 赵永春
 */
public interface MetadataHolder {
    /** 获取模型的元信息对象。 */
    public ObjectMetadata getMetadata();
};