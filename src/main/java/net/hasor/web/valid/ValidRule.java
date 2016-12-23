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
package net.hasor.web.valid;
import net.hasor.restful.invoker.InnerRenderData;
import net.hasor.web.Validation;
import net.hasor.web.annotation.Valid;
import net.hasor.web.annotation.ValidBy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
/**
 * 线程安全
 * @version : 2016-08-03
 * @author 赵永春 (zyc@hasor.net)
 */
public class ValidRule {
    private boolean               enable        = false;
    private Map<String, Valid>    paramValidMap = new HashMap<String, Valid>();
    private Map<String, Class<?>> paramTypeMap  = new HashMap<String, Class<?>>();
    //
    public ValidRule(Method targetMethod) {
        // .解析参数
        Annotation[][] paramAnno = targetMethod.getParameterAnnotations();
        Class<?>[] paramType = targetMethod.getParameterTypes();
        for (int paramIndex = 0; paramIndex < paramAnno.length; paramIndex++) {
            Annotation[] annoArrays = paramAnno[paramIndex];
            for (Annotation anno : annoArrays) {
                if (anno == null || !(anno instanceof Valid)) {
                    continue;
                }
                this.paramValidMap.put(String.valueOf(paramIndex), (Valid) anno);
                this.paramTypeMap.put(String.valueOf(paramIndex), paramType[paramIndex]);
            }
        }
        // .是否启用
        this.enable = !this.paramTypeMap.isEmpty();
    }
    //
    public void doValid(InnerRenderData renderData, Object[] resolveParams) {
        //
        if (!this.enable) {
            return;
        }
        //
        for (String paramIndex : this.paramValidMap.keySet()) {
            Valid paramValid = this.paramValidMap.get(paramIndex);
            if (paramValid == null) {
                continue;
            }
            final String validName = paramValid.value();
            Class<?> paramType = this.paramTypeMap.get(paramIndex);
            ValidBy validBy = paramType.getAnnotation(ValidBy.class);
            if (validBy == null) {
                onErrors(renderData, new InnerValidData(validName, "@ValidBy is Undefined."));
                continue;
            }
            Class<? extends Validation>[] validArrays = validBy.value();
            Validation[] validObjects = new Validation[validArrays.length];
            for (int i = 0; i < validArrays.length; i++) {
                validObjects[i] = renderData.getAppContext().getInstance(validArrays[i]);
            }
            valid(renderData, resolveParams[Integer.valueOf(paramIndex)], validName, validObjects);
        }
    }
    private void onErrors(InnerRenderData renderData, InnerValidData newData) {
        if (newData == null) {
            return;
        }
        InnerValidData messages = renderData.getValidData().get(newData.getKey());
        if (messages == null) {
            renderData.getValidData().put(newData.getKey(), newData);
        } else {
            messages.addAll(newData);
        }
    }
    private void valid(final InnerRenderData renderData, Object resolveParam, String validName, Validation[] validObjects) {
        // ---
        for (Validation valid : validObjects) {
            if (valid == null) {
                onErrors(renderData, new InnerValidData(validName, "program is not exist."));
                continue;
            }
            valid.doValidation(validName, resolveParam, new InnerValidErrors(renderData) {
                protected void errors(InnerValidData messages) {
                    onErrors(renderData, messages);
                }
            });
        }
        // ---
    }
}