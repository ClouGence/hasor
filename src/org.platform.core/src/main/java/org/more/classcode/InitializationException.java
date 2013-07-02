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
package org.more.classcode;
/**
 * 初始化异常。
 * @version 2009-7-7
 * @author 赵永春 (zyc@byshell.org)
 */
public class InitializationException extends ClassCodeRuntimeException {
    private static final long serialVersionUID = 6489409968925378968L;
    /**初始化异常*/
    public InitializationException(String string) {
        super(string);
    }
    /**初始化异常*/
    public InitializationException(Throwable error) {
        super(error);
    }
    /**初始化异常*/
    public InitializationException(String string, Throwable error) {
        super(string, error);
    }
}