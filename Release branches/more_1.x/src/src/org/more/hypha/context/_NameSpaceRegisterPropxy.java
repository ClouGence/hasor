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
package org.more.hypha.context;
/**
 * 接口{@link XmlNameSpaceRegister}的代理，用于保存所有来自于regedit.xml配置文件的信息。
 * @version 2010-11-12
 * @author 赵永春 (zyc@byshell.org)
 */
class _NameSpaceRegisterPropxy implements XmlNameSpaceRegister {
    private int                initSequence  = 0;
    private String             namespace     = null;
    private String             schema        = null;
    private boolean            schemaEnable  = false;
    private XmlNameSpaceRegister register      = null;
    private String             registerClass = null;
    //
    public _NameSpaceRegisterPropxy(String registerClass) {
        this.registerClass = registerClass;
    }
    //
    public void initRegister(String namespaceURL, XmlDefineResource resource) throws Throwable {
        Class<?> factory = Class.forName(this.registerClass);
        this.register = (XmlNameSpaceRegister) factory.newInstance();
        this.register.initRegister(this.namespace, resource);
    }
    public int getInitSequence() {
        return initSequence;
    }
    public void setInitSequence(int initSequence) {
        this.initSequence = initSequence;
    }
    public String getNamespace() {
        return namespace;
    }
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    public String getSchema() {
        return schema;
    }
    public void setSchema(String schema) {
        this.schema = schema;
    }
    public boolean isSchemaEnable() {
        return schemaEnable;
    }
    public void setSchemaEnable(boolean schemaEnable) {
        this.schemaEnable = schemaEnable;
    }
}