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
package org.platform.servlet.resource.support;
import java.util.HashMap;
import org.more.util.StringUtils;
import org.platform.servlet.resource.IResourceLoaderCreator;
import org.platform.servlet.resource.ResourceBinder;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.internal.UniqueAnnotations;
/**
 * 定义一个{@link IResourceLoaderCreator}映射类型。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class ResourceBinderImplements implements Module, ResourceBinder {
    private HashMap<String, ResourceLoaderCreatorDefinition> define = new HashMap<String, ResourceLoaderCreatorDefinition>();
    @Override
    public void bindLoaderCreator(String name, Class<IResourceLoaderCreator> resourceLoaderCreatorType) {
        if (StringUtils.isBlank(name) || resourceLoaderCreatorType == null)
            return;
        this.define.put(name, new ResourceLoaderCreatorDefinition(name, resourceLoaderCreatorType));
    }
    @Override
    public void configure(Binder binder) {
        for (ResourceLoaderCreatorDefinition define : this.define.values())
            binder.bind(ResourceLoaderCreatorDefinition.class).annotatedWith(UniqueAnnotations.create()).toInstance(define);
    }
}