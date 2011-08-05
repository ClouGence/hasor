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
 * 数据性异常，该类型异常是指由于数据引发的不可控结果。
 * 例如：格式、支持性、存在性、空间程度。
 * @version 2009-10-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class MoreDataException extends MoreException {
    private static final long serialVersionUID = 3459563001434381901L;
    /** 数据性异常。*/
    public MoreDataException(String string) {
        super(string);
    }
    /** 数据性异常。*/
    public MoreDataException(Throwable error) {
        super(error);
    }
    /** 数据性异常。*/
    public MoreDataException(String string, Throwable error) {
        super(string, error);
    }
}