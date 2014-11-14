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
package net.hasor.rsf.runtime.server;
import java.lang.reflect.Method;
import net.hasor.rsf.runtime.RsfFilterChain;
import net.hasor.rsf.runtime.RsfRequest;
import net.hasor.rsf.runtime.RsfResponse;
/**
 * 负责执行Rsf调用，并写入response。
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerRsfFilterChain implements RsfFilterChain {
    private Object targetObj    = null;
    private Method targetMethod = null;
    //
    public InnerRsfFilterChain(Object targetObj, Method targetMethod) {
        this.targetObj = targetObj;
        this.targetMethod = targetMethod;
    }
    //default invoke
    public void doFilter(RsfRequest request, RsfResponse response) throws Throwable {
        if (response.isCommitted() == true)
            return;
        Object[] params = request.getParameterObject();
        Object resData = this.targetMethod.invoke(this.targetObj, params);
        response.sendData(resData);
    }
}