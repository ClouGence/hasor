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
 * Date : 2009-5-13
 * @author 赵永春
 */
public class NullLog implements ILog {
    @Override
    public void addWrite(ILogWrite logwrite, String level) {}
    @Override
    public void debug(String msg) {}
    @Override
    public void error(String msg) {}
    @Override
    public void info(String msg) {}
    @Override
    public void setFormater(ILogFormater formater) {}
    @Override
    public void warning(String msg) {}
}
