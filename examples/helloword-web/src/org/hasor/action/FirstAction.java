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
package org.hasor.action;
import org.hasor.mvc.controller.Controller;
import org.hasor.mvc.controller.HeaderParam;
import org.hasor.mvc.controller.Path;
import org.hasor.mvc.controller.PathParam;
import org.hasor.mvc.controller.QueryParam;
/**
 * 
 * @version : 2013-8-23
 * @author 赵永春(zyc@hasor.net)
 */
@Controller("/abc/123")
//命名空间
public class FirstAction {
    /*print是action名字*/
    public void print() {
        System.out.println("Hello Action!");
    }
    @Path("/user/{uid}/")
    public void userInfo(@PathParam("uid") String uid,//@Path中声明的参数。
            @HeaderParam("User-Agent") String userAgent,//Heade请求头
            @QueryParam("age") int age,//请求地址“?”之后的参数。
            @QueryParam("ns") String[] ns) {//同名参数数组
        //
        System.out.println(String.format("user %s age=%s by:%s", uid, age, userAgent));
    }
}