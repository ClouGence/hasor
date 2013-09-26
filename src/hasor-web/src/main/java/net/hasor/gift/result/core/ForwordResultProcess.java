/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.hasor.gift.result.core;
import java.io.IOException;
import java.lang.annotation.Annotation;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hasor.Hasor;
import net.hasor.gift.result.ResultDefine;
import net.hasor.gift.result.ResultProcess;
/**
* 
* @version : 2013-6-5
* @author ’‘”¿¥∫ (zyc@hasor.net)
*/
@ResultDefine(Forword.class)
public class ForwordResultProcess implements ResultProcess {
    public void process(HttpServletRequest request, HttpServletResponse response, Annotation annoData, Object result) throws ServletException, IOException {
        Hasor.debug("forword to %s.", result);
        request.getRequestDispatcher(result.toString()).forward(request, response);
    }
}