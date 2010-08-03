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
package org.more.submit.ext.filter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 声明当前类是一个Submit过滤器，参数isPublic可以指定这个过滤器是否为一个全局过滤器(如果配置了Filter注解那么配置文件的中的isPublic配置将会失效)。<br/>
 * 注意：当前类必须实现{@link ActionFilter}接口Filter注解才会有效。不仅如此如果该类没有实现
 * {@link ActionFilter}接口那么即使是配置文件方式也将无效。
 * @version : 2010-8-2
 * @author 赵永春(zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
public @interface Filter {
    /**表明当前这个Submit过滤器是否是一个全局过滤器(如果配置了Filter注解那么配置文件的中的isPublic配置将会失效)。*/
    public boolean isPublic() default false;
}