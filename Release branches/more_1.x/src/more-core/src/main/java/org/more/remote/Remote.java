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
package org.more.remote;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 标记该类被曝露到RMI服务中，当RMI服务启动时该类可以被其他机器上的RMI服务访问到。
 * @version : 2011-8-15
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Remote {
    /**发布的名称，最终客户端在使用该RMI服务时会通过服务器地址在加上这个名称来定位，但如果指定了forPublisher属性则是另外一种情况。*/
    public String name();
    /**标记的当前远程对象的所属Bean ID。*/
    public String refBeanID() default "";
    /**为该RMI服务提供一个指定的发布者，{@link RemoteService}可以通过发布者来修改发布的基地址，端口以及绑定的IP等信息。*/
    public String forPublisher() default "";
    /**表示服务被曝露出去之后使用的接口。*/
    public Class<? extends java.rmi.Remote>[] faces() default {};
}