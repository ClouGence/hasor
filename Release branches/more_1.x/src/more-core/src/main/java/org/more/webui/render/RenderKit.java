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
package org.more.webui.render;
import java.util.HashMap;
import java.util.Map;
import org.more.webui.tag.TagObject;
/**
 * 
 * @version : 2012-5-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class RenderKit {
    private Map<String, Class<?>> renderMapping = new HashMap<String, Class<?>>();
    private Map<String, Object>   tagMap        = new HashMap<String, Object>();
    //
    /**获取已经注册的标签对象集合*/
    public Map<String, Object> getTags() {
        return this.tagMap;
    }
    public Render<?> getRender(String tagName) {
        Class<?> renderType = this.renderMapping.get(tagName);
        return (Render<?>) AppUtil.getObj(renderType);
    }
    /**注册标签类，只能对已经注册render的组建进行注册*/
    public void addTag(String tagName, TagObject tagObject) {
        if (this.renderMapping.containsKey(tagName) == true)
            if (tagObject != null)
                tagMap.put(tagName, tagObject);
            else
                throw new NullPointerException("TagObject类型参数不能为空。");
    }
    public void addRender(String tagName, Class<?> renderClass) {
        this.renderMapping.put(tagName, renderClass);
        this.tagMap.put(tagName, new TagObject());//输出默认标签
    }
}