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
package net.hasor.mvc.support;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;
import net.hasor.mvc.ModelController;
/**
 * 
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public interface Call {
    /**参数Keys*/
    public Set<String> getParamKeys();
    /**获取参数*/
    public Object getParam(String key);
    /**目标方法*/
    public Method getMethod();
    /**目标方法参数*/
    public Class<?>[] getParameterTypes();
    /**方法参数注解。*/
    public Annotation[][] getMethodParamAnnos();
    /**方法注解。*/
    public Annotation[] getAnnotations();
    /**映射信息*/
    public MappingInfo getMappingInfo();
    /**目标类*/
    public ModelController getTarget();
    /**执行最终的调用并传入参数。*/
    public Object call(Object... params) throws Throwable;
}