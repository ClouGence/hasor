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
package net.hasor.mvc.support.result;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.EventListener;
import net.hasor.mvc.ResultProcess;
import net.hasor.mvc.WebCall;
/**
 * 
 * @version : 2014年8月29日
 * @author 赵永春(zyc@hasor.net)
 */
public class ResultProcessDefine implements ResultProcess, EventListener {
    private Class<?>                resultType = null;
    private BindInfo<ResultProcess> bindInfo   = null;
    private ResultProcess           proc       = null;
    //
    public void onEvent(String event, Object[] params) throws Throwable {
        AppContext appContext = (AppContext) params[0];
        this.proc = appContext.getInstance(this.bindInfo);
    }
    public ResultProcessDefine(Class<?> resultType, BindInfo<ResultProcess> bindInfo) {
        this.resultType = resultType;
        this.bindInfo = bindInfo;
    }
    public Class<?> getResultType() {
        return resultType;
    }
    public String toString() {
        return this.resultType.getName() + "-[BindType: " + bindInfo.getBindType() + "]";
    }
    public Object onResult(Object result, WebCall call) throws Throwable {
        return proc.onResult(result, call);
    }
    public Object onThrowable(Throwable throwable, WebCall call) throws Throwable {
        return proc.onResult(throwable, call);
    }
}