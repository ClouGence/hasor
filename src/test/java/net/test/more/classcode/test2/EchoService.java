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
package net.test.more.classcode.test2;
import java.lang.reflect.Method;
import org.more.classcode.aop.InnerChainAopInvocation;
import org.more.util.ExceptionUtils;
/**
 * 远程服务接口。
 * @version : 2015年1月3日
 * @author 赵永春(zyc@hasor.net)
 */
public interface EchoService {
    public String echo(String sayMessage, long i);
}
class EchoServiceImpl implements EchoService {
    public String echo(String sayMessage, long i) {
        Class<?>[] pTypes = new Class<?>[] { String.class, Long.TYPE };
        Object pObjects = new Object[] { sayMessage, Long.valueOf(i) };
        try {
            Method m = this.getClass().getMethod("doCall", pTypes);
            InnerChainAopInvocation chain = null;
            Object obj = null;
            return (String) obj;
        } catch (Throwable e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}