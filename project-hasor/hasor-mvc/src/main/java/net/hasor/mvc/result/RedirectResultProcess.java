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
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import net.hasor.mvc.Call;
import net.hasor.mvc.ResultProcess;
import org.more.logger.LoggerHelper;
/**
 * 
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class RedirectResultProcess implements ResultProcess {
    public Object returnData(Object result, Call call) throws ServletException, IOException {
        if (result == null) {
            return result;
        }
        HttpServletResponse response = call.getHttpResponse();
        //
        if (response.isCommitted() == false) {
            LoggerHelper.logFine("redirect to %s.", result);
            response.sendRedirect(result.toString());
        }
        LoggerHelper.logFine("no redirect, response isCommitted!");
        return result;
    }
}