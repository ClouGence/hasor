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
package net.hasor.land.utils;
import java.math.BigInteger;
/**
 * ID 工具
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class TermUtils {
    /** +1 */
    public static String incrementAndGet(String termID) {
        BigInteger integer = new BigInteger(termID, 16);
        integer = integer.add(BigInteger.valueOf(1L));
        return integer.toString(16);
    }
    /** 第二个比第一个大 */
    public static boolean gtFirst(String termID_1, String termID_2) {
        if (termID_1.equalsIgnoreCase(termID_2))
            return false;
        BigInteger integer_1 = new BigInteger(termID_1, 16);
        BigInteger integer_2 = new BigInteger(termID_2, 16);
        return integer_1.compareTo(integer_2) < 0;
    }
}