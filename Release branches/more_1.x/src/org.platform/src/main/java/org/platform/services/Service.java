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
package org.platform.services;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.google.inject.ScopeAnnotation;
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
    /**服务的初始化是否延期装载，默认true（延期）。*/
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
    /** 服务的启动参数。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public InitParam[] initParams() default {};
    /**服务所处级别。默认值：{@link Level#Standard}。*/
    public Level level() default Level.Standard;
    /**
     * 服务所处级别。
     * @version : 2013-3-12
     * @author 赵永春 (zyc@byshell.org)
     */
    public static enum Level {
        /**跟随框架启动而启动，如果标记为Framework级别则在Listener启动阶段服务就会被启动。*/
        Framework,
        /**标准级别，该类服务通会在首次初始化Filter时候启动。*/
        Standard,
        /**附属品级别，该类服务会在初始化完Standard级别之后进行初始化，该类服务还有一个特点它的启动和销毁如果出现失败只会以一条警告消息通知到控制台。*/
        Gift,
    }
}