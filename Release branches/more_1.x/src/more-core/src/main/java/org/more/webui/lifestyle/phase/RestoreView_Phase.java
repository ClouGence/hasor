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
import org.more.core.error.MoreDataException;
import org.more.core.json.JsonUtil;
import org.more.webui.components.UIViewRoot;
import org.more.webui.context.ViewContext;
import org.more.webui.lifestyle.Phase;
import org.more.webui.lifestyle.PhaseID;
/**
 * 第2阶段，重塑UI组件状态。
 * @version : 2011-8-4
 * @author 赵永春 (zyc@byshell.org)
 */
public class RestoreView_Phase extends Phase {
    private RestoreView_PhaseID phaseID = new RestoreView_PhaseID();
    //
    public PhaseID getPhaseID() {
        return this.phaseID;
    };
    public void execute(ViewContext uiContext) throws Throwable {
        String stateJsonData = uiContext.getStateJsonData();
        if (stateJsonData == null)
            return;
        UIViewRoot viewRoot = uiContext.getViewRoot();
        Object[] viewState = (Object[]) JsonUtil.transformToObject(stateJsonData);
        //1.数据检查
        if (viewState == null)
            return;
        if (viewState.length != 2)
            throw new MoreDataException("由于数据丢失无法重塑viewRoot状态。");
        //2.回溯状态
        viewRoot.restoreState(viewState);
    };
};
class RestoreView_PhaseID extends PhaseID {
    public String getPhaseID() {
        return "RestoreView";
    };
};