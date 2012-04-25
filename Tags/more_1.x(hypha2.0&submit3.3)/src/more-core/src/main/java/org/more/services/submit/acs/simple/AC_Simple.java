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
package org.more.services.submit.acs.simple;
import java.lang.reflect.Method;
import org.more.services.submit.impl.AbstractAC;
/**
 * 简单的AC实现，该AC会直接创建class对象。
 * @version : 2011-7-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class AC_Simple extends AbstractAC {
    public ClassLoader classLoader = null;
    //
    /**创建类型所指定的对象。*/
    protected Object getBean(Method actionPath, String queryInfo) throws Throwable {
        return actionPath.getDeclaringClass().newInstance();
    }
};
