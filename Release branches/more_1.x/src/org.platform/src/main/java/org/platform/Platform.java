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
import java.util.Arrays;
import java.util.Collection;
/**
 * 
 * @version : 2013-4-3
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
public abstract class Platform implements PlatformConfigEnum {
    private static String callerType() {
        StackTraceElement[] stackElements = new Exception().getStackTrace();
        StackTraceElement onCode = stackElements[2];
        String callerClass = onCode.getClassName();
        return callerClass.substring(callerClass.lastIndexOf(".") + 1) + ":" + onCode.getMethodName();
    }
    public static void debug(String string) {
        System.out.println(callerType() + " ->> " + string);//TODO
    }
    public static void error(String string, Exception e) {
        System.err.println(callerType() + " ->> " + string);//TODO
    }
    public static void warning(String string) {
        System.err.println(callerType() + " ->> " + string);//TODO
    }
    public static void info(String string) {
        System.out.println(callerType() + " ->> " + string);//TODO 
    }
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
                err.printStackTrace(new PrintWriter(sw));
                logString.append(sw.getBuffer());
            } else {
                logString.append(object.toString());
            }
        }
        return logString.toString();
    }
}