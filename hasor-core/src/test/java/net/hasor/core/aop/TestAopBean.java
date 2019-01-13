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
package net.hasor.core.aop;
import net.hasor.utils.ExceptionUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class TestAopBean extends ConstructorAopBean {
    private static Method[] targetMethod;
    private static Method[] targetMethod2;
    //
    public TestAopBean(byte byteValue, short shortValue, int intValue, long longValue, float floatValue, double doubleValue, boolean booleanValue, char charValue) {
        super(byteValue, shortValue, intValue, longValue, floatValue, doubleValue, booleanValue, charValue);
    }
    //
    public List<?> checkBaseType1(long longValue, double doubleValue) throws IOException {
        try {
            Object[] pObjects = new Object[] { longValue, doubleValue };
            Object obj = new InnerAopInvocation("checkBaseType1", targetMethod[10], targetMethod2[10], this, pObjects).proceed();
            return (List) obj;
        } catch (Throwable e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}