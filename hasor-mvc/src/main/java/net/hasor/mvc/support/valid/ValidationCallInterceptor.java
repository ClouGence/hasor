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
package net.hasor.mvc.support.valid;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.mvc.ValidationForm;
import net.hasor.mvc.WebCall;
import net.hasor.mvc.WebCallInterceptor;
import net.hasor.mvc.api.Valid;
import org.more.bizcommon.ResultDO;
import org.more.util.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class ValidationCallInterceptor implements WebCallInterceptor, AppContextAware {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public void setAppContext(AppContext appContext) {
        // TODO Auto-generated method stub
    }
    //
    /** 执行调用 */
    public Object exeCall(Object[] args, WebCall call) throws Throwable {
        HashMap<String, ValidData> validList = new HashMap<String, ValidData>();
        Annotation[][] paramAnno = call.getMethodParamAnnos();
        boolean validResule = false;
        //
        for (int paramIndex = 0; paramIndex < paramAnno.length; paramIndex++) {
            Annotation[] annoArrays = paramAnno[paramIndex];
            for (Annotation anno : annoArrays) {
                if (anno instanceof Valid == false) {
                    continue;
                }
                Valid valid = (Valid) anno;
                Object paramObj = args[paramIndex];
                ValidData data = this.doValidData(paramObj, valid);
                if (data != null) {
                    validList.put(data.getKey(), data);
                }
                break;
            }
        }
        //
        if (validResule == false) {
            return BeanUtils.getDefaultValue(call.getMethod().getReturnType());
        } else {
            return call.call(args);
        }
    }
    //
    /** 执行调用 */
    private ValidData doValidData(Object paramObj, Valid valid) throws Throwable {
        if (paramObj instanceof ValidationForm) {
            ResultDO<String> result = ((ValidationForm) paramObj).doValidation();
            if (result.isSuccess() == false) {
                //                ValidData validData = new ValidData(valid.value());
                //                        valid = false;
                //                        validList.add(result.getResult());
            }
        }
        return null;
    }
}