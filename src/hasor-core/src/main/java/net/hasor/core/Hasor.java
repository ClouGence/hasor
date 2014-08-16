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
import net.hasor.core.context.AbstractResourceAppContext;
import net.hasor.core.context.StandardAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Hasor 基础工具包。
 * @version : 2013-4-3
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class Hasor {
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext() {
        return Hasor.createAppContext(AbstractResourceAppContext.DefaultSettings, null, new Module[0]);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final BindInfoFactoryCreater factory) {
        return Hasor.createAppContext(AbstractResourceAppContext.DefaultSettings, factory, new Module[0]);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final BindInfoFactoryCreater factory, final Module... modules) {
        return Hasor.createAppContext(AbstractResourceAppContext.DefaultSettings, factory, modules);
    }
    //
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final String config) {
        return Hasor.createAppContext(config, null, new Module[0]);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final String config, final BindInfoFactoryCreater factory) {
        return Hasor.createAppContext(config, factory, new Module[0]);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final String config, final BindInfoFactoryCreater factory, final Module... modules) {
        try {
            StandardAppContext app = new StandardAppContext(config, factory);
            app.start(modules);
            return app;
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }
    //
    //
    //
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final String config, final Module... modules) {
        return Hasor.createAppContext(config, null, modules);
    }
    /**用简易的方式创建{@link AppContext}容器。*/
    public static AppContext createAppContext(final Module... modules) {
        return Hasor.createAppContext(AbstractResourceAppContext.DefaultSettings, null, modules);
    }
    //
    /*----------------------------------------------------------------------------------------Log*/
    private static StackTraceElement onTrace() {
        StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
        StackTraceElement onCode = stackElements[4];
        return onCode;
    }
    private static String callerTrace() {
        StackTraceElement onCode = Hasor.onTrace();
        String callerClass = onCode.getClassName();
        return callerClass.substring(callerClass.lastIndexOf(".") + 1) + ":" + onCode.getMethodName();
    }
    private static String callerInfo() {
        StackTraceElement onCode = Hasor.onTrace();
        String callerClass = onCode.getClassName();
        return callerClass.substring(callerClass.lastIndexOf(".") + 1) + ":" + onCode.getMethodName();
    }
    private static String callerWarn() {
        StackTraceElement onCode = Hasor.onTrace();
        String callerClass = onCode.getClassName();
        return callerClass.substring(callerClass.lastIndexOf(".") + 1) + ":" + onCode.getMethodName();
    }
    private static String callerErr() {
        StackTraceElement onCode = Hasor.onTrace();
        //String callerClass = onCode.getClassName();
        return onCode.getFileName() + ":" + onCode.getLineNumber() + " - " + onCode.getMethodName();
    }
    private static Class<?> callerClass() {
        StackTraceElement onCode = Hasor.onTrace();
        try {
            return Class.forName(onCode.getClassName());
        } catch (Exception e) {
            return Hasor.class;
        }
    }
    private static String[] getStringArray(final Object... objects) {
        ArrayList<String> returnData = new ArrayList<String>();
        for (Object obj : objects) {
            if (obj == null) {
                returnData.add("null");
            } else {
                returnData.add(Hasor.logString(obj));
            }
        }
        return returnData.toArray(new String[returnData.size()]);
    }
    /**
     * 输出 <i><b>调试</b></i> 日志信息。该方法使用：<code>String.format(String, Object[])</code>方式实现。
     * @param string 要输出的日志信息，或将要输出的格式化日志信息。
     * @param params 需要被格式化的内容。
     */
    public static void logDebug(final String string, final Object... params) {
        Logger log = LoggerFactory.getLogger(Hasor.callerClass());
        if (!log.isDebugEnabled()) {
            return;
        }
        Object[] paramsStr = Hasor.getStringArray(params);
        log.debug(Hasor.callerInfo() + " ->> " + String.format(string, paramsStr));
    }
    public static void logDebug(final Object e) {
        Hasor.logDebug("%s", e);
    }
    /**
    
    
    /**
     * 输出 <i><b>错误</b></i> 日志信息。该方法使用：<code>String.format(String, Object[])</code>方式实现。
     * @param string 要输出的日志信息，或将要输出的格式化日志信息。
     * @param params 需要被格式化的内容。
     */
    public static void logError(final String string, final Object... params) {
        Logger log = LoggerFactory.getLogger(Hasor.callerClass());
        if (!log.isErrorEnabled()) {
            return;
        }
        Object[] paramsStr = Hasor.getStringArray(params);
        log.error(Hasor.callerErr() + " ->> " + String.format(string, paramsStr));
    }
    public static void logError(final Object e) {
        Hasor.logError("%s", e);
    }
    /**
     * 输出 <i><b>警告</b></i> 日志信息。该方法使用：<code>String.format(String, Object[])</code>方式实现。
     * @param string 要输出的日志信息，或将要输出的格式化日志信息。
     * @param params 需要被格式化的内容。
     */
    public static void logWarn(final String string, final Object... params) {
        Logger log = LoggerFactory.getLogger(Hasor.callerClass());
        if (!log.isWarnEnabled()) {
            return;
        }
        Object[] paramsStr = Hasor.getStringArray(params);
        log.warn(Hasor.callerWarn() + " ->> " + String.format(string, paramsStr));
    }
    public static void logWarn(final Object e) {
        Hasor.logWarn("%s", e);
    }
    /**
     * 输出 <i><b>消息</b></i> 日志信息。该方法使用：<code>String.format(String, Object[])</code>方式实现。
     * @param string 要输出的日志信息，或将要输出的格式化日志信息。
     * @param params 需要被格式化的内容。
     */
    public static void logInfo(final String string, final Object... params) {
        Logger log = LoggerFactory.getLogger(Hasor.callerClass());
        if (!log.isInfoEnabled()) {
            return;
        }
        Object[] paramsStr = Hasor.getStringArray(params);
        log.info(Hasor.callerInfo() + " ->> " + String.format(string, paramsStr));
    }
    public static void logInfo(final Object e) {
        Hasor.logInfo("%s", e);
    }
    /**
     * 输出 <i><b>Trace</b></i> 日志信息。该方法使用：<code>String.format(String, Object[])</code>方式实现。
     * @param string 要输出的日志信息，或将要输出的格式化日志信息。
     * @param params 需要被格式化的内容。
     */
    public static void logTrace(final String string, final Object... params) {
        Logger log = LoggerFactory.getLogger(Hasor.callerClass());
        if (!log.isTraceEnabled()) {
            return;
        }
        Object[] paramsStr = Hasor.getStringArray(params);
        log.info(Hasor.callerTrace() + " ->> " + String.format(string, paramsStr));
    }
    public static void logTrace(final Object e) {
        Hasor.logTrace("%s", e);
    }
    /**使用日志的方式格式化。*/
    public static String formatString(final String formatString, final Object... args) {
        Object[] paramsStr = Hasor.getStringArray(args);
        return String.format(formatString, paramsStr);
    }
    /**
     * 转换对象为字符串内容，用以打印目的。
     * @param object 将参数对象转换为可以作为日志输出的字符串内容。
     */
    public static String logString(final Object object) {
        if (object == null) {
            return "null";
        }
        //
        StringBuilder logString = new StringBuilder("");
        if (object instanceof Collection) {
            //
            Collection<?> coll = (Collection<?>) object;
            for (Object obj : coll) {
                logString.append(Hasor.logString(obj) + " , ");
            }
            if (logString.length() > 1) {
                logString.delete(logString.length() - 3, logString.length() - 1);
            }
            logString.insert(0, "[ ");
            logString.append("]");
        } else if (object.getClass().isEnum() == true) {
            //
            Enum<?> enumObj = (Enum<?>) object;
            logString.append(enumObj.name());
        } else if (object.getClass().isArray() == true) {
            //
            Object[] array = (Object[]) object;
            logString.append(Hasor.logString(Arrays.asList(array)));
        } else {
            //
            if (object instanceof Class) {
                logString.append(((Class<?>) object).getName());
            } else if (object instanceof Throwable) {
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
        Logger log = LoggerFactory.getLogger(Hasor.callerClass());
        return log.isTraceEnabled();
    }
    /**是否输出 Debug 级日志。*/
    public static boolean isDebugLogger() {
        Logger log = LoggerFactory.getLogger(Hasor.callerClass());
        return log.isDebugEnabled();
    }
    /**是否输出 Error 级日志。*/
    public static boolean isErrorLogger() {
        Logger log = LoggerFactory.getLogger(Hasor.callerClass());
        return log.isErrorEnabled();
    }
    /**是否输出 Warning 级日志。*/
    public static boolean isWarningLogger() {
        Logger log = LoggerFactory.getLogger(Hasor.callerClass());
        return log.isWarnEnabled();
    }
    /**是否输出 Info 级日志。*/
    public static boolean isInfoLogger() {
        Logger log = LoggerFactory.getLogger(Hasor.callerClass());
        return log.isInfoEnabled();
    }
    //
    /*---------------------------------------------------------------------------------------Util*/
    /**如果参数为空会抛出 NullPointerException 异常。*/
    public static <T> T assertIsNotNull(final T object) {
        return Hasor.assertIsNotNull(object, ""); //$NON-NLS-1$
    }
    /**如果参数为空会抛出 NullPointerException 异常。*/
    public static <T> T assertIsNotNull(final T object, final String message) {
        if (object == null) {
            throw new NullPointerException("null argument:" + message); //$NON-NLS-1$
        }
        return object;
    }
}