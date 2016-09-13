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
package net.hasor.rsf.domain;
import net.hasor.rsf.RsfResult;
/**
 * 消息调用结果集
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfResultDO implements RsfResult {
    private static final long serialVersionUID = -4678893554960623786L;
    private long    messageID;
    private boolean success;
    private int    errorCode    = 0;
    private String errorMessage = "";
    //
    public RsfResultDO() {
    }
    public RsfResultDO(long messageID, boolean success) {
        this.messageID = messageID;
        this.success = success;
    }
    //
    @Override
    public boolean isSuccess() {
        return this.success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    @Override
    public long getMessageID() {
        return this.messageID;
    }
    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }
    @Override
    public int getErrorCode() {
        return this.errorCode;
    }
    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}