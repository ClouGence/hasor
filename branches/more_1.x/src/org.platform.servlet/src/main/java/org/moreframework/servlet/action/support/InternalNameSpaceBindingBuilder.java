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
package org.moreframework.servlet.action.support;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
import org.moreframework.servlet.action.ActionBinder.ActionBindingBuilder;
import org.moreframework.servlet.action.ActionBinder.NameSpaceBindingBuilder;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.internal.UniqueAnnotations;
/**
 * 
 * @version : 2013-5-30
 * @author 赵永春 (zyc@byshell.org)
 */
class InternalNameSpaceBindingBuilder implements Module, NameSpaceBindingBuilder {
    private String                             namespace  = null;
    private List<AbstractActionBindingBuilder> actionList = new ArrayList<AbstractActionBindingBuilder>();
    public InternalNameSpaceBindingBuilder(String namespace) {
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
        AbstractActionBindingBuilder actionBuilder = new ActionBindingBuilderImpl(targetMethod);
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
    public void configure(Binder binder) {
        binder.bind(InternalActionNameSpace.class).annotatedWith(UniqueAnnotations.create()).toInstance(new InternalActionNameSpace(this.namespace));
        for (AbstractActionBindingBuilder ent : this.actionList)
            ent.configure(binder, this);
    }
    //
    //
    /**目的是用来统一{@link Module}、{@link ActionBindingBuilder}两个类型，并且表示一个ActionBindingBuilder定义。*/
    private static abstract class AbstractActionBindingBuilder implements ActionBindingBuilder {
        public abstract void configure(Binder binder, NameSpaceBindingBuilder nameSpaceBindingBuilder);
    }
    /**对一个Action进行定义*/
    private static class ActionBindingBuilderImpl extends AbstractActionBindingBuilder {
        private Method            targetMethod   = null;
        private Object            targetObject   = null;
        private ArrayList<String> bindHttpMethod = new ArrayList<String>();
        private String            mimeType       = null;
        private String            mappingRestful = null;
        //
        public ActionBindingBuilderImpl(Method targetMethod) {
            this.targetMethod = targetMethod;
        }
        @Override
        public void toInstance(Object targetObject) {
            this.targetObject = targetObject;
        }
        @Override
        public ActionBindingBuilder onHttpMethod(String httpMethod) {
            if (StringUtils.isBlank(httpMethod) == true)
                return null;
            this.bindHttpMethod.add(httpMethod.toUpperCase());
            return this;
        }
        @Override
        public void mappingRestful(String mappingRestful) {
            this.mappingRestful = mappingRestful;
        }
        @Override
        public ActionBindingBuilder returnMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }
        @Override
        public void configure(Binder binder, NameSpaceBindingBuilder nameSpaceBindingBuilder) {
            InternalActionInvoke actionInvoke = null;;
            if (this.targetObject != null)
                actionInvoke = new InternalActionInvoke(this.targetMethod, this.mimeType, this.targetObject);
            else
                actionInvoke = new InternalActionInvoke(this.targetMethod, this.mimeType);
            //
            actionInvoke.setHttpMethod(this.bindHttpMethod.toArray(new String[this.bindHttpMethod.size()]));
            if (StringUtils.isBlank(this.mappingRestful) == false) {
                String restfulString = nameSpaceBindingBuilder.getNameSpace() + "/" + this.mappingRestful;
                restfulString = restfulString.replace("\\", "/").replaceAll("[/]{2}", "/");
                restfulString = restfulString.replace("*", ".*").replace("?", ".");
                actionInvoke.setRestfulMapping(restfulString);
                binder.bind(ActionInvoke2.class).annotatedWith(UniqueAnnotations.create()).toInstance(actionInvoke);
            }
            binder.bind(ActionInvoke.class).annotatedWith(UniqueAnnotations.create()).toInstance(actionInvoke);
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
        public void mappingRestful(String mappingRestful) {
            for (AbstractActionBindingBuilder item : elements)
                item.mappingRestful(mappingRestful);
        }
        @Override
        public void configure(Binder binder, NameSpaceBindingBuilder nameSpaceBindingBuilder) {
            for (AbstractActionBindingBuilder item : elements)
                item.configure(binder, nameSpaceBindingBuilder);
        }
        @Override
        public void toInstance(Object targetAction) {
            for (AbstractActionBindingBuilder item : elements)
                item.toInstance(targetAction);
        }
        @Override
        public ActionBindingBuilder returnMimeType(String mimeType) {
            for (AbstractActionBindingBuilder item : elements)
                item.returnMimeType(mimeType);
            return this;
        }
    }
}