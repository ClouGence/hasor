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
package org.more.core.error;
/**
 * more定义性异常，该类型异常是指静态的不可变的性质所导致的。通常异常定义是名词性质的，超越时间的。
 * 例如：格式、支持性、存在性、空间程度。
 * @version 2009-10-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class MoreDefineException extends MoreException {
    private static final long serialVersionUID = 5497348918164005888L;
    /** more定义类异常。*/
    public MoreDefineException(String string) {
        super(string);
    }
    /** more定义类异常。*/
    public MoreDefineException(Throwable error) {
        super(error);
    }
    /** more定义类异常。*/
    public MoreDefineException(String string, Throwable error) {
        super(string, error);
    }
}