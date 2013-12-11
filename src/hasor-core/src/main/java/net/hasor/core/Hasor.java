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
package net.hasor.core;
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
 * Hasor 基础工具包。
 * @version : 2013-4-3
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class Hasor {
    //
    private static StackTraceElement onTrace() {
        StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
        StackTraceElement onCode = stackElements[4];
        return onCode;
    }
    private static String callerTrace() {
        StackTraceElement onCode = onTrace();
        String callerClass = onCode.getClassName();
        return callerClass.substring(callerClass.lastIndexOf(".") + 1) + ":" + onCode.getMethodName();
    }
    private static String callerInfo() {
        StackTraceElement onCode = onTrace();
        String callerClass = onCode.getClassName();
        return callerClass.substring(callerClass.lastIndexOf(".") + 1) + ":" + onCode.getMethodName();
    }
    private static String callerWarn() {
        StackTraceElement onCode = onTrace();
        String callerClass = onCode.getClassName();
        return callerClass.substring(callerClass.lastIndexOf(".") + 1) + ":" + onCode.getMethodName();
    }
    private static String callerErr() {
        StackTraceElement onCode = onTrace();
        //String callerClass = onCode.getClassName();
        return onCode.getFileName() + ":" + onCode.getLineNumber() + " - " + onCode.getMethodName();
    }
    private static Class<?> callerClass() {
        StackTraceElement onCode = onTrace();
        try {
            return Class.forName(onCode.getClassName());
        } catch (Exception e) {
            return Hasor.class;
        }
    }
    private static String[] getStringArray(Object... objects) {
        ArrayList<String> returnData = new ArrayList<String>();
        for (Object obj : objects) {
            if (obj == null)
                returnData.add("null");
            else
                returnData.add(logString(obj));
        }
        return returnData.toArray(new String[returnData.size()]);
    }
    /**
     * 输出 <i><b>调试</b></i> 日志信息。该方法使用：<code>String.format(String, Object[])</code>方式实现。
     * @param string 要输出的日志信息，或将要输出的格式化日志信息。
     * @param params 需要被格式化的内容。
     */
    public static void logDebug(String string, Object... params) {
        Logger log = LoggerFactory.getLogger(callerClass());
        if (!log.isDebugEnabled())
            return;
        Object[] paramsStr = getStringArray(params);
        log.debug(callerInfo() + " ->> " + String.format(string, paramsStr));
    }
    /**
     * 输出 <i><b>错误</b></i> 日志信息。该方法使用：<code>String.format(String, Object[])</code>方式实现。
     * @param string 要输出的日志信息，或将要输出的格式化日志信息。
     * @param params 需要被格式化的内容。
     */
    public static void logError(String string, Object... params) {
        Logger log = LoggerFactory.getLogger(callerClass());
        if (!log.isErrorEnabled())
            return;
        Object[] paramsStr = getStringArray(params);
        log.error(callerErr() + " ->> " + String.format(string, paramsStr));
    }
    /**
     * 输出 <i><b>警告</b></i> 日志信息。该方法使用：<code>String.format(String, Object[])</code>方式实现。
     * @param string 要输出的日志信息，或将要输出的格式化日志信息。
     * @param params 需要被格式化的内容。
     */
    public static void logWarn(String string, Object... params) {
        Logger log = LoggerFactory.getLogger(callerClass());
        if (!log.isWarnEnabled())
            return;
        Object[] paramsStr = getStringArray(params);
        log.warn(callerWarn() + " ->> " + String.format(string, paramsStr));
    }
    /**
     * 输出 <i><b>消息</b></i> 日志信息。该方法使用：<code>String.format(String, Object[])</code>方式实现。
     * @param string 要输出的日志信息，或将要输出的格式化日志信息。
     * @param params 需要被格式化的内容。
     */
    public static void logInfo(String string, Object... params) {
        Logger log = LoggerFactory.getLogger(callerClass());
        if (!log.isInfoEnabled())
            return;
        Object[] paramsStr = getStringArray(params);
        log.info(callerInfo() + " ->> " + String.format(string, paramsStr));
    }
    /**
     * 输出 <i><b>Trace</b></i> 日志信息。该方法使用：<code>String.format(String, Object[])</code>方式实现。
     * @param string 要输出的日志信息，或将要输出的格式化日志信息。
     * @param params 需要被格式化的内容。
     */
    public static void logTrace(String string, Object... params) {
        Logger log = LoggerFactory.getLogger(callerClass());
        if (!log.isTraceEnabled())
            return;
        Object[] paramsStr = getStringArray(params);
        log.info(callerTrace() + " ->> " + String.format(string, paramsStr));
    }
    /**使用日志的方式格式化。*/
    public static String formatString(String formatString, Object... args) {
        Object[] paramsStr = getStringArray(args);
        return String.format(formatString, paramsStr);
    }
    /**
     * 转换对象为字符串内容，用以打印目的。
     * @param object 将参数对象转换为可以作为日志输出的字符串内容。
     */
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
    //
    /**是否输出 Trace 级日志。*/
    public static boolean isTraceLogger() {
        Logger log = LoggerFactory.getLogger(callerClass());
        return log.isTraceEnabled();
    }
    /**是否输出 Debug 级日志。*/
    public static boolean isDebugLogger() {
        Logger log = LoggerFactory.getLogger(callerClass());
        return log.isDebugEnabled();
    }
    /**是否输出 Error 级日志。*/
    public static boolean isErrorLogger() {
        Logger log = LoggerFactory.getLogger(callerClass());
        return log.isErrorEnabled();
    }
    /**是否输出 Warning 级日志。*/
    public static boolean isWarningLogger() {
        Logger log = LoggerFactory.getLogger(callerClass());
        return log.isWarnEnabled();
    }
    /**是否输出 Info 级日志。*/
    public static boolean isInfoLogger() {
        Logger log = LoggerFactory.getLogger(callerClass());
        return log.isInfoEnabled();
    }
    //
    //
    /** Asserts that an argument is legal. If the given boolean is
     * not <code>true</code>, an <code>IllegalArgumentException</code>
     * is thrown.
     *
     * @param expression the outcome of the check
     * @return <code>true</code> if the check passes (does not return if the check fails)
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
     * @return <code>true</code> if the check passes (does not return if the check fails)
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