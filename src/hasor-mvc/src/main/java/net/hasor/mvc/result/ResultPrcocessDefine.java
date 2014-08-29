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
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.BindInfo;
import net.hasor.mvc.support.Call;
/**
 * 
 * @version : 2014年8月29日
 * @author 赵永春(zyc@hasor.net)
 */
class ResultPrcocessDefine implements ResultProcess, AppContextAware {
    private Class<?>                resultType = null;
    private BindInfo<ResultProcess> bindInfo   = null;
    private AppContext              appContext = null;
    //
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    public ResultPrcocessDefine(Class<?> resultType, BindInfo<ResultProcess> bindInfo) {
        this.resultType = resultType;
        this.bindInfo = bindInfo;
    }
    public Class<?> getResultType() {
        return resultType;
    }
    public Object returnData(Object returnData, Call call) throws Throwable {
        ResultProcess exe = this.appContext.getInstance(this.bindInfo);
        return exe.returnData(returnData, call);
    }
    public String toString() {
        return this.resultType.getName() + "-[BindType: " + bindInfo.getBindType() + "]";
    }
}