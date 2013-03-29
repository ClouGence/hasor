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
package org.platform.api.upfile;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 上传文件的处理类，需要实现{@link IUpFile}接口。
 * @see org.platform.upfile.IUpFile
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface UpLoadFile {
    /**上传服务初始化参数。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public InitParam[] initParam() default {};
    /**上传服务注册的服务名。
     * 如果配置为空则使用类的简短类名作为名称。*/
    public String[] upName();
    /**上传服务允许的最大请求数据长度。默认：0(无限制)。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public long maxSize() default 0;
    /**上传服务允许的最小请求数据长度。默认：0(无限制)。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public long minSize() default 0;
    /**是否允许一个请求中多个上传实体请求。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public boolean allowMulti() default false;
    /**上传服务允许的文件类型。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public String[] allowFiles() default { "*.*" };
    /**上传服务策略。默认:{@link AccessPolicy#Public}。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public AccessPolicy policy() default AccessPolicy.Public;
    /**
     * 公开范围枚举，如果系统启动了对外服务访问策略则可以通过访问通路直接访问到该服务。
     * @version : 2013-3-12
     * @author 赵永春 (zyc@byshell.org)
     */
    public static enum AccessPolicy {
        /**完全公开。*/
        Public,
        /**需要经过{@link IUpFilePolicy}策略检验。*/
        Policy,
    }
}