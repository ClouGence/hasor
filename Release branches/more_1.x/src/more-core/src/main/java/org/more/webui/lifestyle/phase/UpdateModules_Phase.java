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
import org.more.webui.context.ViewContext;
import org.more.webui.lifestyle.Phase;
import org.more.webui.lifestyle.PhaseID;
/**
 * 第5阶段，将组件模型中的值设置到映射的bean中。
 * @version : 2011-8-4
 * @author 赵永春 (zyc@byshell.org)
 */
public class UpdateModules_Phase extends Phase {
    public static class UpdateModules_PhaseID extends PhaseID {
        public String getPhaseID() {
            return "UpdateModules";
        };
    };
    private static UpdateModules_PhaseID PhaseID = new UpdateModules_PhaseID();
    public PhaseID getPhaseID() {
        return PhaseID;
    };
    public void execute(ViewContext uiContext) throws Throwable {
        uiContext.getViewRoot().processUpdate(uiContext); //执行模型更新
    };
};