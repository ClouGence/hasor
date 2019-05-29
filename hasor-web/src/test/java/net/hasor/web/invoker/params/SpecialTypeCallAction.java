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
package net.hasor.web.invoker.params;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Post;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//
@MappingTo("/special_param.do")
public class SpecialTypeCallAction {
    //
    @Post
    public Map<String, Object> execute(HttpServletRequest request, HttpServletResponse response, HttpSession session, Invoker invoker, List listData) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("request", request);
        dataMap.put("response", response);
        dataMap.put("session", session);
        dataMap.put("invoker", invoker);
        dataMap.put("listData", listData);
        return dataMap;
    }
}
