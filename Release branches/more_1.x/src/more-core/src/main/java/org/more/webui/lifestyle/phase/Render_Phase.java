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
import javax.servlet.http.HttpServletResponse;
import org.more.webui.context.RenderType;
import org.more.webui.context.ViewContext;
import org.more.webui.lifestyle.Phase;
import org.more.webui.lifestyle.PhaseID;
/**
 * 第7阶段，将执行完的UI信息渲染到客户机中。
 * @version : 2011-8-4
 * @author 赵永春 (zyc@byshell.org)
 */
public class Render_Phase extends Phase {
    public static class Render_PhaseID extends PhaseID {
        public String getPhaseID() {
            return "Render";
        };
    };
    private static Render_PhaseID PhaseID = new Render_PhaseID();
    public PhaseID getPhaseID() {
        return PhaseID;
    };
    public void execute(ViewContext viewContext) throws Throwable {
        HttpServletResponse response = viewContext.getHttpResponse();
        if (response.isCommitted() == true)
            return;
        //确定渲染范围，进行渲染
        RenderType renderType = viewContext.getRenderType();
        if (renderType == RenderType.No)
            return;
        else if (renderType == RenderType.Part) {//TODO : 严重问题 有可能不支持
            System.out.println("局部渲染::暂不支持.");
            return;
        } else if (renderType == RenderType.ALL)
            viewContext.getTemplate().process(viewContext.getViewELContext(), response.getWriter());
    };
};
