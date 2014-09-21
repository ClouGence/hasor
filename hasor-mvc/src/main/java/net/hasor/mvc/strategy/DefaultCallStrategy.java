/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.mvc.strategy;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import net.hasor.mvc.Param;
import net.hasor.mvc.support.Call;
import org.more.convert.ConverterUtils;
import org.more.util.BeanUtils;
/**
 * 
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultCallStrategy extends AbstractCallStrategy {
    /**准备参数*/
    protected Object[] resolveParams(Call call) throws Throwable {
        Method targetMethod = call.getMethod();
        //
        Class<?>[] targetParamClass = call.getParameterTypes();
        Annotation[][] targetParamAnno = call.getMethodParamAnnos();
        targetParamClass = (targetParamClass == null) ? new Class<?>[0] : targetParamClass;
        targetParamAnno = (targetParamAnno == null) ? new Annotation[0][0] : targetParamAnno;
        ArrayList<Object> paramsArray = new ArrayList<Object>();
        /*准备参数*/
        for (int i = 0; i < targetParamClass.length; i++) {
            Class<?> paramClass = targetParamClass[i];
            Object paramObject = this.resolveParam(paramClass, targetParamAnno[i], call);//获取参数
            /*获取到的参数需要做一个类型转换，以防止method.invoke时发生异常。*/
            if (paramObject == null) {
                paramObject = BeanUtils.getDefaultValue(paramClass);
            } else {
                paramObject = ConverterUtils.convert(paramClass, paramObject);
            }
            paramsArray.add(paramObject);
        }
        Object[] invokeParams = paramsArray.toArray();
        return invokeParams;
    }
    /**准备参数
     * @param call */
    protected Object resolveParam(Class<?> paramClass, Annotation[] paramAnno, Call call) {
        for (Annotation pAnno : paramAnno) {
            Object finalValue = resolveParam(paramClass, pAnno, call);
            if (finalValue != null) {
                return finalValue;
            }
        }
        return BeanUtils.getDefaultValue(paramClass);
    }
    /**/
    protected Object resolveParam(Class<?> paramClass, Annotation pAnno, Call call) {
        if (pAnno instanceof Param == false) {
            return null;
        }
        Param param = (Param) pAnno;
        String paramName = param.value();
        return call.getParam(paramName);
    }
}