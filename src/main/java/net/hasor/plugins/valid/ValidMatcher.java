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
package net.hasor.plugins.valid;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import net.hasor.core.ApiBinder.Matcher;
/**
 * 匹配方法的参数中是否包含了 Valid 注解。
 * @version : 2015年7月29日
 * @author 赵永春(zyc@hasor.net)
 */
class ValidMatcher implements Matcher<Method> {
    public boolean matches(Method target) {
        Annotation[][] paramAnno = target.getParameterAnnotations();
        for (int paramIndex = 0; paramIndex < paramAnno.length; paramIndex++) {
            Annotation[] annoArrays = paramAnno[paramIndex];
            for (Annotation anno : annoArrays) {
                if (anno != null && anno instanceof Valid) {
                    return true;
                }
            }
        }
        return false;
    }
}