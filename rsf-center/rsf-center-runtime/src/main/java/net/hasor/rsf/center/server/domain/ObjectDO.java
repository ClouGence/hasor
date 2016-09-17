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
import java.util.Date;
/**
 * 对象
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class ObjectDO {
    private String objectID;
    private String content;
    private Date   refreshTime;
    //
    public String getObjectID() {
        return objectID;
    }
    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Date getRefreshTime() {
        return refreshTime;
    }
    public void setRefreshTime(Date refreshTime) {
        this.refreshTime = refreshTime;
    }
}