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
package org.more;
/**
 * 未定义。
 * @version 2009-4-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class UndefinedException extends RuntimeException {
    /**
     * Required for serialization support.
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 2377606123252842745L;
    /**未定义。*/
    public UndefinedException(String string) {
        super(string);
    }
    /**未定义。*/
    public UndefinedException(Throwable error) {
        super(error);
    }
    /**未定义。*/
    public UndefinedException(String string, Throwable error) {
        super(string, error);
    }
}