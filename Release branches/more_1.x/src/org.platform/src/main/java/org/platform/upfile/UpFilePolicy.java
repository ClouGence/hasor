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
package org.platform.upfile;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 表示该类为一个上传服务策略处理类，该类需要实现{@link IUpFilePolicy}接口。
 * @version : 2013-3-25
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface UpFilePolicy {
    /**上传策略初始化参数。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public InitParam[] initParam() default {};
    /**UpFilePolicy在策略链上的顺序。默认：0，数字越大启动越靠后。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public int sort() default 0;
    /**上传服务的策略名。*/
    public String name();
    /**需要配置策略的上传服务名。支持正则表达式。*/
    public String[] withService();
}