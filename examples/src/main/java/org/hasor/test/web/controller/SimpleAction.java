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
package org.hasor.test.web.controller;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.hasor.plugins.aop.Aop;
import net.hasor.plugins.controller.AbstractController;
import net.hasor.plugins.controller.Controller;
import net.hasor.plugins.result.ext.Forword;
import net.hasor.plugins.result.ext.Json;
import net.hasor.plugins.result.ext.Redirect;
import org.hasor.test.web.controller.interceptor.TestControllerInterceptor;
/**
 * 
 * @version : 2013-7-23
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
@Controller("/action")
public class SimpleAction extends AbstractController {
    @Aop(TestControllerInterceptor.class)
    public void sayHallo() {
        System.out.println("Hallo Word form First Action.");
    }
    //
    @Json
    public Map<String, Object> json() {
        HashMap<String, Object> returnData = new HashMap<String, Object>();
        returnData.put("data1", true);
        returnData.put("data2", 123);
        returnData.put("data3", "Data");
        returnData.put("data4", new Date());
        returnData.put("data5", 456.3);
        return returnData;
    }
    //
    @Forword
    public String forwordTo() {
        return "/index.htm";
    }
    @Redirect
    public String redirectTo() {
        return "http://www.baidu.com";
    }
}