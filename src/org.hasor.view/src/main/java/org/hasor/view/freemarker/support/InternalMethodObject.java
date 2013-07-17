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
package org.hasor.view.freemarker.support;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.hasor.Assert;
import org.more.util.StringConvertUtils;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
/**
 * ±Í«©∂‘œÛ°£
 * @version : 2012-5-13
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class InternalMethodObject implements TemplateMethodModel {
    private Method fmMethodType = null;
    private Object target       = null;
    //
    public InternalMethodObject(Method fmMethodType, Object target) {
        Assert.isNotNull(target, "method Object is null.");
        this.fmMethodType = fmMethodType;
        this.target = target;
    }
    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Class<?>[] paramTypes = fmMethodType.getParameterTypes();
        Object[] paramObjects = new Object[paramTypes.length];
        //
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > arguments.size())
                paramObjects[i] = null;
            else
                paramObjects[i] = StringConvertUtils.changeType(arguments.get(i), paramTypes[i]);
        }
        try {
            return this.fmMethodType.invoke(this.target, paramObjects);
        } catch (InvocationTargetException e) {
            Throwable ee = e.getCause();
            TemplateModelException tme = new TemplateModelException(ee instanceof Exception ? (Exception) ee : e);
            tme.initCause(e.getCause());
            throw tme;
        } catch (Exception e) {
            throw new TemplateModelException(e);
        }
    }
}