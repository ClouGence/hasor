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
package org.platform.security;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.platform.Platform;
import org.platform.security.Power.Level;
/**
 * 执行权限检测
 * @version : 2013-4-17
 * @author 赵永春 (zyc@byshell.org)
 */
class SecurityInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //1.获取权限数据
        Power powerAnno = invocation.getMethod().getAnnotation(Power.class);
        if (powerAnno == null)
            powerAnno = invocation.getMethod().getDeclaringClass().getAnnotation(Power.class);
        //2.测试权限
        boolean passPower = true;
        if (Level.PassLogin == powerAnno.level()) {
            passPower = this.doPassLogin(powerAnno, invocation.getMethod());
        } else if (Level.PassPolicy == powerAnno.level()) {
            passPower = this.doPassPolicy(powerAnno, invocation.getMethod());
        } else if (Level.Free == powerAnno.level()) {
            passPower = true;
        }
        //3.执行代码
        if (passPower)
            return invocation.proceed();
        String msg = "has no permission Level=" + powerAnno.level().name() + " Code : " + Platform.logString(powerAnno.value());
        throw new PermissionException(msg, powerAnno);
    }
    //
    //
    private boolean doPassLogin(Power powerAnno, Method method) {
        return SecurityHelper.getAuthSession().isLogin();
    }
    //
    //
    private boolean doPassPolicy(Power powerAnno, Method method) {
        return SecurityHelper.getAuthSession().checkPolicy(powerAnno);
    }
}