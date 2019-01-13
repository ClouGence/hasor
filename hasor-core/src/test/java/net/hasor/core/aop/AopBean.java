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
import net.hasor.core.AopIgnore;
import net.hasor.core.Settings;
import net.hasor.core.aop.interceptor.AopBeanInterceptor;
import net.hasor.core.aop.interceptor.CheckBaseType0Interceptor;
import net.hasor.core.aop.interceptor.TransparentInterceptor;
import net.hasor.core.exts.aop.Aop;

import java.util.*;
/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
@Aop(AopBeanInterceptor.class)
@AopIgnore(ignore = false)
public class AopBean {
    //
    public void doInit(List<String> event) {
        event.add("DO");
    }
    //
    //
    @Aop(CheckBaseType0Interceptor.class)
    public List<?> checkBaseType0(//
            boolean booleanValue, byte byteValue, short shortValue, //
            int intValue, long longValue, float floatValue, double doubleValue, char charValue) {
        //
        return Arrays.asList(booleanValue, byteValue, shortValue, intValue, longValue, floatValue, doubleValue, charValue);
    }
    //
    //
    @Aop(TransparentInterceptor.class)
    public boolean aBooleanValue(boolean aBooleanValue) {
        return aBooleanValue;
    }
    @Aop(TransparentInterceptor.class)
    public byte aByteValue(byte aByteValue) {
        return aByteValue;
    }
    @Aop(TransparentInterceptor.class)
    public short aShort(short aShortValue) {
        return aShortValue;
    }
    @Aop(TransparentInterceptor.class)
    public int aIntValue(int aIntValue) {
        return aIntValue;
    }
    @Aop(TransparentInterceptor.class)
    public long aLongValue(long aLongValue) {
        return aLongValue;
    }
    @Aop(TransparentInterceptor.class)
    public float aFloatValue(float aFloatValue) {
        return aFloatValue;
    }
    @Aop(TransparentInterceptor.class)
    public double aDoubleValue(double aDoubleValue) {
        return aDoubleValue;
    }
    @Aop(TransparentInterceptor.class)
    public char aCharValue(char aCharValue) {
        return aCharValue;
    }
    //
    //
    @Aop(TransparentInterceptor.class)
    public <T extends List<V>, V extends Settings> Map<String, Object> signatureMethod(T param1, V param2) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("obj1", param1);
        result.put("obj2", param2);
        return result;
    }
    //
    @Aop(TransparentInterceptor.class)
    public Map<String, Object> signatureMethod(List<? super Date> param1, List<? extends Date> param2) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("obj1", param1);
        result.put("obj2", param2);
        return result;
    }
}