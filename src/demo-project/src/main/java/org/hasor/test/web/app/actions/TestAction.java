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
package org.hasor.test.web.app.actions;
import net.hasor.core.AppContext;
import net.hasor.web.controller.Controller;
import net.hasor.web.controller.HeaderParam;
import net.hasor.web.controller.InjectParam;
import net.hasor.web.controller.Path;
import net.hasor.web.controller.PathParam;
import net.hasor.web.controller.QueryParam;
import net.hasor.web.controller.plugins.result.core.Json;
import net.hasor.web.controller.plugins.result.core.Redirect;
/**
 * 
 * @version : 2013-7-23
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
@Controller("/action")
public class TestAction {
    @Redirect
    public String test(@InjectParam AppContext appContext) {
        String[] strs = appContext.getSettings().getStringArray("framework.loadPackages");
        System.out.println("invoke test" + strs);
        return "/index.htm";
    }
    protected void finalize() throws Throwable {
        // TODO Auto-generated method stub
        super.finalize();
    }
    //
    @Json
    @Path("/user/{uid}/")
    public Object userInfo(@PathParam("uid") String uid, @HeaderParam("User-Agent") String[] userAgent, @QueryParam("age") int age, @QueryParam("ns") String[] ns) {
        System.out.println("hello");
        return new String[] { "abc", "cde" };
    }
}