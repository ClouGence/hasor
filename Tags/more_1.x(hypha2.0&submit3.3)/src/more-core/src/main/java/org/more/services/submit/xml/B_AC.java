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
package org.more.services.submit.xml;
import org.more.services.submit.ActionContext;
/**
 * 该类是表示一个由xml配置文件配置的{@link ActionContext}。
 * @version : 2011-7-15
 * @author 赵永春 (zyc@byshell.org)
 */
class B_AC {
    private String namespace = null;
    private String refBean   = null;
    public B_AC(String namespace, String refBean) {
        this.namespace = namespace;
        this.refBean = refBean;
    }
    public String getNamespace() {
        return this.namespace;
    }
    public String getRefBean() {
        return this.refBean;
    }
}