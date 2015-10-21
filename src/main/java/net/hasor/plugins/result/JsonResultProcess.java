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
package net.hasor.plugins.result;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import net.hasor.mvc.ResultProcess;
import net.hasor.mvc.WebCall;
import org.more.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class JsonResultProcess implements ResultProcess {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public Object onThrowable(Throwable throwable, WebCall call) throws Throwable {
        throw throwable;
    }
    public Object onResult(Object result, WebCall call) throws Throwable {
        if (result == null) {
            return result;
        }
        HttpServletResponse response = call.getHttpResponse();
        //
        if (response != null && response.isCommitted() == false) {
            if (logger.isDebugEnabled()) {
                logger.debug("json to %s.", result);
            }
            String jsonData = JSON.toString(result);
            PrintWriter pw = response.getWriter();
            pw.write(jsonData);
            pw.flush();
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("no write, response isCommitted!");
            }
        }
        return result;
    }
}