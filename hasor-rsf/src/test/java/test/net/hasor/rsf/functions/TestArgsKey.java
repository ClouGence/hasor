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
package test.net.hasor.rsf.functions;
import net.hasor.rsf.address.route.rule.DefaultArgsKey;
/**
 * 对指定的服务上的方法进行处理，sayEcho，testUserTag。两个方法上用于计算路由地址的参考参数是第一个传入参数。
 * @version : 2015年4月16日
 * @author 赵永春(zyc@hasor.net)
 */
public class TestArgsKey extends DefaultArgsKey {
    public String eval(String serviceID, String methodName, Object[] args) {
        if (serviceID.equals("[RSF]test.net.hasor.rsf.services.EchoService-1.0.0")) {
            /*  */
            if (methodName.equals("sayEcho")) {
                return args[0].toString();
            } else if (methodName.equals("testUserTag")) {
                return args[0].toString();
            }
        }
        return super.eval(serviceID, methodName, args);
    }
}