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
package net.hasor.plugins.servlet3;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.servlet.http.HttpServlet;
/**
 * 声明一个Servlet，该Servlet需要继承{@link HttpServlet}类。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@hasor.net)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface WebServlet {
    /**Servlet在过滤器链上的顺序。默认：0，数字越大启动越延迟。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public int loadOnStartup() default 0;
    /** 服务的启动参数。
     * <br/><b>注：</b><i>该值可以通过管理控制台中重新设置。</i>*/
    public WebInitParam[] initParams() default {};
    /**URL匹配规则。*/
    public String[] value();
    /**URL规则是否使用正则表达式格式书写的*/
    public boolean regex() default false;
    /**Servlet名称*/
    public String servletName() default "";
}