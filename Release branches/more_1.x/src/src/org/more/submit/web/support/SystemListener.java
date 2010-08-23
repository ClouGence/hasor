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
package org.more.submit.web.support;
import javax.servlet.ServletContext;
import org.more.submit.SubmitContext;
/**
 * more工作在web模式上的时通过参数配置的消息通知器，由于more的启动是延迟的。因此通过这个监听器可以在filter延迟启动more时得到通知。
 * @version 2010-8-15
 * @author 赵永春 (zyc@byshell.org)
 */
public interface SystemListener {
    /**启动时*/
    public void start(ServletContext servletContext, SubmitContext context);
    /**停止时*/
    public void stop(ServletContext servletContext, SubmitContext context);
}