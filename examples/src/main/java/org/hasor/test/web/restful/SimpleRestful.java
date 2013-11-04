/*
 * Copyright 2008-2009 the original ÕÔÓÀ´º(zyc@hasor.net).
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
package org.hasor.test.web.restful;
import net.hasor.plugins.aop.Aop;
import net.hasor.plugins.restful.Path;
import net.hasor.plugins.restful.PathParam;
import net.hasor.plugins.restful.RestfulService;
import net.hasor.plugins.result.ext.Redirect;
import org.hasor.test.web.restful.interceptor.TestRestfulInterceptor;
/**
 * 
 * @version : 2013-7-23
 * @author ÕÔÓÀ´º (zyc@hasor.net)
 */
@RestfulService
public class SimpleRestful {
    @Path("/restful/{name}")
    public void sayName(@PathParam("name") String name) {
        System.out.println("sayName form :" + name);
    }
    @Path("/restful/age/{age}")
    @Aop(TestRestfulInterceptor.class)
    public void sayAge(@PathParam("age") String age) {
        System.out.println("sayAge form :" + age);
    }
    //
    //
    @Redirect
    @Path("/restful/to/{target}")
    public String to(@PathParam("target") String target) {
        if (target.equals("sayName")) {
            //
            System.out.println("Ìø×ªµ½:toSayName");
            return "/restful/toSayName";
        } else if (target.equals("sayAge")) {
            //
            System.out.println("Ìø×ªµ½:age=108");
            return "/restful/age/108";
        }
        return "/index.htm";
    }
}