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
package org.more.webui.lifestyle.phase;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.more.webui.context.UIContext;
import org.more.webui.context.ViewContext;
import org.more.webui.lifestyle.Phase;
import org.more.webui.lifestyle.PhaseID;
import org.more.webui.tag.TagObject;
/**
 * 第7阶段，将执行完的UI信息渲染到客户机中。
 * @version : 2011-8-4
 * @author 赵永春 (zyc@byshell.org)
 */
public class Render_Phase extends Phase {
    private Render_PhaseID phaseID = new Render_PhaseID();
    public PhaseID getPhaseID() {
        return this.phaseID;
    };
    public void execute(ViewContext viewContext) throws Throwable {
        if (viewContext.isRender() == false)
            return;
        HttpServletResponse response = viewContext.getHttpResponse();
        if (response.isCommitted() == false) {
            UIContext uiContext = viewContext.getUIContext();
            //1.拿到所有标签
            Map<String, TagObject> tagMap = uiContext.getRegister().createTagObjectMap();
            //2.拿到用户空间
            HashMap<String, Object> mm = new HashMap<String, Object>();
            mm.putAll(tagMap);
            mm.put("att_1", "aaa");
            //            BeanManager manager =uiContext .getBeanManager();//TODO
            viewContext.getTemplate().process(mm, response.getWriter());
        }
    };
};
class Render_PhaseID extends PhaseID {
    public String getPhaseID() {
        return "Render";
    };
};