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
package net.hasor.mvc.strategy;
import net.hasor.core.ApiBinder;
import net.hasor.mvc.result.ResultCallStrategyFactory;
/**
 * 
 * @version : 2014年8月29日
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultCallStrategyFactory extends SimpleCallStrategyFactory {
    private CallStrategyFactory resultStrategyFactory = null;
    //
    public DefaultCallStrategyFactory(ApiBinder apiBinder) {
        //1.ResultDefine
        this.resultStrategyFactory = new ResultCallStrategyFactory(apiBinder);
    }
    public CallStrategy createStrategy(CallStrategy parentCall) {
        //1.默认情况。
        parentCall = super.createStrategy(parentCall);
        //2.Around
        //      parentCall = this.resultStrategyFactory.createStrategy(parentCall);
        //3.Result
        parentCall = this.resultStrategyFactory.createStrategy(parentCall);
        //4.返回最终的
        return parentCall;
    }
}