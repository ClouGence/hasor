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
package org.more.webui.tag;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.core.error.LostException;
import org.more.webui.context.ViewContext;
import org.more.webui.freemarker.parser.Hook_UserTag;
import org.more.webui.render.Render;
import org.more.webui.support.UICom;
import org.more.webui.support.UIComponent;
import freemarker.core.Environment;
import freemarker.core.TemplateElement;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.DeepUnwrap;
/**
 * 通用标签对象。
 * @version : 2012-5-13
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TagObject implements TemplateDirectiveModel {
    public final void execute(Environment arg0, Map arg1, TemplateModel[] arg2, TemplateDirectiveBody arg3) throws TemplateException, IOException {
        //0.反解过程
        HashMap<String, Object> objMap = new HashMap<String, Object>();
        if (arg1 != null)
            for (Object key : arg1.keySet()) {
                TemplateModel item = (TemplateModel) arg1.get(key);
                objMap.put(key.toString(), DeepUnwrap.permissiveUnwrap(item));
            }
        //1.取得位置
        try {
            //A.取得Stack中最后一个元素
            Field elementStackField = arg0.getClass().getDeclaredField("elementStack");
            elementStackField.setAccessible(true);
            List<Object> stackList = (List<Object>) elementStackField.get(arg0);
            TemplateElement element = (TemplateElement) stackList.get(stackList.size() - 1);
            //B.如果是一个注册元素则处理，否则忽略
            String tagType = element.getClass().getSimpleName();
            if (Hook_UserTag.Name.equals(tagType) == false)
                throw new TemplateException("遇到一个非“UnifiedCall”类型标签绑定。", arg0);
            //C.取得标签所处文档的位置路径。
            UIComponent component = getComponentByElement(ViewContext.getCurrentViewContext().getViewRoot(), element);
            this.exec(component, arg0, objMap, arg2, arg3);
        } catch (Exception e) {
            throw new TemplateException("错误：", e, arg0);
        }
    }
    private UIComponent getComponentByElement(UIComponent component, TemplateElement element) {
        TemplateElement obj = (TemplateElement) component.getAtts().get(Hook_UserTag.Name);
        //1.判断是否相等，在include情况下比较复杂不能使用==判断
        if (obj == element)
            return component;
        if (obj != null)
            if (obj.getTemplate().getName().equals(element.getTemplate().getName()) == true)
                if (obj.getBeginLine() == element.getBeginLine())
                    if (obj.getBeginColumn() == element.getBeginColumn())
                        if (obj.getEndColumn() == element.getEndColumn())
                            if (obj.getEndLine() == element.getEndLine())
                                return component;
        //2.
        for (UIComponent com : component.getChildren()) {
            UIComponent returnRes = this.getComponentByElement(com, element);
            if (returnRes != null)
                return returnRes;
        }
        return null;
    }
    public void exec(UIComponent component, Environment arg0, Map<String, Object> objMap, TemplateModel[] arg2, TemplateDirectiveBody arg3) throws TemplateException, IOException {
        //A.获取组建
        component.setupPropertys(objMap);
        //B.判断时候需要执行渲染
        if (component.isRender() == false)
            return;
        //C.获取渲染类
        UICom uicom = component.getClass().getAnnotation(UICom.class);
        if (uicom == null)
            throw new LostException("组建“" + component.getComponentID() + "”的类型无法定位到其渲染器。");
        ViewContext viewContext = ViewContext.getCurrentViewContext();
        String kitScope = viewContext.getRenderKitScope();
        Render renderer = viewContext.getUIContext().getRenderKit(kitScope).getRender(uicom.tagName());
        //D.进行渲染
        TemplateBody body = new TemplateBody(arg3, arg0);
        Writer writer = arg0.getOut();
        renderer.beginRender(viewContext, component, body, writer);
        if (component.isRenderChildren() == true)
            renderer.render(viewContext, component, body, writer);
        renderer.endRender(viewContext, component, body, writer);
    }
}