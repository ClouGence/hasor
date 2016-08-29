/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.more.util.reflect;
import org.more.util.ArrayUtils;
import org.more.util.ClassUtils;
import org.more.util.StringUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
/**
 * Contains common code for working with Methods/Constructors, extracted and
 * refactored from <code>MethodUtils</code> when it was imported from Commons
 * BeanUtils.
 *
 * @author Apache Software Foundation
 * @author Steve Cohen
 * @author Matt Benson
 * @since 2.5
 * @version $Id: MemberUtils.java 1057013 2011-01-09 20:04:16Z niallp $
 */
abstract class MemberUtils {
    // TODO extract an interface to implement compareParameterSets(...)?
    private static final int ACCESS_TEST = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;
    private static final Method IS_SYNTHETIC;
    //
    public static float toVersionFloat() {
        int limit = 3;
        String version = null;
        try {
            version = System.getProperty("java.version");
        } catch (SecurityException ex) {
            version = null;
        }
        int[] javaVersions = null;
        if (version == null) {
            javaVersions = ArrayUtils.EMPTY_INT_ARRAY;
        } else {
            String[] strings = StringUtils.split(version, "._- ");
            int[] ints = new int[Math.min(limit, strings.length)];
            int j = 0;
            for (int i = 0; i < strings.length && j < limit; i++) {
                String s = strings[i];
                if (s.length() > 0) {
                    try {
                        ints[j] = Integer.parseInt(s);
                        j++;
                    } catch (Exception e) {
                    }
                }
            }
            if (ints.length > j) {
                int[] newInts = new int[j];
                System.arraycopy(ints, 0, newInts, 0, j);
                ints = newInts;
            }
            javaVersions = ints;
        }
        //
        if (javaVersions == null || javaVersions.length == 0) {
            return 0f;
        }
        if (javaVersions.length == 1) {
            return javaVersions[0];
        }
        StringBuffer builder = new StringBuffer();
        builder.append(javaVersions[0]);
        builder.append('.');
        for (int i = 1; i < javaVersions.length; i++) {
            builder.append(javaVersions[i]);
        }
        try {
            return Float.parseFloat(builder.toString());
        } catch (Exception ex) {
            return 0f;
        }
    }
    static {
        Method isSynthetic = null;
        if (toVersionFloat() >= 1.5f) {
            // cannot call synthetic methods:
            try {
                isSynthetic = Member.class.getMethod("isSynthetic", ArrayUtils.EMPTY_CLASS_ARRAY);
            } catch (Exception e) {
            }
        }
        IS_SYNTHETIC = isSynthetic;
    }

