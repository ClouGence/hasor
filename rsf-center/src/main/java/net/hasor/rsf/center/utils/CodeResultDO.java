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
package net.hasor.rsf.center.utils;
import net.hasor.rsf.center.domain.constant.ErrorCodeObject;
import org.more.bizcommon.ResultDO;
/**
 * 
 * @version : 2015年7月6日
 * @author 赵永春(zyc@hasor.net)
 */
public class CodeResultDO<T> extends ResultDO<T> {
    private static final long serialVersionUID = -6535714269265915519L;
    private ErrorCodeObject   errorCode;
    //
    public CodeResultDO() {}
    public ErrorCodeObject getErrorCode() {
        return this.errorCode;
    }
    public CodeResultDO<T> setErrorCode(ErrorCodeObject errorCode) {
        this.errorCode = errorCode;
        return this;
    }
}