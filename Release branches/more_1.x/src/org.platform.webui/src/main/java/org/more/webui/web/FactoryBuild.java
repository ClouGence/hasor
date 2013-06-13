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
package org.more.webui.web;
import java.util.Map.Entry;
import java.util.Set;
import org.more.util.ClassUtils;
import org.more.webui.component.UIComponent;
import org.more.webui.component.support.UICom;
import org.more.webui.context.BeanManager;
import org.more.webui.context.FacesConfig;
import org.more.webui.context.FacesContext;
import org.more.webui.lifestyle.Lifecycle;
import org.more.webui.lifestyle.PhaseListener;
import org.more.webui.lifestyle.UIPhaseListener;
import org.more.webui.render.RenderKit;
import freemarker.template.TemplateModelException;
/**
 * 
 * @version : 2012-6-27
 * @author 赵永春 (zyc@byshell.org)
 */
class FactoryBuild {
    private ClassUtils   classUtil    = null; //扫描的类包路径
    private WebUIFactory webUIFactory = null;
    public FactoryBuild(WebUIFactory webUIFactory) {
        this.webUIFactory = webUIFactory;
    }
    private void setup(FacesConfig config) {
        if (this.classUtil != null)
            return;
        String scanPackageStr = config.getScanPackages();
        String[] scanPackages = scanPackageStr.split(",");
        this.classUtil = ClassUtils.newInstance(scanPackages);
    }
    /**生成{@link FacesContext} */
    public FacesContext buildFacesContext(FacesConfig config) throws TemplateModelException {
        this.setup(config);
        FacesContext fc = this.webUIFactory.createFacesContext(config);
        RenderKit kit = new RenderKit();
        kit.initKit(fc);
        fc.addRenderKit("default", kit);
        Set<Class<?>> classSet = null;
        //A.扫描注解添加组建。
        classSet = classUtil.getClassSet(UICom.class);
        for (Class<?> type : classSet) {
            UICom uicom = type.getAnnotation(UICom.class);
            if (UIComponent.class.isAssignableFrom(type) == false)
                throw new ClassCastException(type + " to UIComponent");
            else {
                /*添加组建*/
                fc.addComponentType(uicom.tagName(), type);
                /*为组建添加默认标签渲染器，因为只有用于渲染器的组建才会生效。*/
                fc.getRenderKit("default").addRenderType(uicom.tagName(), uicom.renderType());
            }
        }
        //C.向全局Freemarker服务中注册标签
        kit = fc.getRenderKit("default");
        for (Entry<String, Object> tagEntry : kit.getTags().entrySet())
            fc.getFreemarker().setSharedVariable(tagEntry.getKey(), tagEntry.getValue());
        return fc;
    }
    /**生成{@link Lifecycle}*/
    public Lifecycle buildLifestyle(FacesConfig config, FacesContext context) {
        this.setup(config);
        /*创建生命周期对象*/
        Lifecycle lifestyle = this.webUIFactory.createLifestyle(config, context);
        /*添加生命周期监听器*/
        BeanManager beanManager = context.getBeanContext();
        Set<Class<?>> classSet = classUtil.getClassSet(UIPhaseListener.class);
        for (Class type : classSet)
            if (PhaseListener.class.isAssignableFrom(type) == false)
                throw new ClassCastException(type + " to PhaseListener");
            else {
                PhaseListener listener = beanManager.getBean(type);
                lifestyle.addPhaseListener(listener);
            }
        return lifestyle;
    }
}