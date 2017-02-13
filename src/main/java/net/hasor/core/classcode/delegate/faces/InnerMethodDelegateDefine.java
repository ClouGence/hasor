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
package net.hasor.core.classcode.delegate.faces;
import java.lang.reflect.Method;
/**
 *
 * @version : 2014年9月9日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerMethodDelegateDefine implements MethodDelegate {
    private Class<?>       faces    = null;
    private MethodDelegate delegate = null;
    //
    public InnerMethodDelegateDefine(Class<?> faces, MethodDelegate delegate) {
        this.faces = faces;
        this.delegate = delegate;
    }
    public Class<?> getFaces() {
        return this.faces;
    }
    public Object invoke(Method callMethod, Object target, Object[] params) throws Throwable {
        return this.delegate.invoke(callMethod, target, params);
    }
}