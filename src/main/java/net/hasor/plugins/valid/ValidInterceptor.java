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
package net.hasor.plugins.valid;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.MethodInvocation;
import org.more.bizcommon.ResultDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
class ValidInterceptor implements MethodInterceptor, AppContextAware {
    private static Logger logger     = LoggerFactory.getLogger(ValidInterceptor.class);
    private AppContext    appContext = null;
    //
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    public AppContext getAppContext() {
        return this.appContext;
    }
    /* 执行调用，每个方法的参数都进行判断，一旦查到参数上具有Valid 标签那么就调用doValid进行参数验证。 */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method targetMethod = invocation.getMethod();
        Annotation[][] paramAnno = targetMethod.getParameterAnnotations();
        Object[] paramArrays = invocation.getArguments();
        //
        for (int paramIndex = 0; paramIndex < paramAnno.length; paramIndex++) {
            Annotation[] annoArrays = paramAnno[paramIndex];
            for (Annotation anno : annoArrays) {
                if (anno == null || anno instanceof Valid == false) {
                    continue;
                }
                Valid valid = (Valid) anno;
                Object paramObj = paramArrays[paramIndex];
                ValidData validData = doValid(valid.value(), paramObj);
                if (validData.isValid() == false) {
                    throw new ValidationException(validData);
                }
                break;
            }
        }
        //
        return invocation.proceed();
    }
    /*对参数进行验证，如果参数对象实现了ValidationForm那么就进行ValidationForm接口验证。否则进一步查找Validation接口进行验证。*/
    public ValidData doValid(String validName, Object paramObj) {
        //1.ValidationForm
        if (paramObj instanceof ValidationForm) {
            ResultDO<String> result = ((ValidationForm) paramObj).doValidation();
            ValidData vData = converResult(ValidationForm.class.getSimpleName(), result);
            return vData;
        }
        //2.查找对应的Validation接口。
        Validation validApp = appContext.findBindingBean(validName, Validation.class);
        //3.调用验证
        if (validApp != null) {
            ResultDO<String> result = validApp.doValidation(paramObj);
            ValidData vData = converResult(validName, result);
            logger.info("doValidation {},result = {}", validName, vData);
            return vData;
        } else {
            ValidData vData = new ValidData(validName, false);
            String msg = "doValidation " + validName + " program is not exist, valid faild.";
            vData.addValidString(msg);
            logger.info(msg);
            return vData;
        }
    }
    /*将ResultDO转换为ValidData*/
    private ValidData converResult(String validName, ResultDO<String> result) {
        ValidData validData = new ValidData(validName, result.isSuccess());
        validData.addValidString(result.getResult());
        validData.addValidMessage(result.getMessageList());
        return validData;
    }
}