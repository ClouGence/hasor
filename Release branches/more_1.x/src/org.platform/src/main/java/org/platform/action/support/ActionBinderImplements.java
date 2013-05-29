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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.more.util.StringUtils;
import org.platform.action.ActionBinder;
import org.platform.action.ActionInvoke;
import org.platform.action.support.InternalActionInvoke.InternalClassActionInvoke;
import org.platform.action.support.InternalActionInvoke.InternalInvokeActionInvoke;
import org.platform.action.support.InternalActionInvoke.InternalMethodActionInvoke;
import org.platform.action.support.InternalActionInvoke.InternalObjectActionInvoke;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.internal.UniqueAnnotations;
/***
 * 接口{@link ActionBinder}的实现类。
 * @version : 2013-5-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class ActionBinderImplements implements Module, ActionBinder {
    private Map<String, NameSpaceBindingBuilderImpl> nameSpace = new HashMap<String, NameSpaceBindingBuilderImpl>();
    @Override
    public NameSpaceBindingBuilder bindNameSpace(String namespace) {
        if (this.nameSpace.containsKey(namespace) == true)
            return nameSpace.get(namespace);
        NameSpaceBindingBuilderImpl nameSpace = new NameSpaceBindingBuilderImpl(namespace);
        this.nameSpace.put(namespace, nameSpace);
        return nameSpace;
    }
    @Override
    public void configure(Binder binder) {
        ArrayList<NameSpaceBindingBuilderImpl> nsList = new ArrayList<NameSpaceBindingBuilderImpl>(nameSpace.values());
        Collections.sort(nsList, new Comparator<NameSpaceBindingBuilderImpl>() {
            @Override
            public int compare(NameSpaceBindingBuilderImpl o1, NameSpaceBindingBuilderImpl o2) {
                String ns1 = o1.getNameSpace();
                String ns2 = o2.getNameSpace();
                return ns1.compareToIgnoreCase(ns2);
            }
        });
        for (NameSpaceBindingBuilderImpl item : nsList)
            item.configure(binder);
    }
    /**/
    /**/
    /*-------------------------------------*/
    private static class NameSpaceBindingBuilderImpl implements Module, NameSpaceBindingBuilder {
        private String                                namespace = null;
        private Map<String, ActionBindingBuilderImpl> actionMap = new HashMap<String, ActionBindingBuilderImpl>();
        public NameSpaceBindingBuilderImpl(String namespace) {
            this.namespace = namespace;
        }
        @Override
        public String getNameSpace() {
            return namespace;
        }
        @Override
        public ActionBindingBuilder bindAction(String actionName) {
            if (this.actionMap.containsKey(actionName) == true)
                return actionMap.get(actionName);
            ActionBindingBuilderImpl actionBuilder = new ActionBindingBuilderImpl(actionName);
            this.actionMap.put(actionName, actionBuilder);
            return actionBuilder.onMethod("ANY");
        }
        @Override
        public void configure(Binder binder) {
            binder.bind(InternalActionNameSpace.class).annotatedWith(UniqueAnnotations.create()).toInstance(new InternalActionNameSpace(this.namespace));
            for (ActionBindingBuilderImpl ent : this.actionMap.values())
                ent.configure(binder);
        }
    }
    //
    private static class ActionBindingBuilderImpl implements Module, ActionBindingBuilder {
        private String               actionName   = null;
        private InternalActionInvoke actionInvoke = null;
        private ArrayList<String>    onMethod     = new ArrayList<String>();
        //
        public ActionBindingBuilderImpl(String actionName) {
            this.actionName = actionName;
        }
        @Override
        public String getActionName() {
            return this.actionName;
        }
        @Override
        public ActionBindingBuilder onMethod(String httpMethod) {
            if (StringUtils.isBlank(httpMethod) == true)
                return null;
            this.onMethod.add(httpMethod.toUpperCase());
            return this;
        }
        @Override
        public void bindMethod(Method targetMethod) {
            String[] methods = this.onMethod.toArray(new String[this.onMethod.size()]);
            this.actionInvoke = new InternalMethodActionInvoke(methods, targetMethod);
        }
        @Override
        public void bindActionInvoke(ActionInvoke targetInvoke) {
            String[] methods = this.onMethod.toArray(new String[this.onMethod.size()]);
            this.actionInvoke = new InternalInvokeActionInvoke(methods, targetInvoke);
        }
        @Override
        public void bindClass(Class<?> targetClass) {
            String[] methods = this.onMethod.toArray(new String[this.onMethod.size()]);
            this.actionInvoke = new InternalClassActionInvoke(methods, targetClass);
        }
        @Override
        public void bindObject(Object targetObject) {
            String[] methods = this.onMethod.toArray(new String[this.onMethod.size()]);
            this.actionInvoke = new InternalObjectActionInvoke(methods, targetObject);
        }
        @Override
        public void configure(Binder binder) {
            if (this.actionInvoke != null)
                binder.bind(InternalActionInvoke.class).annotatedWith(UniqueAnnotations.create()).toInstance(this.actionInvoke);
        }
    }
}