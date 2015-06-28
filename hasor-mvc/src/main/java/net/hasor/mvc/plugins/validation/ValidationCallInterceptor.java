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
package net.hasor.mvc.plugins.validation;
import net.hasor.mvc.WebCall;
import net.hasor.mvc.WebCallInterceptor;
import org.more.bizcommon.ResultDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @version : 2014年8月27日
 * @author 赵永春(zyc@hasor.net)
 */
public class ValidationCallInterceptor implements WebCallInterceptor {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    /** 执行调用 */
    public Object exeCall(WebCall call) throws Throwable {
        Object[] params = call.getArgs();
        if (params != null && params.length != 0) {
            for (Object obj : params) {
                if (obj instanceof Validation) {
                    ResultDO<String> result = ((Validation) obj).doValidation();
                }
            }
        }
        return call.call();
    }
}