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
package org.more.webui.context;
import java.util.Map.Entry;
import java.util.Set;
import org.more.webui.render.NoRender;
import org.more.webui.render.Render;
import org.more.webui.render.RenderKit;
import org.more.webui.render.UIRender;
import org.more.webui.support.UICom;
import org.more.webui.support.UIComponent;
/**
 * 
 * @version : 2012-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class FacesContextFactory {
    /**创建{@link FacesContext}。*/
    public FacesContext createFacesContext(FacesConfig config) {
        try {
            FacesContext fc = new FacesContext(config) {};
            //A.扫描注解添加组建。
            {
                Set<Class<?>> classSet = AppUtil.getClassSet(UICom.class);
                for (Class<?> type : classSet) {
                    UICom uicom = type.getAnnotation(UICom.class);
                    if (UIComponent.class.isAssignableFrom(type) == false)
                        throw new ClassCastException(type + " to UIComponent");
                    else {
                        /*添加组建*/
                        fc.getFacesConfig().addComponent(uicom.tagName(), type);
                        /*为组建添加默认标签渲染器，因为只有用于渲染器的组建才会生效。*/
                        fc.getRenderKit().addRender(uicom.tagName(), NoRender.class);
                    }
                }
            }
            //B.扫描注解添加标签渲染器。
            {
                Set<Class<?>> classSet = AppUtil.getClassSet(UIRender.class);
                for (Class<?> type : classSet) {
                    UIRender uiRender = type.getAnnotation(UIRender.class);
                    if (Render.class.isAssignableFrom(type) == false)
                        throw new ClassCastException(type + " to Render");
                    else
                        fc.getRenderKit().addRender(uiRender.tagName(), type);
                }
            }
            //C.向全局Freemarker服务中注册标签
            RenderKit kit = fc.getRenderKit();
            for (Entry<String, Object> tagEntry : kit.getTags().entrySet())
                fc.getFreemarker().setSharedVariable(tagEntry.getKey(), tagEntry.getValue());
            return fc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}