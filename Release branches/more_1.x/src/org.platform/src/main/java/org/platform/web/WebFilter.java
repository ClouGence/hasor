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
package org.platform.web;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.servlet.Filter;
/**
 * 声明一个Filter，该Filter需要实现{@link Filter}接口。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface WebFilter {
    /**对服务的描述信息。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public String description() default "";
    /**在管理控制台显示服务时使用displayName属性。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public String displayName() default "";
    /**Filter在过滤器链上的顺序。默认：0，数字越大启动越延迟。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public int sort() default 0;
    /** 服务的启动参数。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public WebInitParam[] initParams() default {};
    /** 过滤器名称或ID */
    public String filterName() default "";
    /** The small-icon of the filter.
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public String smallIcon() default "";
    /** The large-icon of the filter.
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public String largeIcon() default "";
    /**
     * URL匹配规则，与{@link WebFilter#urlPatterns()}属性表示同样功效。
     * @see org.platform.web.WebFilter#urlPatterns()
     */
    public String[] value() default {};
    /**
     * URL匹配规则，与{@link WebFilter#value()}属性表示同样功效。
     * @see org.platform.web.WebFilter#value()
     */
    public String[] urlPatterns() default {};
}