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
package net.hasor.utils;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.function.Function;

/**
 * <p>Provides utilities for manipulating and examining
 * <code>Throwable</code> objects.</p>
 * @version : 2014年9月25日
 * @author 赵永春 (zyc@hasor.net)
 * @version $Id: ExceptionUtils.java 1436770 2013-01-22 07:09:45Z ggregory $
 */
public class ExceptionUtils {
    public static RuntimeException toRuntimeException(Throwable proxy) {
        return toRuntimeException(proxy, throwable -> {
            return new RuntimeException(throwable.getClass().getName() + " - " + throwable.getMessage(), throwable);
        });
    }

    /**将异常包装为 {@link RuntimeException}*/
    public static RuntimeException toRuntimeException(Throwable proxy, Function<Throwable, RuntimeException> conver) {
        if (proxy instanceof InvocationTargetException && ((InvocationTargetException) proxy).getTargetException() != null) {
            proxy = ((InvocationTargetException) proxy).getTargetException();
        }
        if (proxy instanceof RuntimeException) {
            return (RuntimeException) proxy;
        }
        return conver.apply(proxy);
    }

    public static Throwable toRuntimeException(Throwable proxy, Class<?>[] exceptionTypes) {
        if (exceptionTypes != null) {
            for (Class<?> e : exceptionTypes) {
                if (e.isInstance(exceptionTypes)) {
                    return proxy;
                }
            }
        }
        return new RuntimeException(proxy.getClass().getName() + " - " + proxy.getMessage(), proxy);
    }

    public static Throwable getRootCause(Throwable throwable) {
        List<Throwable> list = getThrowableList(throwable);
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    private static Throwable getCauseUsingMethodName(Throwable throwable, String methodName) {
        Method method = null;
        try {
            method = throwable.getClass().getMethod(methodName);
        } catch (SecurityException | NoSuchMethodException var) { /**/}
        if (method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
            try {
                return (Throwable) method.invoke(throwable);
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var) { /**/ }
        }
        return null;
    }

    public static int getThrowableCount(Throwable throwable) {
        return getThrowableList(throwable).size();
    }

    public static Throwable[] getThrowables(Throwable throwable) {
        List<Throwable> list = getThrowableList(throwable);
        return list.toArray(new Throwable[0]);
    }

    public static List<Throwable> getThrowableList(Throwable throwable) {
        ArrayList<Throwable> list;
        for (list = new ArrayList<>(); throwable != null && !list.contains(throwable); throwable = throwable.getCause()) {
            list.add(throwable);
        }
        return list;
    }

    public static int indexOfThrowable(Throwable throwable, Class<?> clazz) {
        return indexOf(throwable, clazz, 0, false);
    }

    public static int indexOfThrowable(Throwable throwable, Class<?> clazz, int fromIndex) {
        return indexOf(throwable, clazz, fromIndex, false);
    }

    public static int indexOfType(Throwable throwable, Class<?> type) {
        return indexOf(throwable, type, 0, true);
    }

    public static int indexOfType(Throwable throwable, Class<?> type, int fromIndex) {
        return indexOf(throwable, type, fromIndex, true);
    }

    private static int indexOf(Throwable throwable, Class<?> type, int fromIndex, boolean subclass) {
        if (throwable != null && type != null) {
            if (fromIndex < 0) {
                fromIndex = 0;
            }
            Throwable[] throwables = getThrowables(throwable);
            if (fromIndex >= throwables.length) {
                return -1;
            } else {
                int i;
                if (subclass) {
                    for (i = fromIndex; i < throwables.length; ++i) {
                        if (type.isAssignableFrom(throwables[i].getClass())) {
                            return i;
                        }
                    }
                } else {
                    for (i = fromIndex; i < throwables.length; ++i) {
                        if (type.equals(throwables[i].getClass())) {
                            return i;
                        }
                    }
                }
                return -1;
            }
        } else {
            return -1;
        }
    }

    public static void printRootCauseStackTrace(Throwable throwable) {
        printRootCauseStackTrace(throwable, System.err);
    }

    public static void printRootCauseStackTrace(Throwable throwable, PrintStream stream) {
        if (throwable == null) {
            return;
        }
        Objects.requireNonNull(stream, "The PrintStream must not be null.");
        String[] trace = getRootCauseStackTrace(throwable);
        for (String s : trace) {
            stream.println(s);
        }
        stream.flush();
    }

    public static void printRootCauseStackTrace(Throwable throwable, PrintWriter writer) {
        if (throwable == null) {
            return;
        }
        Objects.requireNonNull(writer, "The PrintWriter must not be null");
        String[] trace = getRootCauseStackTrace(throwable);
        for (String s : trace) {
            writer.println(s);
        }
        writer.flush();
    }

    public static String[] getRootCauseStackTrace(Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            Throwable[] throwables = getThrowables(throwable);
            int count = throwables.length;
            List<String> frames = new ArrayList<>();
            List<String> nextTrace = getStackFrameList(throwables[count - 1]);
            int i = count;
            while (true) {
                --i;
                if (i < 0) {
                    return frames.toArray(new String[0]);
                }
                List<String> trace = nextTrace;
                if (i != 0) {
                    nextTrace = getStackFrameList(throwables[i - 1]);
                    removeCommonFrames(trace, nextTrace);
                }
                if (i == count - 1) {
                    frames.add(throwables[i].toString());
                } else {
                    frames.add(" [wrapped] " + throwables[i].toString());
                }
                frames.addAll(nextTrace);
            }
        }
    }

