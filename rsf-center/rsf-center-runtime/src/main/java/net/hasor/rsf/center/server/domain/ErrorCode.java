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
package net.hasor.rsf.center.server.domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
/**
 * 错误码
 *
 * @version : 2015年7月3日
 * @author 赵永春(zyc@hasor.net)
 */
public enum ErrorCode {
    OK(0, "Success"),
    Exception(1, "异常抛出。"),
    EmptyResult(2, "结果集为空。"),
    //
    //
    //
    // ---------------------------------------------
    ;
    private static Logger logger = LoggerFactory.getLogger(ErrorCode.class);
    private final int    codeType;
    private final String message;
    ErrorCode(final int codeType, final String message) {
        this.codeType = codeType;
        this.message = message;
    }
    public int getCodeType() {
        return codeType;
    }
    public String getMessage() {
        return message;
    }
    static {
        checkErrorCode();
    }

    public static void checkErrorCode() {
        Set<Integer> values = new HashSet<Integer>();
        for (ErrorCode a : ErrorCode.values()) {
            if (values.contains(a.codeType)) {
                throw new RuntimeException(a.codeType + " duplicate ");
            }
            values.add(a.codeType);
        }
    }
}