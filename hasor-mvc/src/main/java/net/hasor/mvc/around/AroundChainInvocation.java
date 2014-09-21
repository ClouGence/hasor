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
package net.hasor.mvc.around;
import net.hasor.mvc.support.Call;
/**
 * 
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
 */
class AroundChainInvocation implements AroundPoint {
    private final AroundInterceptor[] aroundDefinitions;
    private final AroundPoint         aroundPoint;
    private int                       index = -1;
    // 
    public AroundChainInvocation(AroundInterceptor[] aroundDefinitions, AroundPoint aroundPoint) {
        this.aroundDefinitions = aroundDefinitions;
        this.aroundPoint = aroundPoint;
    }
    @Override
    public Object doCall(Call call) throws Throwable {
        this.index++;
        if (this.index < this.aroundDefinitions.length) {
            return this.aroundDefinitions[this.index].invoke(this);
        } else {
            return this.aroundPoint.doCall(call);
        }
    }
}