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
import java.lang.reflect.Array;
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
    public static Object[] addToArray(Object[] array, Object element) {
        
        
        return (Object[]) add(array, index, element, Object.class);
    }
    /**
     * Returns a copy of the given array of size 1 greater than the argument.
     * The last value of the array is left to the default value.
     *
     * @param array The array to copy, must not be <code>null</code>.
     * @param newArrayComponentType If <code>array</code> is <code>null</code>, create a
     * size 1 array of this type.
     * @return A new copy of the array of size 1 greater than the input.
     */
    private static Object copyArrayGrow1(Object array, Class newArrayComponentType) {
        if (array != null) {
            int arrayLength = Array.getLength(array);
            Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            System.arraycopy(array, 0, newArray, 0, arrayLength);
            return newArray;
        }
        return Array.newInstance(newArrayComponentType, 1);
    }
    /**
     * Underlying implementation of add(array, index, element) methods.
     * The last parameter is the class, which may not equal element.getClass
     * for primitives.
     *
     * @param array  the array to add the element to, may be <code>null</code>
     * @param index  the position of the new object
     * @param element  the object to add
     * @param clss the type of the element being added
     * @return A new array containing the existing elements and the new element
     */
    private static Object add(Object array, int index, Object element, Class clss) {
        if (array == null) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Length: 0");
            }
            Object joinedArray = Array.newInstance(clss, 1);
            Array.set(joinedArray, 0, element);
            return joinedArray;
        }
        int length = Array.getLength(array);
        if (index > length || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        Object result = Array.newInstance(clss, length + 1);
        System.arraycopy(array, 0, result, 0, index);
        Array.set(result, index, element);
        if (index < length) {
            System.arraycopy(array, index, result, index + 1, length - index);
        }
        return result;
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
    /**判断数组中是否有被判断的元素。*/
    public static <T> boolean contains(T[] arr, T object) {
        if (arr == null)
            return false;
        for (T item : arr)
            if (item == object)
                return true;
        return false;
    }
    /**删除数组中空元素*/
    public static <T> T[] clearNull(T[] arr) {
        ArrayList<T> list = new ArrayList<T>();
        if (arr != null)
            for (T item : arr)
                if (item != null)
                    list.add(item);
        Object[] target = list.toArray();
        return (T[]) Arrays.copyOf(target, target.length, arr.getClass());
    }
}