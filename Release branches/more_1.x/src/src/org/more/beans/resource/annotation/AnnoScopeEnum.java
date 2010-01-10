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
package org.more.beans.resource.annotation;
/**
 * 该枚举中定义了beans注解引擎会扫描到的注解出现位置。
 * @version 2010-1-10
 * @author 赵永春 (zyc@byshell.org)
 */
public enum AnnoScopeEnum {
    /**类型上的注解。*/
    Anno_Type,
    /**字段上的注解。*/
    Anno_Field,
    /**构造方法上的注解*/
    Anno_Constructor,
    /**普通方法的注解*/
    Anno_Method,
    /**方法参数的注解*/
    Anno_Param,
}