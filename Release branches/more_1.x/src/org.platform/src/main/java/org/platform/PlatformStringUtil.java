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
package org.platform;
/**
 * 
 * @version : 2013-5-16
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public abstract class PlatformStringUtil {
    public static String getIndexStr(int index) {
        if (index == Integer.MIN_VALUE)
            return "Min";
        else if (index == Integer.MIN_VALUE + 1)
            return "Min+1";
        else if (index == Integer.MIN_VALUE + 2)
            return "Min+2";
        else if (index == Integer.MIN_VALUE + 3)
            return "Min+3";
        else if (index == Integer.MIN_VALUE + 4)
            return "Min+4";
        else if (index == Integer.MIN_VALUE + 5)
            return "Min+5";
        else if (index == Integer.MIN_VALUE + 6)
            return "Min+6";
        else if (index == Integer.MIN_VALUE + 7)
            return "Min+7";
        else if (index == Integer.MIN_VALUE + 8)
            return "Min+8";
        else if (index == Integer.MIN_VALUE + 9)
            return "Min+9";
        else if (index == Integer.MAX_VALUE)
            return "Max";
        else if (index == Integer.MAX_VALUE - 1)
            return "Max-1";
        else if (index == Integer.MAX_VALUE - 2)
            return "Max-2";
        else if (index == Integer.MAX_VALUE - 3)
            return "Max-3";
        else if (index == Integer.MAX_VALUE - 4)
            return "Max-4";
        else if (index == Integer.MAX_VALUE - 5)
            return "Max-5";
        else if (index == Integer.MAX_VALUE - 6)
            return "Max-6";
        else if (index == Integer.MAX_VALUE - 7)
            return "Max-7";
        else if (index == Integer.MAX_VALUE - 8)
            return "Max-8";
        else if (index == Integer.MAX_VALUE - 9)
            return "Max-9";
        else
            return String.valueOf(index);
    }
}