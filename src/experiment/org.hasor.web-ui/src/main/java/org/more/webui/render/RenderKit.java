/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
import org.more.webui.context.FacesContext;
import org.more.webui.tag.TagObject;
/**
 * 
 * @version : 2012-5-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class RenderKit {
    private FacesContext          facesContext  = null;
    /*标签对象集合*/
    private Map<String, Object>   tagObjectMap  = new HashMap<String, Object>();
    /*渲染器映射*/
    private Map<String, Class<?>> renderMapping = new HashMap<String, Class<?>>();
    /*----------------------------------------------------------------*/
    public void initKit(FacesContext facesContext) {
        this.facesContext = facesContext;
    }
    /**获取已经注册的标签对象集合。*/
    public Map<String, Object> getTags() {
        return this.tagObjectMap;
    }
    /**注册标签类，只能对已经注册render的组建进行注册。*/
    public void addTag(String tagName, TagObject tagObject) {
        if (tagObject == null)
            throw new NullPointerException("TagObject类型参数不能为空。");
        this.tagObjectMap.put(tagName, tagObject);
    }
    /**获取渲染器对象。*/
    public Render<?> getRender(String tagName) {
        Class beanType = this.renderMapping.get(tagName);
        return this.facesContext.getBeanContext().getBean(beanType);
    }
    /**添加渲染器映射。*/
    public void addRenderType(String tagName, Class<?> beanType) {
        this.renderMapping.put(tagName, beanType);
        this.tagObjectMap.put(tagName, new TagObject());//输出默认标签
    }
}