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
import net.hasor.core.AppContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
/**
 * 验证执行器,线程安全
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
class ValidDefinition {
    private boolean               enable        = false;
    private Map<String, Valid>    paramValidMap = new HashMap<String, Valid>();
    private Map<String, Class<?>> paramTypeMap  = new HashMap<String, Class<?>>();
    //
    public ValidDefinition(Method targetMethod) {
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
    public boolean isEnable() {
        return enable;
    }
    //
    //
    public void doValid(AppContext appContext, ValidInvoker data, Object[] resolveParams) {
        // .启用否
        if (!this.enable) {
            return;
        }
        // .参数校验
        for (String paramIndex : this.paramValidMap.keySet()) {
            doValidParam(appContext, data, paramIndex, resolveParams[Integer.valueOf(paramIndex)]);
        }
    }
    private void doValidParam(AppContext appContext, ValidInvoker data, String paramIndex, Object paramObject) {
        Valid paramValid = this.paramValidMap.get(paramIndex);
        if (paramValid == null) {
            return;
        }
        String sceneName = paramValid.value();
        Class<?> paramType = this.paramTypeMap.get(paramIndex);
        ValidBy validBy = paramType.getAnnotation(ValidBy.class);
        if (validBy == null) {
            data.addError(sceneName, "@ValidBy is Undefined.");
            return;
        }
        for (Class<? extends Validation> validType : validBy.value()) {
            if (validType == null) {
                data.addError(sceneName, "validType is Undefined.");
                continue;
            }
            Validation<Object> validObject = appContext.getInstance(validType);
            if (validObject == null) {
                data.addError(sceneName, "Validation program is not exist. [" + validType + "]");
                continue;
            }
            //
            validObject.doValidation(sceneName, paramObject, data);
        }
        //
    }
}