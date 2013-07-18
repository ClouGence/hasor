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
package org.more.util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * 数组相关工具
 * @version : 2013-4-12
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class ArrayUtils {
    public static boolean isInclude(Object[] arr, Object target) {
        if (isBlank(arr) == true)
            return false;
        //
        boolean returnData = false;
        for (Object obj : arr) {
            if (obj == null && target == null) {
                returnData = true;
                break;
            }
            //
            if ((obj == null && target != null) || (obj != null && target == null))
                returnData = false;
            //
            returnData = obj.equals(target);
            //
            if (returnData)
                break;
        }
        return returnData;
    }
    public static boolean isBlank(Object[] arr) {
        return arr == null || arr.length == 0;
    }
    public static <T> T[] removeInArray(T[] arr, T object) {
        ArrayList<T> list = new ArrayList<T>();
        if (arr != null)
            for (T item : arr)
                list.add(item);
        if (object != null)
            list.remove(object);
        return (T[]) list.toArray();
    }
    /***/
    public static <T> T[] addToArray(T[] arr, T object) {
        Object[] target = null;
        if (arr == null) {
            target = new Object[1];
            target[0] = object;
        } else {
            target = new Object[arr.length + 1];
            if (arr.length != 0)
                System.arraycopy(arr, 0, target, 0, target.length);
            target[target.length - 1] = object;
        }
        return (T[]) Arrays.copyOf(target, target.length, arr.getClass());
    }
    /***/
    public static <T> List<T> newArrayList(T[] arr, T object) {
        ArrayList<T> list = new ArrayList<T>();
        if (arr != null)
            for (T item : arr)
                list.add(item);
        if (object != null)
            list.add(object);
        return list;
    }
    /***/
    public static <T> boolean contains(T[] arr, T object) {
        if (arr == null)
            return false;
        for (T item : arr)
            if (item == object)
                return true;
        return false;
    }
}