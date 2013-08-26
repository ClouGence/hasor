/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.hasor.freemarker.support;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.hasor.freemarker.FmBinder;
import org.hasor.freemarker.FmTemplateLoaderCreator;
import org.hasor.freemarker.Tag;
import org.more.util.StringUtils;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.internal.UniqueAnnotations;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
/**
 * 
 * @version : 2013-5-22
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class FmBinderImplements implements Module, FmBinder {
    private List<TemplateLoaderCreatorDefinition> templateLoaderDefinition = new ArrayList<TemplateLoaderCreatorDefinition>();
    private List<FmMethodDefinition>              fmMethodDefinition       = new ArrayList<FmMethodDefinition>();
    private List<FmTagDefinition>                 fmTagDefinition          = new ArrayList<FmTagDefinition>();
    private List<FmObjectDefinition>              fmObjectDefinition       = new ArrayList<FmObjectDefinition>();
    public void bindTemplateLoaderCreator(String name, Class<FmTemplateLoaderCreator> templateLoaderCreatorType) {
        if (StringUtils.isBlank(name) || templateLoaderCreatorType == null)
            return;
        this.templateLoaderDefinition.add(new TemplateLoaderCreatorDefinition(name, templateLoaderCreatorType));
    }
    public void bindTag(String tagName, Class<Tag> fmTagType) {
        if (StringUtils.isBlank(tagName) || fmTagType == null)
            throw new NullPointerException("tagName or tagType is null.");
        this.fmTagDefinition.add(new FmTagDefinition(tagName, fmTagType));
    }
    public void bindMethod(String funName, Method fmMethodType) {
        if (StringUtils.isBlank(funName) || fmMethodType == null)
            throw new NullPointerException("funName or targetMethod is null.");
        this.fmMethodDefinition.add(new FmMethodDefinition(funName, fmMethodType));
    }
    public void bindObject(String objName, Object targetObject) throws TemplateModelException {
        if (StringUtils.isBlank(objName) || targetObject == null)
            throw new NullPointerException("objName or targetObject is null.");
        TemplateModel modelObject = DefaultObjectWrapper.DEFAULT_WRAPPER.wrap(targetObject);
        this.fmObjectDefinition.add(new FmObjectDefinition(objName, modelObject));
    }
    public void configure(Binder binder) {
        for (TemplateLoaderCreatorDefinition define : this.templateLoaderDefinition) {
            binder.bind(TemplateLoaderCreatorDefinition.class).annotatedWith(UniqueAnnotations.create()).toInstance(define);
        }
        for (FmTagDefinition define : this.fmTagDefinition) {
            binder.bind(FmTagDefinition.class).annotatedWith(UniqueAnnotations.create()).toInstance(define);
        }
        for (FmMethodDefinition define : this.fmMethodDefinition) {
            binder.bind(FmMethodDefinition.class).annotatedWith(UniqueAnnotations.create()).toInstance(define);
        }
        for (FmObjectDefinition define : this.fmObjectDefinition) {
            binder.bind(FmObjectDefinition.class).annotatedWith(UniqueAnnotations.create()).toInstance(define);
        }
    }
}