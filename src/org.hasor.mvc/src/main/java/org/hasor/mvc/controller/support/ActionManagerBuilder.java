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
package org.hasor.mvc.controller.support;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hasor.mvc.controller.ActionBinder;
import org.more.util.ArrayUtils;
import org.more.util.BeanUtils;
import org.more.util.StringUtils;
import com.google.inject.Binder;
import com.google.inject.internal.UniqueAnnotations;
/***
 * 接口{@link ActionBinder}的实现类。
 * @version : 2013-5-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class ActionManagerBuilder implements ActionBinder {
    private Map<String, InternalNameSpaceBindingBuilder> nameSpaceMap = new HashMap<String, InternalNameSpaceBindingBuilder>();
    @Override
    public NameSpaceBindingBuilder bindNameSpace(String namespace) {
        InternalNameSpaceBindingBuilder ansBuilder = this.nameSpaceMap.get(namespace);
        if (ansBuilder == null) {
            ansBuilder = new InternalNameSpaceBindingBuilder(namespace);
            this.nameSpaceMap.put(namespace, ansBuilder);
        }
        return ansBuilder;
    }
    /**构建ActionManager*/
    public void buildManager(Binder binder) {
        for (InternalNameSpaceBindingBuilder item : this.nameSpaceMap.values()) {
            ActionNameSpace ans = item.buildNameSpace(binder);
            binder.bind(ActionNameSpace.class).annotatedWith(UniqueAnnotations.create()).toInstance(ans);
        }
    }
    //
    /**{@link NameSpaceBindingBuilder}接口实现类，用于注册Action*/
    private static class InternalNameSpaceBindingBuilder implements NameSpaceBindingBuilder {
        private String                             namespace  = null;
        private List<AbstractActionBindingBuilder> actionList = new ArrayList<AbstractActionBindingBuilder>();
        //
        public InternalNameSpaceBindingBuilder(String namespace) {
            this.namespace = namespace;
        }
        @Override
        public String getNameSpace() {
            return this.namespace;
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
            return groupActionBuilder;
        }
        @Override
        public ActionBindingBuilder bindActionMethod(Method targetMethod) {
            if (targetMethod == null)
                return null;
            AbstractActionBindingBuilder actionBuilder = new ActionBindingBuilderImpl(targetMethod);
            this.actionList.add(actionBuilder);
            return actionBuilder;
        }
        //
        public ActionNameSpace buildNameSpace(Binder binder) {
            ActionNameSpace ans = new ActionNameSpace(this.namespace);
            for (AbstractActionBindingBuilder actBuilder : this.actionList)
                actBuilder.buildActionNameSpace(binder, ans);
            return ans;
        }
    }
    //
    /**目的是用来统一{@link ActionBindingBuilderImpl}、{@link ActionBindingBuilderGroup}两个类型。*/
    private static abstract class AbstractActionBindingBuilder implements ActionBindingBuilder {
        public abstract void buildActionNameSpace(Binder binder, ActionNameSpace ans);
    }
    //
    /**对一个Action进行定义*/
    private static class ActionBindingBuilderImpl extends AbstractActionBindingBuilder {
        private Method      targetMethod   = null;
        private Object      targetObject   = null;
        private Set<String> bindHttpMethod = new HashSet<String>();
        private String      mimeType       = null;
        private String      mappingRestful = null;
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
            if (httpMethod == null)
                return null;
            this.bindHttpMethod.add(httpMethod);
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
        public void buildActionNameSpace(Binder binder, ActionNameSpace ans) {
            //1.HttpMethod
            String[] bindMethods = this.bindHttpMethod.toArray(new String[this.bindHttpMethod.size()]);
            String restfulString = null;
            //2.RestfulMapping
            if (StringUtils.isBlank(this.mappingRestful) == false) {
                restfulString = /*ans.getNameSpace() +*/"/" + this.mappingRestful;//TODO 
                restfulString = restfulString.replace("\\", "/").replaceAll("[/]{2}", "/");
                char lastChar = restfulString.charAt(restfulString.length() - 1);
                if (lastChar == '/')
                    restfulString = restfulString.substring(0, restfulString.length() - 1);
                restfulString = restfulString.replace("*", ".*").replace("?", ".");
            } else
                restfulString = null;
            //3.
            if (ArrayUtils.isEmpty(bindMethods))
                bindMethods = new String[] { "ANY" };
            ActionDefineImpl define = new ActionDefineImpl(this.targetMethod, bindMethods, this.mimeType, restfulString, this.targetObject);
            ans.putActionDefine(define);
        }
    }
    //
    /**对一组{@link ActionBindingBuilder}进行配置*/
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
        @Override
        public void buildActionNameSpace(Binder binder, ActionNameSpace ans) {
            for (AbstractActionBindingBuilder item : elements)
                item.buildActionNameSpace(binder, ans);
        }
    }
}