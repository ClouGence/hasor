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
package org.platform.action.support;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.platform.action.ActionBinder;
import com.google.inject.Binder;
import com.google.inject.Module;
/***
 * 接口{@link ActionBinder}的实现类。
 * @version : 2013-5-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class ActionBinderImplements implements Module, ActionBinder {
    private Map<String, InternalNameSpaceBindingBuilder> nameSpace = new HashMap<String, InternalNameSpaceBindingBuilder>();
    @Override
    public NameSpaceBindingBuilder bindNameSpace(String namespace) {
        if (this.nameSpace.containsKey(namespace) == true)
            return nameSpace.get(namespace);
        InternalNameSpaceBindingBuilder nameSpace = new InternalNameSpaceBindingBuilder(namespace);
        this.nameSpace.put(namespace, nameSpace);
        return nameSpace;
    }
    @Override
    public void configure(Binder binder) {
        ArrayList<InternalNameSpaceBindingBuilder> nsList = new ArrayList<InternalNameSpaceBindingBuilder>(nameSpace.values());
        Collections.sort(nsList, new Comparator<InternalNameSpaceBindingBuilder>() {
            @Override
            public int compare(InternalNameSpaceBindingBuilder o1, InternalNameSpaceBindingBuilder o2) {
                String ns1 = o1.getNameSpace();
                String ns2 = o2.getNameSpace();
                return ns1.compareToIgnoreCase(ns2);
            }
        });
        for (InternalNameSpaceBindingBuilder item : nsList)
            item.configure(binder);
    }
}