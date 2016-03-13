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
package net.hasor.rsf.center.domain;
import java.io.Serializable;
import java.util.List;
/**
 * 订阅
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
public class ReceiveResult implements Serializable {
    private static final long serialVersionUID = -8277039265148152750L;
    private String            centerSnapshot   = null;
    private List<String>      providerList     = null;
    public String getCenterSnapshot() {
        return centerSnapshot;
    }
    public void setCenterSnapshot(String centerSnapshot) {
        this.centerSnapshot = centerSnapshot;
    }
    public List<String> getProviderList() {
        return providerList;
    }
    public void setProviderList(List<String> providerList) {
        this.providerList = providerList;
    }
}