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
package net.hasor.rsf.center.domain.constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 工作模式
 * @version : 2015年7月3日
 * @author 赵永春(zyc@hasor.net)
 */
public enum WorkMode {
    //
    /**1, "集群主机模式"*/
    Master(1, "master", "集群主机模式"),
    /**2, "集群从机模式"*/
    Slave(2, "slave", "集群从机模式"),
    /**3, "内存模式"*/
    Memory(3, "memory", "内存模式"), ;
    //
    //
    //
    //---------------------------------------------
    ;
    private static Logger logger = LoggerFactory.getLogger(WorkMode.class);
    private int           codeType;
    private String        codeString;
    private String        message;
    WorkMode(int codeType, String codeString, String message) {
        this.codeType = codeType;
        this.codeString = codeString;
        this.message = message;
    }
    public int getCodeType() {
        return this.codeType;
    }
    public String getCodeString() {
        return this.codeString;
    }
    public String getMessage() {
        return this.message;
    }
    public static WorkMode getModeByCodeType(int codeType) {
        for (WorkMode a : WorkMode.values()) {
            if (a.getCodeType() == codeType) {
                return a;
            }
        }
        logger.error("WorkMode = " + codeType);
        throw new RuntimeException("not found WorkMode: " + codeType);
    }
}
