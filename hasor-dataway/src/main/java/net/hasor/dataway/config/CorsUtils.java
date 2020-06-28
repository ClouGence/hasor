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
package net.hasor.dataway.config;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CorsUtils {
    public static void setupInner(Invoker invoker) {
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        HttpServletResponse httpResponse = invoker.getHttpResponse();
        //
        String originString = httpRequest.getHeader("Origin");
        if (StringUtils.isNotBlank(originString)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", originString);
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        } else {
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        }
        httpResponse.addHeader("Access-Control-Allow-Methods", "*");
        httpResponse.addHeader("Access-Control-Allow-Headers", "Content-Type,X-InterfaceUI-Info");
        httpResponse.addHeader("Access-Control-Expose-Headers", "X-InterfaceUI-ContextType");
        httpResponse.addHeader("Access-Control-Max-Age", "3600");
    }

    public static void setup(Invoker invoker) {
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        HttpServletResponse httpResponse = invoker.getHttpResponse();
        //
        String originString = httpRequest.getHeader("Origin");
        if (StringUtils.isNotBlank(originString)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", originString);
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        } else {
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        }
        httpResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
        httpResponse.addHeader("Access-Control-Allow-Headers", StringUtils.join(new String[] {//
                "Origin",           //
                "X-Requested-With", //
                "Content-Type",     //
                "Accept",           //
                "Accept-Encoding",  //
                "Accept-Language",  //
                "Host",             //
                "Referer",          //
                "Connection",       //
                "User-Agent",       //
                "Authorization",    //
                "X-InterfaceUI-Info",//
                //
                "authorization",    //
                "connection",       //
                "sw-useragent",     //
                "sw-version"        //
        }, ","));
        httpResponse.addHeader("Access-Control-Expose-Headers", "X-InterfaceUI-ContextType");
        httpResponse.addHeader("Access-Control-Max-Age", "3600");
    }
}