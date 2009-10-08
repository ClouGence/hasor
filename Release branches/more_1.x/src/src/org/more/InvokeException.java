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
package org.more;
/**
 * 当调用时，或者执行操作时发生异常。
 * Date : 2009-6-26
 * @author 赵永春
 */
public class InvokeException extends RuntimeException {
    /**  */
    private static final long serialVersionUID = -7774988512856603877L;
    /**
     * 当调用时，或者执行操作时发生异常。
     * @param string 异常的描述信息
     */
    public InvokeException(String string) {
        super(string);
    }
    /**
     * 当调用时，或者执行操作时发生异常。
     * @param error 异常的描述信息
     */
    public InvokeException(Throwable error) {
        super(error);
    }
}
