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
import java.util.List;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
import org.platform.action.ActionBinder.ActionBindingBuilder;
import org.platform.action.ActionBinder.NameSpaceBindingBuilder;
import org.platform.action.ActionInvoke;
import org.platform.action.support.InternalActionInvoke.InternalInvokeActionInvoke;
import org.platform.action.support.InternalActionInvoke.InternalMethodActionInvoke;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.internal.UniqueAnnotations;
/**
 * 
 * @version : 2013-5-30
 * @author 赵永春 (zyc@byshell.org)
 */
class NameSpaceBindingBuilderImpl implements Module, NameSpaceBindingBuilder {
    private String                             namespace  = null;
    private List<AbstractActionBindingBuilder> actionList = new ArrayList<AbstractActionBindingBuilder>();
    public NameSpaceBindingBuilderImpl(String namespace) {
        this.namespace = namespace;
    }
    @Override
    public String getNameSpace() {
        return namespace;
    }
    @Override
    public ActionBindingBuilder bindActionMethod(Method targetMethod) {
        if (targetMethod == null)
            return null;
        AbstractActionBindingBuilder actionBuilder = new ActionBindingBuilderImpl(new InternalMethodActionInvoke(targetMethod));
        this.actionList.add(actionBuilder);
        return actionBuilder.onHttpMethod("ANY");
    }
    @Override
    public ActionBindingBuilder bindActionClass(Class<?> targetClass) {
        if (targetClass == null)
            return null;
        /*迭代targetClass中所有方法都加入到ActionBindingBuilderGroup中。*/
        ActionBindingBuilderGroup groupActionBuilder = new ActionBindingBuilderGroup();
        List<Method> methodList = BeanUtils.getMethods(targetClass);
        for (Method targetMethod : methodList) {
            AbstractActionBindingBuilder actionBuilder = (AbstractActionBindingBuilder) this.bindActionMethod(targetMethod);
            groupActionBuilder.getElements().add(actionBuilder);
        }
        this.actionList.add(groupActionBuilder);
        return groupActionBuilder.onHttpMethod("ANY");
    }
    @Override
    public ActionBindingBuilder bindActionInvoke(String actionName, ActionInvoke targetInvoke) {
        if (StringUtils.isBlank(actionName) || targetInvoke == null)
            return null;
        AbstractActionBindingBuilder actionBuilder = new ActionBindingBuilderImpl(new InternalInvokeActionInvoke(actionName, targetInvoke));
        this.actionList.add(actionBuilder);
        return actionBuilder.onHttpMethod("ANY");
    }
    @Override
    public ActionBindingBuilder bindActionObject(Object targetObject) {
        if (targetObject == null)
            return null;
        /*迭代targetClass中所有方法都加入到ActionBindingBuilderGroup中。*/
        ActionBindingBuilderGroup groupActionBuilder = new ActionBindingBuilderGroup();
        groupActionBuilder.toInstance(targetObject);
        List<Method> methodList = BeanUtils.getMethods(targetObject.getClass());
        for (Method targetMethod : methodList) {
            AbstractActionBindingBuilder actionBuilder = (AbstractActionBindingBuilder) this.bindActionMethod(targetMethod);
            groupActionBuilder.getElements().add(actionBuilder);
        }
        this.actionList.add(groupActionBuilder);
        return groupActionBuilder.onHttpMethod("ANY");
    }
    @Override
    public void configure(Binder binder) {
        binder.bind(InternalActionNameSpace.class).annotatedWith(UniqueAnnotations.create()).toInstance(new InternalActionNameSpace(this.namespace));
        for (AbstractActionBindingBuilder ent : this.actionList)
            ent.configure(binder);
    }
    //
    //
    /**目的是用来统一{@link Module}、{@link ActionBindingBuilder}两个类型，并且表示一个ActionBindingBuilder定义。*/
    private static abstract class AbstractActionBindingBuilder implements Module, ActionBindingBuilder {}
    /**对一个Action进行定义*/
    private static class ActionBindingBuilderImpl extends AbstractActionBindingBuilder {
        private String               restfulMapping = null;
        private Object               target         = null;
        private InternalActionInvoke actionInvoke   = null;
        private ArrayList<String>    onMethod       = new ArrayList<String>();
        //
        public ActionBindingBuilderImpl(InternalActionInvoke actionInvoke) {
            this.actionInvoke = actionInvoke;
        }
        @Override
        public void toInstance(Object target) {
            this.target = target;
        }
        @Override
        public ActionBindingBuilder onHttpMethod(String httpMethod) {
            if (StringUtils.isBlank(httpMethod) == true)
                return null;
            this.onMethod.add(httpMethod.toUpperCase());
            return this;
        }
        @Override
        public void restfulMapping(String restfulMapping) {
            this.restfulMapping = restfulMapping;
        }
        protected String getRestfulMapping() {
            return restfulMapping;
        }
        @Override
        public void configure(Binder binder) {
            InternalActionInvoke actionInvoke = this.actionInvoke;
            if (actionInvoke != null) {
                actionInvoke.setRestfulMapping(this.getRestfulMapping());
                actionInvoke.setActionMethod(this.onMethod.toArray(new String[this.onMethod.size()]));
                if (this.target != null)
                    actionInvoke.setTarget(this.target);
                binder.bind(InternalActionInvoke.class).annotatedWith(UniqueAnnotations.create()).toInstance(actionInvoke);
            }
        }
    }
    /**对一组Action进行定义*/
    private static class ActionBindingBuilderGroup extends AbstractActionBindingBuilder {
        private ArrayList<AbstractActionBindingBuilder> elements = new ArrayList<AbstractActionBindingBuilder>();
        //
        public ArrayList<AbstractActionBindingBuilder> getElements() {
            return elements;
        }
        @Override
        public ActionBindingBuilder onHttpMethod(String httpMethod) {
            for (AbstractActionBindingBuilder item : elements)
                item.onHttpMethod(httpMethod);
            return this;
        }
        @Override
        public void restfulMapping(String restfulMapping) {
            for (AbstractActionBindingBuilder item : elements)
                item.restfulMapping(restfulMapping);
        }
        @Override
        public void configure(Binder binder) {
            for (AbstractActionBindingBuilder item : elements)
                item.configure(binder);
        }
        @Override
        public void toInstance(Object targetAction) {
            for (AbstractActionBindingBuilder item : elements)
                item.toInstance(targetAction);
        }
    }
}