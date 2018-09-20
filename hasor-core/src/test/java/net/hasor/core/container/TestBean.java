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
package net.hasor.core.container;
import test.net.hasor.core.pojos.IntefaceBean;
import test.net.hasor.core.pojos.PojoInfo;

import java.util.UUID;
/**
 * 一个Bean
 * @version : 2014-1-3
 * @author 赵永春 (zyc@hasor.net)
 */
public class TestBean implements PojoInfo, IntefaceBean {
    private String uuid    = UUID.randomUUID().toString();
    private String name    = "马三";
    private String address = "北京马连洼街道办...";
    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}