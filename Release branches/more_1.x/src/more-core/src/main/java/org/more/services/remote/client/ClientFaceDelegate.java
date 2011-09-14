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
package org.more.services.remote.client;
import java.lang.reflect.Method;
import org.more.core.classcode.MethodDelegate;
import org.more.core.error.InvokeException;
/**
 * 接口附加实现类
 * @version : 2011-8-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class ClientFaceDelegate implements MethodDelegate {
    public Object invoke(Method callMethod, Object target, Object[] params) throws InvokeException {
        ClientRemotePropxy propxy = (ClientRemotePropxy) target;
        Object obj = propxy.getTarget();
        try {
            Method m = obj.getClass().getMethod(callMethod.getName(), callMethod.getParameterTypes());
            return m.invoke(obj, params);
        } catch (Exception e) {
            throw new InvokeException(e.getCause());
        }
    };
};