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
package org.more.submit.support;
import java.util.Date;
import java.util.UUID;
import org.more.submit.Session;
import org.more.util.attribute.AttBase;
/**
 * 简单{@link Session Session接口}实现。
 * <br/>Date : 2009-12-4
 * @author 赵永春
 */
public class BaseSession extends AttBase implements Session {
    //========================================================================================Field
    private static final long serialVersionUID = 3400815924288633711L;
    private long              createTime       = new Date().getTime();
    private String            sessionID        = UUID.randomUUID().toString();
    //==========================================================================================Job
    @Override
    public long getCreateTime() {
        return createTime;
    }
    @Override
    public String getSessionID() {
        return sessionID;
    }
}