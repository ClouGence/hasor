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
package net.hasor.registry.domain.server;
import java.util.Date;
/**
 * 对象
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class ObjectDO {
    private String objectID;    // 对象ID
    private String type;        // 对象类别
    private String refObjectID; // 与其它对象建立关系
    private String content;     // 内容
    private Date   refreshTime; // 最后刷新时间
    //
    public String getObjectID() {
        return objectID;
    }
    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getRefObjectID() {
        return refObjectID;
    }
    public void setRefObjectID(String refObjectID) {
        this.refObjectID = refObjectID;
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