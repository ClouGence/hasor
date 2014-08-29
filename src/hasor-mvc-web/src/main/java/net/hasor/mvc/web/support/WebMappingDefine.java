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
package net.hasor.mvc.web.support;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import net.hasor.core.Hasor;
import net.hasor.mvc.strategy.CallStrategyFactory;
import net.hasor.mvc.support.MappingDefine;
import net.hasor.mvc.web.restful.HttpMethod;
import org.more.util.StringUtils;
/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class WebMappingDefine extends MappingDefine {
    private String[] httpMethod;
    //
    protected WebMappingDefine(String bindID, Method targetMethod, CallStrategyFactory strategyFactory) {
        super(bindID, targetMethod, strategyFactory);
        /*HttpMethod*/
        Annotation[] annos = targetMethod.getAnnotations();
        ArrayList<String> allHttpMethod = new ArrayList<String>();
        if (annos != null) {
            for (Annotation anno : annos) {
                HttpMethod httpMethodAnno = anno.annotationType().getAnnotation(HttpMethod.class);
                if (httpMethodAnno != null) {
                    String bindMethod = httpMethodAnno.value();
                    if (StringUtils.isBlank(bindMethod) == false)
                        allHttpMethod.add(bindMethod);
                }
            }
        }
        if (allHttpMethod.isEmpty())
            allHttpMethod.add("ANY");
        this.httpMethod = allHttpMethod.toArray(new String[allHttpMethod.size()]);
    }
    //
    public String[] getHttpMethod() {
        return this.httpMethod;
    }
    /**判断Restful实例是否支持这个 请求方法。*/
    public boolean matchingMethod(String httpMethod) {
        for (String m : this.httpMethod)
            if (StringUtils.equalsIgnoreCase(httpMethod, m))
                return true;
            else if (StringUtils.equalsIgnoreCase(m, "ANY"))
                return true;
        return false;
    }
    public String toString() {
        return Hasor.logString(httpMethod) + " " + super.toString();
    }
}