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
package org.more.core.log.impl;
import java.util.ArrayList;
import org.more.core.log.Log;
/**
 * 所有级别的一个代理。
 * @version : 2011-7-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class Log_Propxy implements Log {
    private ArrayList<Log> logs = new ArrayList<Log>();
    //
    public void debug(String msg, Object... infoObjects) {
        for (Log l : this.logs)
            l.debug(msg, infoObjects);
    };
    public void info(String msg, Object... infoObjects) {
        for (Log l : this.logs)
            l.info(msg, infoObjects);
    };
    public void error(String msg, Object... infoObjects) {
        for (Log l : this.logs)
            l.error(msg, infoObjects);
    };
    public void warning(String msg, Object... infoObjects) {
        for (Log l : this.logs)
            l.warning(msg, infoObjects);
    };
    public void out(String type, String msg, Object... infoObjects) {
        for (Log l : this.logs)
            l.out(type, msg, infoObjects);
    };
    public void add(Log logAsLevel) {
        this.logs.add(logAsLevel);
    }
};