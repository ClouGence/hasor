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
package org.more.log.objects;
import java.util.Date;

import org.more.log.ILogFormater;
/**
 * 默认格式化数据,输出时间+线程号+时间
 * Date : 2009-5-13
 * @author 赵永春
 */
public class DefaultLogFormater implements ILogFormater {
    public String getFormatMessage(String level, String msg) {
        String time = String.format("%1$tY-%1$tm-%1$te %1$tH:%1tM:%1$tS", new Date());
        return time + ": T[" + Thread.currentThread().getId() + "]Level:" + level + " MSG=" + msg;
    }
}