    /** Array of primitive number types ordered by "promotability" */
    private static final Class<?>[] ORDERED_PRIMITIVE_TYPES = {Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE};
    /**
     * XXX Default access superclass workaround
     *
     * When a public class has a default access superclass with public members,
     * these members are accessible. Calling them from compiled code works fine.
     * Unfortunately, on some JVMs, using reflection to invoke these members
     * seems to (wrongly) to prevent access even when the modifer is public.
     * Calling setAccessible(true) solves the problem but will only work from
     * sufficiently privileged code. Better workarounds would be gratefully
     * accepted.
     * @param o the AccessibleObject to set as accessible
     */
    static void setAccessibleWorkaround(final AccessibleObject o) {
        if (o == null || o.isAccessible()) {
            return;
        }
        Member m = (Member) o;
        if (Modifier.isPublic(m.getModifiers()) && MemberUtils.isPackageAccess(m.getDeclaringClass().getModifiers())) {
            try {
                o.setAccessible(true);
            } catch (SecurityException e) {
                // ignore in favor of subsequent IllegalAccessException
            }
        }
    }
    /**
     * Learn whether a given set of modifiers implies package access.
     * @param modifiers to test
     * @return true unless package/protected/private modifier detected
     */
    static boolean isPackageAccess(final int modifiers) {
        return (modifiers & MemberUtils.ACCESS_TEST) == 0;
    }
    /**
     * Check a Member for basic accessibility.
     * @param m Member to check
     * @return true if <code>m</code> is accessible
     */
    static boolean isAccessible(final Member m) {
        return m != null && Modifier.isPublic(m.getModifiers()) && !MemberUtils.isSynthetic(m);
    }
    /**
     * Try to learn whether a given member, on JDK >= 1.5, is synthetic.
     * @param m Member to check
     * @return true if <code>m</code> was introduced by the compiler.
     */
    static boolean isSynthetic(final Member m) {
        if (MemberUtils.IS_SYNTHETIC != null) {
            try {
                return ((Boolean) MemberUtils.IS_SYNTHETIC.invoke(m)).booleanValue();
            } catch (Exception e) {
            }
        }
        return false;
    }
    /**
     * Compare the relative fitness of two sets of parameter types in terms of
     * matching a third set of runtime parameter types, such that a list ordered
     * by the results of the comparison would return the best match first
     * (least).
     *
     * @param left the "left" parameter set
     * @param right the "right" parameter set
     * @param actual the runtime parameter types to match against
     * <code>left</code>/<code>right</code>
     * @return int consistent with <code>compare</code> semantics
     */
    static int compareParameterTypes(final Class<?>[] left, final Class<?>[] right, final Class<?>[] actual) {
        float leftCost = MemberUtils.getTotalTransformationCost(actual, left);
        float rightCost = MemberUtils.getTotalTransformationCost(actual, right);
        return leftCost < rightCost ? -1 : rightCost < leftCost ? 1 : 0;
    }
    /**
     * Returns the sum of the object transformation cost for each class in the
     * source argument list.
     * @param srcArgs The source arguments
     * @param destArgs The destination arguments
     * @return The total transformation cost
     */
    private static float getTotalTransformationCost(final Class<?>[] srcArgs, final Class<?>[] destArgs) {
        float totalCost = 0.0f;
        for (int i = 0; i < srcArgs.length; i++) {
            Class<?> srcClass, destClass;
            srcClass = srcArgs[i];
            destClass = destArgs[i];
            totalCost += MemberUtils.getObjectTransformationCost(srcClass, destClass);
        }
        return totalCost;
    }
    /**
     * Gets the number of steps required needed to turn the source class into
     * the destination class. This represents the number of steps in the object
     * hierarchy graph.
     * @param srcClass The source class
     * @param destClass The destination class
     * @return The cost of transforming an object
     */
    private static float getObjectTransformationCost(Class<?> srcClass, final Class<?> destClass) {
        if (destClass.isPrimitive()) {
            return MemberUtils.getPrimitivePromotionCost(srcClass, destClass);
        }
        float cost = 0.0f;
        while (srcClass != null && !destClass.equals(srcClass)) {
            if (destClass.isInterface() && ClassUtils.isAssignable(srcClass, destClass)) {
                // slight penalty for interface match.
                // we still want an exact match to override an interface match,
                // but
                // an interface match should override anything where we have to
                // get a superclass.
                cost += 0.25f;
                break;
            }
            cost++;
            srcClass = srcClass.getSuperclass();
        }
        /*
         * If the destination class is null, we've travelled all the way up to
         * an Object match. We'll penalize this by adding 1.5 to the cost.
         */
        if (srcClass == null) {
            cost += 1.5f;
        }
        return cost;
    }
    /**
     * Get the number of steps required to promote a primitive number to another
     * type.
     * @param srcClass the (primitive) source class
     * @param destClass the (primitive) destination class
     * @return The cost of promoting the primitive
     */
    private static float getPrimitivePromotionCost(final Class<?> srcClass, final Class<?> destClass) {
        float cost = 0.0f;
        Class<?> cls = srcClass;
        if (!cls.isPrimitive()) {
            // slight unwrapping penalty
            cost += 0.1f;
            cls = ClassUtils.wrapperToPrimitive(cls);
        }
        for (int i = 0; cls != destClass && i < MemberUtils.ORDERED_PRIMITIVE_TYPES.length; i++) {
            if (cls == MemberUtils.ORDERED_PRIMITIVE_TYPES[i]) {
                cost += 0.1f;
                if (i < MemberUtils.ORDERED_PRIMITIVE_TYPES.length - 1) {
                    cls = MemberUtils.ORDERED_PRIMITIVE_TYPES[i + 1];
                }
            }
        }
        return cost;
    }
}