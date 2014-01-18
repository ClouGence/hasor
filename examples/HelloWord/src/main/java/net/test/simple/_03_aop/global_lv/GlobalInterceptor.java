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
package net.test.simple._03_aop.global_lv;
import net.hasor.plugins.aop.GlobalAop;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
/**
 *  π”√Õ®≈‰∑˚√Ë ˆ∑∂Œßµƒ»´æ÷¿πΩÿ∆˜°£
 * @version : 2013-8-11
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
@GlobalAop("*net.test.simple._03_aop.global_lv.*")
public class GlobalInterceptor implements MethodInterceptor {
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            System.out.println("before GlobalAop...");
            Object returnData = invocation.proceed();
            System.out.println("after GlobalAop...");
            return returnData;
        } catch (Exception e) {
            System.out.println("throw GlobalAop...");
            throw e;
        }
    }
}