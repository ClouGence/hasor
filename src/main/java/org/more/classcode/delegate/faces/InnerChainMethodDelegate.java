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
package org.more.classcode.delegate.faces;
import org.more.classcode.AbstractClassConfig;
import org.more.classcode.MoreClassLoader;

import java.lang.reflect.Method;
/**
 *
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class InnerChainMethodDelegate implements MethodDelegate {
    private MethodDelegate methodDelegate = null;
    //
    public InnerChainMethodDelegate(String className, String delegateClassName, ClassLoader loader) {
        if (loader instanceof MoreClassLoader) {
            AbstractClassConfig cc = ((MoreClassLoader) loader).findClassConfig(className);
            if (cc != null && cc instanceof MethodClassConfig) {
                MethodClassConfig methodCC = (MethodClassConfig) cc;
                this.methodDelegate = methodCC.getMethodDelegate(delegateClassName);
            }
        }
        //
        if (this.methodDelegate == null) {
            throw new UnsupportedOperationException("not implemented.");
        }
    }
    //
    public Object invoke(Method callMethod, Object target, Object[] params) throws Throwable {
        return this.methodDelegate.invoke(callMethod, target, params);
    }
}