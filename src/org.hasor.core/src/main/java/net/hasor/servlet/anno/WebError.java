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
package net.hasor.servlet.anno;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 注册一个web处理期间抛出异常的处理接口。
 * @version : 2013-3-20
 * @author 赵永春 (zyc@hasor.net)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface WebError {
    //    /**对服务的描述信息。
    //     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    //    public String description() default "";
    //    /**在管理控制台显示服务时使用displayName属性。
    //     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    //    public String displayName() default "";
    /**在迭代链上的顺序。默认：0，按照升序排序。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public int sort() default 0;
    /** 服务的启动参数。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public WebInitParam[] initParams() default {};
    /**要处理的异常类型。*/
    public Class<? extends Throwable> value();
}