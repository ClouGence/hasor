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
package net.hasor.mvc.plugins.result;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hasor.mvc.ResultProcess;
import net.hasor.mvc.WebCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
* 
* @version : 2013-6-5
* @author 赵永春 (zyc@hasor.net)
*/
public class ForwordResultProcess implements ResultProcess {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public Object returnData(Object result, WebCall call) throws ServletException, IOException {
        if (result == null) {
            return result;
        }
        HttpServletRequest request = call.getHttpRequest();
        HttpServletResponse response = call.getHttpResponse();
        //
        if (request != null && response != null && response.isCommitted() == false) {
            if (logger.isDebugEnabled()) {
                logger.debug("forword to %s.", result);
            }
            request.getRequestDispatcher(result.toString()).forward(request, response);
        }
        return result;
    }
}