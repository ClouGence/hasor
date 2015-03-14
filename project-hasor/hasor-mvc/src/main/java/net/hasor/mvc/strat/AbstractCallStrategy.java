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
package net.hasor.mvc.strat;
import net.hasor.mvc.Call;
import net.hasor.mvc.CallStrategy;
/**
 * 
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractCallStrategy implements CallStrategy {
    //
    public Object exeCall(Call call) throws Throwable {
        this.initCall(call);
        Object[] args = this.resolveParams(call);
        return this.returnCallBack(call.call(args), call);
    }
    /**初始化调用。*/
    protected abstract void initCall(Call call);
    /**处理结果 */
    protected Object returnCallBack(Object returnData, Call call) throws Throwable {
        return returnData;
    }
    /**准备参数*/
    protected abstract Object[] resolveParams(Call call) throws Throwable;
}