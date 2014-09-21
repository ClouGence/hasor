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
package net.hasor.mvc.result;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Map.Entry;
import net.hasor.mvc.strategy.CallStrategy;
import net.hasor.mvc.support.Call;
/**
 * 代理CallStrategy已增加结果处理功能。
 * @version : 2013-5-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class ResultCallStrategy implements CallStrategy {
    private CallStrategy                 callStrategy     = null;
    private Map<Class<?>, ResultProcess> resultProcessMap = null;
    //
    public ResultCallStrategy(CallStrategy callStrategy, Map<Class<?>, ResultProcess> resultProcessMap) {
        this.callStrategy = callStrategy;
        this.resultProcessMap = resultProcessMap;
    }
    //
    public Object exeCall(Call call) throws Throwable {
        Object data = this.callStrategy.exeCall(call);
        //
        if (this.resultProcessMap == null) {
            return data;
        }
        Annotation[] annos = call.getAnnotations();
        if (annos == null || annos.length == 0) {
            return data;
        }
        //
        for (Annotation atAnno : annos) {
            for (Entry<Class<?>, ResultProcess> atEntry : this.resultProcessMap.entrySet()) {
                if (atEntry.getKey().isInstance(atAnno) == false) {
                    continue;
                }
                ResultProcess process = atEntry.getValue();
                if (process != null) {
                    data = process.returnData(data, call);
                    break;
                }
            }
        }
        //
        return data;
    }
}