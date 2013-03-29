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
package org.platform.api.services;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.platform.api.safety.Power;
/**
 * 声明该类为一个服务类，该类可以实现{@link IService}接口以得到更多容器支持。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Service {
    /**服务名称或ID，在同一个作用域中可以设置多个不同的名称。*/
    public String[] value();
    /**服务所处的作用域，作用域的目的是分割不同目的或系统的服务。在不同的作用域下服务名可以重复。*/
    public String scope() default "";
    /**服务的初始化是否延期装载，默认true（延期）。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public boolean lazyInit() default true;
    /**用于表示服务对象是否为单例模式。*/
    public boolean singlet() default true;
    /**强制服务的启动是需要依赖其他服务启动之后才能启动，如果配置了该属性则lazyInit的属性配置可能会受到影响。同时如果依赖服务停止运行则该服务也会收到停止运行的信号。*/
    public Class<?>[] startWith() default {};
    /**对服务的描述信息。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public String description() default "";
    /**在管理控制台显示服务时使用displayName属性。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public String displayName() default "";
    /**服务的使用授权范围。默认值：{@link Access#Private}。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public Access access() default Access.Private;
    /** 服务的启动参数。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public InitParam[] initParams() default {};
    /**
     * 公开范围枚举，如果系统启动了对外服务访问策略则可以通过访问通路直接访问到该服务。
     * @version : 2013-3-12
     * @author 赵永春 (zyc@byshell.org)
     */
    public static enum Access {
        /**完全公开。*/
        Public,
        /**
         * 需要通过认证之后才可以使用。如果限制到特定的权限的配置请使用{@link Power}注解进行配置。
         * @see org.platform.faces.safety.Power
         */
        Protected,
        /**只限应用程序内部使用，不设立对外公开。*/
        Private,
    }
}