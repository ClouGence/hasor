/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.hasor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @version : 2013-4-3
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
public abstract class Hasor {
    private static String callerInfo() {
        StackTraceElement[] stackElements = new Exception().getStackTrace();
        StackTraceElement onCode = stackElements[2];
        String callerClass = onCode.getClassName();
        return callerClass.substring(callerClass.lastIndexOf(".") + 1) + ":" + onCode.getMethodName();
    }
    private static String callerWarn() {
        StackTraceElement[] stackElements = new Exception().getStackTrace();
        StackTraceElement onCode = stackElements[2];
        String callerClass = onCode.getClassName();
        return callerClass.substring(callerClass.lastIndexOf(".") + 1) + ":" + onCode.getMethodName();
    }
    private static String callerErr() {
        StackTraceElement[] stackElements = new Exception().getStackTrace();
        StackTraceElement onCode = stackElements[2];
        //String callerClass = onCode.getClassName();
        return onCode.getFileName() + ":" + onCode.getLineNumber() + " - " + onCode.getMethodName();
    }
    private static Class<?> callerClass() {
        StackTraceElement[] stackElements = new Exception().getStackTrace();
        StackTraceElement onCode = stackElements[2];
        try {
            return Class.forName(onCode.getClassName());
        } catch (Exception e) {
            return Hasor.class;
        }
    }
    //
    //
    /***/
    public static void debug(String string, Object... params) {
        Logger log = LoggerFactory.getLogger(callerClass());
        Object[] paramsStr = getStringArray(params);
        log.debug(callerInfo() + " ->> " + String.format(string, paramsStr));
    }
    //
    /***/
    public static void error(String string, Object... params) {
        Logger log = LoggerFactory.getLogger(callerClass());
        Object[] paramsStr = getStringArray(params);
        log.error(callerErr() + " ->> " + String.format(string, paramsStr));
    }
    //
    /***/
    public static void warning(String string, Object... params) {
        Logger log = LoggerFactory.getLogger(callerClass());
        Object[] paramsStr = getStringArray(params);
        log.warn(callerWarn() + " ->> " + String.format(string, paramsStr));
    }
    //
    /***/
    public static void info(String string, Object... params) {
        Logger log = LoggerFactory.getLogger(callerClass());
        Object[] paramsStr = getStringArray(params);
        log.info(callerInfo() + " ->> " + String.format(string, paramsStr));
    }
    //
    /***/
    public static String[] getStringArray(Object... objects) {
        ArrayList<String> returnData = new ArrayList<String>();
        for (Object obj : objects) {
            if (obj == null)
                returnData.add("null");
            else
                returnData.add(logString(obj));
        }
        return returnData.toArray(new String[returnData.size()]);
    }
    //
    /***/
    public static String getIndexStr(int index) {
        int allRange = 1000;
        /*-----------------------------------------*/
        int minStartIndex = Integer.MIN_VALUE;
        int minStopIndex = Integer.MIN_VALUE + allRange;
        for (int i = minStartIndex; i < minStopIndex; i++) {
            if (index == i)
                return "Min" + ((index == Integer.MIN_VALUE) ? "" : ("+" + String.valueOf(i + Math.abs(Integer.MIN_VALUE))));
        }
        int maxStartIndex = Integer.MAX_VALUE;
        int maxStopIndex = Integer.MAX_VALUE - allRange;
        for (int i = maxStartIndex; i > maxStopIndex; i--) {
            if (index == i)
                return "Max" + ((index == Integer.MAX_VALUE) ? "" : ("-" + Math.abs(Integer.MAX_VALUE - i)));
        }
        return String.valueOf(index);
    }
    //
    /***/
    public static String logString(Object object) {
        if (object == null)
            return "null";
        //
        StringBuilder logString = new StringBuilder("");
        if (object instanceof Collection) {
            //
            Collection<?> coll = (Collection<?>) object;
            for (Object obj : coll)
                logString.append(logString(obj) + " , ");
            if (logString.length() > 1)
                logString.delete(logString.length() - 3, logString.length() - 1);
            logString.insert(0, "[ ");
            logString.append("]");
        } else if (object.getClass().isEnum() == true) {
            //
            Enum<?> enumObj = (Enum<?>) object;
            logString.append(enumObj.name());
        } else if (object.getClass().isArray() == true) {
            //
            Object[] array = (Object[]) object;
            logString.append(logString(Arrays.asList(array)));
        } else {
            //
            if (object instanceof Class)
                logString.append(((Class<?>) object).getName());
            else if (object instanceof Throwable) {
                Throwable err = (Throwable) object;
                StringWriter sw = new StringWriter();
                sw.append('\n');
                err.printStackTrace(new PrintWriter(sw));
                logString.append(sw.getBuffer());
            } else if (object instanceof URL) {
                URL url = (URL) object;
                try {
                    logString.append(URLDecoder.decode(url.toString(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    logString.append(url.toString());
                }
            } else if (object instanceof URI) {
                URI uri = (URI) object;
                try {
                    logString.append(URLDecoder.decode(uri.toString(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    logString.append(uri.toString());
                }
            } else {
                logString.append(object.toString());
            }
        }
        return logString.toString();
    }
    /*
    * 
    * 
    * 
    * 
    * 
    * 
    * 
    * 
    * 
    * 
    * 
    * 
    * 
    * 
    */
    /** Asserts that an argument is legal. If the given boolean is
     * not <code>true</code>, an <code>IllegalArgumentException</code>
     * is thrown.
     *
     * @param expression the outcome of the check
     * @return <code>true</code> if the check passes (does not return
     *    if the check fails)
     * @exception IllegalArgumentException if the legality test failed
     */
    public static boolean assertIsLegal(boolean expression) {
        return assertIsLegal(expression, ""); //$NON-NLS-1$
    }
    /** Asserts that an argument is legal. If the given boolean is
     * not <code>true</code>, an <code>IllegalArgumentException</code>
     * is thrown.
     * The given message is included in that exception, to aid debugging.
     *
     * @param expression the outcome of the check
     * @param message the message to include in the exception
     * @return <code>true</code> if the check passes (does not return
     *    if the check fails)
     * @exception IllegalArgumentException if the legality test failed
     */
    public static boolean assertIsLegal(boolean expression, String message) {
        if (!expression)
            throw new IllegalArgumentException(message);
        return expression;
    }
    /** Asserts that the given object is not <code>null</code>. If this
     * is not the case, some kind of unchecked exception is thrown.
     * 
     * @param object the value to test
     */
    public static <T> T assertIsNotNull(T object) {
        return assertIsNotNull(object, ""); //$NON-NLS-1$
    }
    /** Asserts that the given object is not <code>null</code>. If this
     * is not the case, some kind of unchecked exception is thrown.
     * The given message is included in that exception, to aid debugging.
     *
     * @param object the value to test
     * @param message the message to include in the exception
     */
    public static <T> T assertIsNotNull(T object, String message) {
        if (object == null)
            throw new NullPointerException("null argument:" + message); //$NON-NLS-1$
        return object;
    }
}