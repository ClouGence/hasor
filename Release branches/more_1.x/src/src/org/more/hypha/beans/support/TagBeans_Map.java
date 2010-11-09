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
package org.more.hypha.beans.support;
import java.util.HashMap;
import org.more.hypha.beans.define.List_ValueMetaData;
import org.more.hypha.beans.define.Map_ValueMetaData;
import org.more.hypha.configuration.DefineResourceImpl;
/**
 * 用于解析map标签
 * @version 2010-9-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagBeans_Map extends TagBeans_AbstractCollection<Map_ValueMetaData> {
    /**创建{@link TagBeans_Map}对象*/
    public TagBeans_Map(DefineResourceImpl configuration) {
        super(configuration);
    }
    /**创建{@link List_ValueMetaData}对象*/
    protected Map_ValueMetaData createDefine() {
        return new Map_ValueMetaData();
    }
    protected Class<?> getDefaultCollectionType() {
        return HashMap.class;
    }
}