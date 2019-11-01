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
package net.hasor.test.core.aop.anno;
import net.hasor.core.AopIgnore;
import net.hasor.core.Settings;
import net.hasor.core.exts.aop.Aop;

import java.util.*;

/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
@Aop(ClassAnnoInterceptor.class)
@AopIgnore(ignore = false)
public class AopBean {
    public void doInit(List<String> event) {
        event.add("DO");
    }

    @Aop(MethodAnnoInterceptor.class)
    public List<?> checkBaseType(//
            boolean booleanValue, byte byteValue, short shortValue, //
            int intValue, long longValue, float floatValue, double doubleValue, char charValue) {
        return Arrays.asList(booleanValue, byteValue, shortValue, intValue, longValue, floatValue, doubleValue, charValue);
    }

    @Aop(MethodAnnoInterceptor.class)
    public boolean aBooleanValue(boolean aBooleanValue) {
        return aBooleanValue;
    }

    @Aop(MethodAnnoInterceptor.class)
    public byte aByteValue(byte aByteValue) {
        return aByteValue;
    }

    @Aop(MethodAnnoInterceptor.class)
    public short aShort(short aShortValue) {
        return aShortValue;
    }

    @Aop(MethodAnnoInterceptor.class)
    public int aIntValue(int aIntValue) {
        return aIntValue;
    }

    @Aop(MethodAnnoInterceptor.class)
    public long aLongValue(long aLongValue) {
        return aLongValue;
    }

    @Aop(MethodAnnoInterceptor.class)
    public float aFloatValue(float aFloatValue) {
        return aFloatValue;
    }

    @Aop(MethodAnnoInterceptor.class)
    public double aDoubleValue(double aDoubleValue) {
        return aDoubleValue;
    }

    @Aop(MethodAnnoInterceptor.class)
    public char aCharValue(char aCharValue) {
        return aCharValue;
    }

    //
    //
    @Aop(MethodAnnoInterceptor.class)
    public <T extends List<V>, V extends Settings> Map<String, Object> signatureMethod(T param1, V param2) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("obj1", param1);
        result.put("obj2", param2);
        return result;
    }

    //
    @Aop(MethodAnnoInterceptor.class)
    public Map<String, Object> signatureMethod(List<? super Date> param1, List<? extends Date> param2) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("obj1", param1);
        result.put("obj2", param2);
        return result;
    }
}