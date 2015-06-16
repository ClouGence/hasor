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
package net.hasor.mvc.support.caller;
import java.lang.annotation.Annotation;
import java.util.List;
import net.hasor.mvc.Call;
import net.hasor.mvc.CallStrategy;
import net.hasor.mvc.support.ResultDefine;
/**
 * 代理CallStrategy已增加结果处理功能。
 * @version : 2013-5-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class ResultCallStrategy implements CallStrategy {
    private List<ResultDefine> defineList;
    private CallStrategy       parentStrategy;
    //
    public ResultCallStrategy(CallStrategy parentStrategy, List<ResultDefine> defineList) {
        this.defineList = defineList;
        this.parentStrategy = parentStrategy;
    }
    //
    public Object exeCall(Call call) throws Throwable {
        Object returnData = this.parentStrategy.exeCall(call);
        //
        if (this.defineList == null) {
            return returnData;
        }
        Annotation[] annos = call.getAnnotations();
        if (annos == null || annos.length == 0) {
            return returnData;
        }
        //
        for (Annotation atAnno : annos) {
            for (ResultDefine atDefine : this.defineList) {
                if (atDefine.getResultType().isInstance(atAnno) == false) {
                    continue;
                }
                returnData = atDefine.returnData(returnData, call);
            }
        }
        //
        return returnData;
    }
}