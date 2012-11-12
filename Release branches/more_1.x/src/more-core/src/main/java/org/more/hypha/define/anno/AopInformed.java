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
package org.more.hypha.define.anno;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.more.hypha.define.AopPointcutType;
/**
 * 该类的一个aop通知者，通过该注解配置aop的一个具体切入点。
 * @version 2010-10-13
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface AopInformed {
    /**定义该通知上的切入点，该切入点需要使用切入点表达式，默认值是*。*/
    public String pointcut() default "*";
    /**切入点处理bean的名称或id引用。*/
    public String refBean();
    /**切入点类型，默认使用{@link AopPointcutType#Auto}。*/
    public AopPointcutType type() default AopPointcutType.Auto;
}