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
package org.more.submit.acs;
import org.more.core.error.LoadException;
import org.more.hypha.ApplicationContext;
import org.more.hypha.anno.define.Bean;
import org.more.submit.ActionContext;
import org.more.submit.ActionInvoke;
/**
 * 该类扩展的{@link AC_Simple}类，如果配置了{@link Bean}子该类会自动解析bean名。
 * 当hypha使用的是xml配置的bean时候，可以使用{@link HBean}注解来指定bean名。
 * 但当两个注解都没有指定时候，使用{@link AC_Simple}的自有创建模式创建。
 * @version : 2011-7-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class AC_Hypha implements ActionContext {
    public ApplicationContext application = null;
    //
    public AC_Hypha(ApplicationContext application) {
        this.application = application;
    }
    @Override
    public ActionInvoke getActionInvoke(String classKey, String methodKey) throws Throwable {
        Object obj = this.application.getBean(classKey);
        if (obj == null)
            throw new LoadException("装载action对象异常。");
        return new DefaultActionInvoke(obj, methodKey);
    }
};