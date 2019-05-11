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
package net.hasor.core.exts.boot;
import java.lang.annotation.*;
/**
 * @version : 2018-08-04
 * @author 赵永春 (zyc@hasor.net)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SetupModule {
    /** Hasor 的 AppContext 生成器，用户构建各种不同环境的 AppContext */
    public Class<? extends CreateBuilder> builder() default DefaultCreateBuilder.class;

    /** 主配置文件名 */
    public String config() default "hasor-config.xml";

    /** 阻塞主进程的执行，直到收到一个停止信号为止。*/
    public boolean join() default false;
}