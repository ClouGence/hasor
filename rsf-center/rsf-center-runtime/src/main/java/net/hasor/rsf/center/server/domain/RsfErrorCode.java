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
/**
 * 
 * @version : 2016年5月8日
 * @author 赵永春(zyc@hasor.net)
 */
public enum RsfErrorCode {
    /**/
    Remove_TerminalTypeError(10, "注册方式不明确。"),
    /**/
    Beat_TerminalTypeError(10, "注册方式不明确。"),
    /**/
    ResultEmptyError(10, "result is empty."),;
    //
    RsfErrorCode(int type, String msgTemp) {}
    public String getTemplate() {
        // TODO Auto-generated method stub
        return null;
    }
}