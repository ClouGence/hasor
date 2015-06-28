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
package net.hasor.mvc.support.inner;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.mvc.WebCall;
import net.hasor.mvc.WebCallInterceptor;
/**
 * 代理CallStrategy已增加结果处理功能。
 * @version : 2013-5-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class ResultCallInterceptor implements WebCallInterceptor, AppContextAware {
    private List<ResultProcessDefine> defineList;
    public void setAppContext(AppContext appContext) {
        this.defineList = appContext.findBindingBean(ResultProcessDefine.class);
        if (this.defineList == null) {
            this.defineList = new ArrayList<ResultProcessDefine>();
        }
    }
    //
    public Object exeCall(WebCall call) throws Throwable {
        Object returnData = call.call();
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
            for (ResultProcessDefine atDefine : this.defineList) {
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