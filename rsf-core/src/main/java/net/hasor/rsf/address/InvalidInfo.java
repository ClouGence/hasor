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
package net.hasor.rsf.address;
/**
 * 失效信息
 * @version : 2015年10月3日
 * @author 赵永春(zyc@hasor.net)
 */
public class InvalidInfo {
    private long timeoutPoint;
    private int  tryCount;
    private int  maxTryCount;
    //
    public InvalidInfo(long timeout, int maxTryCount) {
        this.timeoutPoint = System.currentTimeMillis() + timeout;
        this.tryCount = 0;
        this.maxTryCount = maxTryCount;
    }
    public void invalid(long timeout) {
        if (this.tryCount >= this.maxTryCount) {
            return;
        }
        if (this.timeoutPoint > System.currentTimeMillis()) {
            this.tryCount++;
        }
        this.timeoutPoint = System.currentTimeMillis() + timeout;
    }
    public boolean reTry() {
        if (this.tryCount >= this.maxTryCount || this.timeoutPoint > System.currentTimeMillis()) {
            return false;
        }
        this.tryCount = 0;
        return true;
    }
}
