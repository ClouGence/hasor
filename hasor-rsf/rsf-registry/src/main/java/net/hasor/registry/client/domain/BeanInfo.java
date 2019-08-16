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
package net.hasor.registry.client.domain;
import net.hasor.rsf.RsfBindInfo;

import java.io.Serializable;

/**
 * 发布的服务信息
 * @version : 2016年2月18日
 * @author 赵永春 (zyc@hasor.net)
 */
public class BeanInfo implements Serializable {
    private static final long   serialVersionUID = -7962837923093982098L;
    /** 唯一标识（客户端唯一标识,BeanID）*/
    private              String beanID;
    /** 注册的服务类型。*/
    private              String beanType;

    //
    public String getBeanID() {
        return beanID;
    }

    public String getBeanType() {
        return beanType;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    //
    public static BeanInfo of(RsfBindInfo<?> domain) {
        BeanInfo info = new BeanInfo();
        info.beanID = domain.getBindID();
        info.beanType = domain.getBindType().getName();
        return info;
    }
}