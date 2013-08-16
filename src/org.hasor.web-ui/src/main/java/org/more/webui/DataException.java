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
package org.more.webui;
/**
 * 数据异常。
 * @version 2009-4-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class DataException extends RuntimeException {
    private static final long serialVersionUID = 3664651649094973500L;
    /**数据异常。*/
    public DataException(String string) {
        super(string);
    }
    /**数据异常。*/
    public DataException(Throwable error) {
        super(error);
    }
    /**数据异常。*/
    public DataException(String string, Throwable error) {
        super(string, error);
    }
}