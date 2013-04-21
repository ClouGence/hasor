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
package org.platform.icache;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 声明一个缓存中用于生成key的服务提供者，标记了该接口的类必须要求实现{@link IKeyBuilder}接口。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface KeyBuilder {
    /**对服务的描述信息。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public String description() default "";
    /**在管理控制台显示服务时使用displayName属性。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public String displayName() default "";
    /** 服务的启动参数。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public InitParam[] initParams() default {};
    /**该生称器可以作用的数据类型*/
    public Class<?> value();
}