    public static void removeCommonFrames(List<String> causeFrames, List<String> wrapperFrames) {
        if (causeFrames != null && wrapperFrames != null) {
            int causeFrameIndex = causeFrames.size() - 1;
            for (int wrapperFrameIndex = wrapperFrames.size() - 1; causeFrameIndex >= 0 && wrapperFrameIndex >= 0; --wrapperFrameIndex) {
                String causeFrame = causeFrames.get(causeFrameIndex);
                String wrapperFrame = wrapperFrames.get(wrapperFrameIndex);
                if (causeFrame.equals(wrapperFrame)) {
                    causeFrames.remove(causeFrameIndex);
                }
                --causeFrameIndex;
            }
        } else {
            throw new IllegalArgumentException("The List must not be null");
        }
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static String[] getStackFrames(Throwable throwable) {
        return throwable == null ? ArrayUtils.EMPTY_STRING_ARRAY : getStackFrames(getStackTrace(throwable));
    }

    static String[] getStackFrames(String stackTrace) {
        String linebreak = System.lineSeparator();
        StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        ArrayList<String> list = new ArrayList<>();
        while (frames.hasMoreTokens()) {
            list.add(frames.nextToken());
        }
        return list.toArray(new String[0]);
    }

    static List<String> getStackFrameList(Throwable t) {
        String stackTrace = getStackTrace(t);
        String linebreak = System.lineSeparator();
        StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        List<String> list = new ArrayList<>();
        boolean traceStarted = false;
        while (frames.hasMoreTokens()) {
            String token = frames.nextToken();
            int at = token.indexOf("at");
            if (at != -1 && token.substring(0, at).trim().isEmpty()) {
                traceStarted = true;
                list.add(token);
            } else if (traceStarted) {
                break;
            }
        }
        return list;
    }

    public static String getMessage(Throwable th) {
        if (th == null) {
            return "";
        } else {
            String clsName = ClassUtils.getShortClassName(th, null);
            String msg = th.getMessage();
            return clsName + ": " + StringUtils.defaultString(msg);
        }
    }

    public static String getRootCauseMessage(Throwable th) {
        Throwable root = getRootCause(th);
        root = root == null ? th : root;
        return getMessage(root);
    }
}