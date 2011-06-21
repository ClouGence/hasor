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
package org.more.hypha.context.xml;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.more.core.xml.stream.EndElementEvent;
import org.more.core.xml.stream.StartElementEvent;
import org.more.core.xml.stream.TextEvent;
import org.more.core.xml.stream.XmlAccept;
import org.more.core.xml.stream.XmlStreamEvent;
import org.more.util.StringConvertUtil;
/**
 * 该类是为了解析regedit.xml而设立的。
 * @version 2010-9-24
 * @author 赵永春 (zyc@byshell.org)
 */
class _NameSpaceConfiguration implements XmlAccept {
    private ArrayList<RegisterBean> registerList = new ArrayList<RegisterBean>();
    /** 用于保存所有来自于regedit.xml配置文件的信息。*/
    public static class RegisterBean {
        public int     initSequence  = 0;
        public String  namespace     = null;
        public String  schema        = null;
        public boolean schemaEnable  = false;
        public String  registerClass = null;
    }
    //------------------------------------------
    public List<RegisterBean> getRegisterBean() {
        Collections.sort(this.registerList, new Comparator<RegisterBean>() {
            public int compare(RegisterBean o1, RegisterBean o2) {
                if (o1.initSequence > o2.initSequence)
                    return 1;
                else if (o1.initSequence == o2.initSequence)
                    return 0;
                else
                    return -1;
            }
        });
        return this.registerList;
    }
    public void beginAccept() {}
    public void endAccept() {}
    //
    private StringBuffer currentNamespace    = null;
    private StringBuffer currentFactory      = null;
    private StringBuffer currentSchema       = null;
    private String       currentSchemaEnable = null;
    private int          currentSequence     = 0;
    //
    public void sendEvent(XmlStreamEvent e) {
        if (e.getXpath().equals("/Configuration/regedit") == true)
            this.processRegedit(e);
        else if (e.getXpath().startsWith("/Configuration/regedit/namespace") == true)
            this.p_namespace(e);
        else if (e.getXpath().startsWith("/Configuration/regedit/factory") == true)
            this.p_factory(e);
        else if (e.getXpath().startsWith("/Configuration/regedit/schema") == true)
            this.p_schema(e);
    }
    /**解析regedit标签*/
    private void processRegedit(XmlStreamEvent e) {
        if (e instanceof StartElementEvent) {
            this.currentNamespace = new StringBuffer();
            this.currentFactory = new StringBuffer();
            this.currentSchema = new StringBuffer();
            this.currentSchemaEnable = null;
            String sequence = ((StartElementEvent) e).getAttributeValue("initSequence");
            this.currentSequence = StringConvertUtil.parseInt(sequence, 0);
        } else if (e instanceof EndElementEvent) {
            RegisterBean obj = new RegisterBean();
            obj.initSequence = this.currentSequence;
            obj.namespace = this.currentNamespace.toString();
            obj.schema = this.currentSchema.toString();
            obj.schemaEnable = StringConvertUtil.parseBoolean(this.currentSchemaEnable);
            obj.registerClass = this.currentFactory.toString();
            this.registerList.add(obj);
        }
    }
    /**解析namespace标签*/
    private void p_namespace(XmlStreamEvent e) {
        if (e instanceof TextEvent == false)
            return;
        TextEvent ee = (TextEvent) e;
        this.currentNamespace.append(ee.getTrimText());
    }
    /**解析factory标签*/
    private void p_factory(XmlStreamEvent e) {
        if (e instanceof TextEvent == false)
            return;
        TextEvent ee = (TextEvent) e;
        this.currentFactory.append(ee.getTrimText());
    }
    /**解析schema标签*/
    private void p_schema(XmlStreamEvent e) {
        if (e instanceof StartElementEvent == true) {
            StartElementEvent ee = (StartElementEvent) e;
            this.currentSchemaEnable = ee.getAttributeValue("enable");
        }
        if (e instanceof TextEvent == false)
            return;
        TextEvent ee = (TextEvent) e;
        this.currentSchema.append(ee.getTrimText());
    }
}