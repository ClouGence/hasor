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
package net.hasor.web.restful.support;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import net.hasor.core.AppContext;
import net.hasor.web.restful.HttpMethod;
import net.hasor.web.restful.Path;
import net.hasor.web.restful.Produces;
import org.more.UndefinedException;
import org.more.classcode.FormatException;
import org.more.util.StringUtils;
/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class RestfulInvokeDefine /*implements Provider<RestfulInvoke>*/{
    private String[]   httpMethod;
    private String     restfulMapping;
    private String     restfulMappingMatches;
    private String     produces;
    private Method     targetMethod;
    private Class<?>   targetClass;
    private AppContext appContext;
    //
    //
    //
    protected RestfulInvokeDefine(AppContext appContext, Method targetMethod) {
        Path pathAnno = targetMethod.getAnnotation(Path.class);
        if (pathAnno == null)
            throw new UndefinedException("is not a valid Restful Service.");
        String servicePath = pathAnno.value();
        if (StringUtils.isBlank(servicePath))
            throw new NullPointerException("Service path is empty.");
        if (!servicePath.matches("/.+"))
            throw new FormatException("Service path format error");
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
        //
        Produces produces = targetMethod.getAnnotation(Produces.class);
        if (produces != null)
            this.produces = produces.value();
        //
        this.restfulMapping = servicePath;
        this.targetMethod = targetMethod;
        this.targetClass = targetMethod.getDeclaringClass();
        this.appContext = appContext;
    }
    //
    //
    //
    /**获取AppContext*/
    public AppContext getAppContext() {
        return this.appContext;
    }
    /**获取目标方法*/
    public Method getTargetMethod() {
        return this.targetMethod;
    }
    /**获取目标类*/
    public Class<?> getTargetClass() {
        return targetClass;
    }
    /**获取映射字符串*/
    public String getRestfulMapping() {
        return this.restfulMapping;
    }
    /**产品，用来定义响应类型*/
    public String getProduces() {
        return this.produces;
    }
    /**获取映射字符串用于匹配的表达式字符串*/
    public String getRestfulMappingMatches() {
        if (this.restfulMappingMatches == null)
            this.restfulMappingMatches = this.restfulMapping.replaceAll("\\{\\w{1,}\\}", "([^/]{1,})");
        return this.restfulMappingMatches;
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
    //
    //
    //
    public RestfulInvoke createIvnoke() {
        return new RestfulInvoke(this);
    }
}