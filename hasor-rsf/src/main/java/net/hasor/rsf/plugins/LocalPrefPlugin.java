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
package net.hasor.rsf.plugins;
import java.lang.reflect.Method;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.runtime.RsfFilter;
import net.hasor.rsf.runtime.RsfFilterChain;
import net.hasor.rsf.runtime.RsfRequest;
import net.hasor.rsf.runtime.RsfResponse;
/**
 * 优先检查本地是否有服务提供（优先本地服务提供者的调用）。
 * @version : 2014年11月30日
 * @author 赵永春(zyc@hasor.net)
 */
public class LocalPrefPlugin implements RsfFilter {
    public void doFilter(RsfRequest request, RsfResponse response, RsfFilterChain chain) throws Throwable {
        if (request.isLocal() == true) {
            ServiceMetaData metaData = request.getMetaData();
            if (metaData.isProvider() == true) {
                //
                Object bean = request.getContext().getBean(metaData);
                String rMethod = request.getMethod();
                Class<?>[] rParams = request.getParameterTypes();
                Object[] rObjects = request.getParameterObject();
                //
                Method m = bean.getClass().getMethod(rMethod, rParams);
                response.sendData(m.invoke(bean, rObjects));
                //
                return;
            }
        }
        //
        chain.doFilter(request, response);
        return;
    }
}