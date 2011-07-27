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
package org.more.workflow.runtime;
import org.more.workflow.metadata.ObjectMetadata;
/**
 * 该类用于描述一个{@link Runtime}的元信息，通过RuntimeMetadata类型可以创建一个{@link Runtime}对象。
 * Date : 2010-6-16
 * @author 赵永春
 */
public class RuntimeMetadata extends ObjectMetadata {
    private final Class<? extends Runtime> runtimeType; //FormBean的具体类型
    public RuntimeMetadata(String metadataID, Class<? extends Runtime> runtimeType) {
        super(metadataID);
        this.runtimeType = runtimeType;
    };
    /**获取runtimeType的具体类型。*/
    public Class<? extends Runtime> getRuntimeType() {
        return this.runtimeType;
    };
};