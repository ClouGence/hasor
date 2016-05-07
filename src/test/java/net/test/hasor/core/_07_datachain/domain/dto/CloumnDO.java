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
package net.test.hasor.core._07_datachain.domain.dto;
/**
 * 新闻帖子
 * @version : 2016年5月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class CloumnDO {
    private long    id;          //栏目Id
    private String  name;        //栏目名
    private boolean special;     //允许个性化
    private boolean canSubscribe;//接受订阅
    //
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isSpecial() {
        return special;
    }
    public void setSpecial(boolean special) {
        this.special = special;
    }
    public boolean isCanSubscribe() {
        return canSubscribe;
    }
    public void setCanSubscribe(boolean canSubscribe) {
        this.canSubscribe = canSubscribe;
    }
}