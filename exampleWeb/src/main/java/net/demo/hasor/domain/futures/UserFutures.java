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
package net.demo.hasor.domain.futures;
/**
 * 用户扩展信息数据结构
 * @version : 2016年08月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserFutures {
    private String present  = null; // 介绍
    private String name     = null; // 姓名
    private String birthday = null; // 生日
    //
    public String getPresent() {
        return present;
    }
    public void setPresent(String present) {
        this.present = present;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}