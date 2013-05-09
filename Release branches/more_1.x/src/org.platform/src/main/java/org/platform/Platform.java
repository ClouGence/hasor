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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.more.util.ArrayUtil;
/**
 * 
 * @version : 2013-4-3
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
public abstract class Platform implements PlatformConfig {
    private static String callerType() {
        StackTraceElement[] stackElements = new Exception().getStackTrace();
        StackTraceElement onCode = stackElements[2];
        String callerClass = onCode.getClassName();
        return callerClass.substring(callerClass.lastIndexOf(".") + 1) + ":" + onCode.getMethodName();
    }
    //
    //
    /***/
    public static void debug(String string, Object... params) {
        Object[] paramsStr = getStringArray(params);
        System.out.println(callerType() + " ->> " + formatString(string, paramsStr));
    }
    //
    /***/
    public static void error(String string, Object... params) {
        Object[] paramsStr = getStringArray(params);
        System.err.println(callerType() + " ->> " + formatString(string, paramsStr));
    }
    //
    /***/
    public static void warning(String string, Object... params) {
        Object[] paramsStr = getStringArray(params);
        System.err.println(callerType() + " ->> " + formatString(string, paramsStr));
    }
    //
    /***/
    public static void info(String string, Object... params) {
        Object[] paramsStr = getStringArray(params);
        System.out.println(callerType() + " ->> " + formatString(string, paramsStr));
    }
    //
    /***/
    public static String formatString(String formatString, Object... args) {
        if (ArrayUtil.isBlank(args))
            return formatString;
        return String.format(formatString, args);
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
        } else if (object.getClass().isArray() == true) {
            //
            Object[] array = (Object[]) object;
            logString.append(logString(Arrays.asList(array)));
        } else {
            //
            if (object instanceof Class)
                logString.append(((Class) object).getName());
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
}