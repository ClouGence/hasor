/*
 * Copyright 2008-2009 the original author or authors.
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
package org.hasor.web.controller.support.result;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hasor.Hasor;
import org.hasor.web.controller.ResultDefine;
import org.hasor.web.controller.ResultProcess;
import org.more.util.StringConvertUtils;
import com.alibaba.fastjson.JSON;
/**
 * 
 * @version : 2013-6-5
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
@ResultDefine(Result.class)
public class DefaultResultProcess implements ResultProcess {
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response, Annotation annoData, Object result) throws ServletException, IOException {
        Result resultData = (Result) annoData;
        switch (resultData.value()) {
        case Forword:
            Hasor.debug("forword to %s.", result);
            request.getRequestDispatcher(result.toString()).forward(request, response);
            break;
        case Redirect:
            Hasor.debug("redirect to %s.", result);
            response.sendRedirect(result.toString());
            break;
        case Include:
            Hasor.debug("include %s.", result);
            request.getRequestDispatcher(result.toString()).include(request, response);
            break;
        case Json:
            String jsonData = JSON.toJSONString(result);
            Hasor.debug("write json %s.", jsonData.length() > 300 ? jsonData.substring(0, 300) : jsonData);
            if (response.isCommitted() == false)
                response.getWriter().write(jsonData);
            break;
        case State:
            if (response.isCommitted() == true)
                return;
            Matcher m = Pattern.compile("(\\d+) (.*)").matcher(result.toString());
            if (m.find() == false) {
                response.sendError(500, "get message error .");
            } else {
                response.sendError(StringConvertUtils.parseInt(m.group(1), 500), m.group(2));
            }
            break;
        case None:
            break;
        }
    }
}