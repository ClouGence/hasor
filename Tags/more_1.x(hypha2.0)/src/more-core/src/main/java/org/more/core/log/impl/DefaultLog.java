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
import org.more.core.log.Log;
/**
 * 默认的{@link Log}接口实现。
 * @version : 2011-7-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultLog implements Log {
    public void debug(String msg, Object... infoObjects) {
        this.out("debug", msg, infoObjects);
    };
    public void info(String msg, Object... infoObjects) {
        this.out("info", msg, infoObjects);
    };
    public void error(String msg, Object... infoObjects) {
        this.out("error", msg, infoObjects);
    };
    public void warning(String msg, Object... infoObjects) {
        this.out("warning", msg, infoObjects);
    };
    public void out(String type, String msg, Object... infoObjects) {
        // TODO Auto-generated method stub
    };
};