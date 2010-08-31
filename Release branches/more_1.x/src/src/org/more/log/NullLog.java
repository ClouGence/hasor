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
/**
 * 默认日志源
 * @version 2009-5-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class NullLog implements ILog {
    private int mode = 0;
    public NullLog(int nowMode) {
        this.mode = nowMode;
    }
    public void addWrite(ILogWrite logwrite, String level) {}
    public void debug(String msg) {
        if (mode == 1)
            System.out.println(msg);
    }
    public void error(String msg) {
        if (mode == 1)
            System.err.println("error:" + msg);
    }
    public void info(String msg) {
        if (mode == 1)
            System.out.println(msg);
    }
    public void setFormater(ILogFormater formater) {}
    public void warning(String msg) {
        if (mode == 1)
            System.err.println("warning:" + msg);
    }
}