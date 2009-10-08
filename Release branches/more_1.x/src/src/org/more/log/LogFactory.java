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
package org.more.log;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.more.log.objects.DefaultLog;
import org.more.log.objects.DefaultLogFormater;
import org.more.log.objects.DefaultLogWrite;
/**
 * 日志系统,默认配置文件名mylog.config
 * Date : 2009-5-13
 * @author 赵永春
 */
public class LogFactory {
    private static final String     Log_Config_File = "mylog.config";
    private static ComFactory       comFactory      = null;
    private static ComObjectFactory comCache        = null;
    private static boolean          init            = false;
    // ===============================================================
    private static void init(CommandReader cr) throws IOException {
        if (comFactory == null)
            comFactory = ComFactory.getComFactory();
        if (comCache == null)
            comCache = new ComObjectFactory(comFactory);
        comFactory.clear();
        comCache.clear();
        //
        CommandAttribut ca = null;
        while (true) {
            ca = cr.readCommandAttribut();
            if (ca == null)
                break;
            //
            if (ca.getName().equals("dim")) {
                // Log
                String[] names = ca.getValue().split(",");
                for (String name : names)
                    comFactory.createComBean(name);
            } else
                comFactory.addAttribut(ca);
        }
        init = true;
        cr.close();
    }
    /**
     * 获得默认日志输出对象，默认输出的所有输出内容只会向控制台输出。提示：默认日志对象使用的是
     * DefaultLog，DefaultLogFormater，DefaultLogWrite三个组建对象。
     * @return 返回默认日志输出对象，默认输出的所有输出内容只会向控制台输出。
     */
    public static ILog getLog() {
        ILog log = new DefaultLog();
        log.setFormater(new DefaultLogFormater());
        log.addWrite(new DefaultLogWrite(), ILog.LogLevel_ALL);
        return log;
    }
    /**
     * 获得配置好的日志输出对象。在获得日志对象时可以向日志对象传递动态参数。
     * @param name 配置的日志对象名。
     * @param objects 在获得日志对象时需要给日志对象传递的参数列表。
     * @return 返回配置好的日志输出对象。
     */
    public static ILog getLog(String name, Object... objects) {
        try {
            if (init == false)
                lookUp();
        } catch (Exception e) {}
        //
        try {
            Object obj = comCache.getComObject(name, objects);// 试图从缓存装载
            if (obj instanceof ILog)
                return (ILog) obj;
            else
                return new NullLog();
        } catch (Exception e) {
            return new NullLog();
        }
    }
    /**
     * 使用系统默认日志文件，初始化日志系统。系统默认日志文件mylog.config，存放在程序启动路径下。
     * @throws IOException 如果发生I/O异常
     */
    public static void lookUp() throws IOException {
        File file = new File(LogFactory.Log_Config_File);
        if (!file.canRead())
            init(new CommandReader(new StringReader("")));// 初始化
        else
            init(new CommandReader(new FileReader(file)));// 初始化
    }
    /**
     * 使用指定日志文件，初始化日志系统。
     * @param file 配置文件
     * @throws IOException 如果发生I/O异常
     */
    public static void lookUp(File file) throws IOException {
        if (!file.canRead())
            return;
        init(new CommandReader(new FileReader(file)));
    }
    /**
     * 使用指定日志配置字符串，初始化日志系统。
     * @param config 配置字符串
     * @throws IOException 如果发生I/O异常
     */
    public static void lookUp(String config) throws IOException {
        init(new CommandReader(new StringReader(config)));
    }
}