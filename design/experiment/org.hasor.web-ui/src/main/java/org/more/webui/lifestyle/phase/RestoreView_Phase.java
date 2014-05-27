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
package org.more.webui.lifestyle.phase;
import java.util.List;
import java.util.Map;
import org.more.webui.component.UIViewRoot;
import org.more.webui.context.ViewContext;
import org.more.webui.lifestyle.Phase;
import org.more.webui.lifestyle.PhaseID;
import com.alibaba.fastjson.JSON;
/**
 * 第2阶段，重塑UI组件状态。
 * @version : 2011-8-4
 * @author 赵永春 (zyc@byshell.org)
 */
public class RestoreView_Phase extends Phase {
    public static class RestoreView_PhaseID extends PhaseID {
        public String getPhaseID() {
            return "RestoreView";
        };
    };
    public final static RestoreView_PhaseID PhaseID = new RestoreView_PhaseID();
    //
    public PhaseID getPhaseID() {
        return PhaseID;
    };
    public void execute(ViewContext uiContext) throws Throwable {
        String targetPath = uiContext.getTargetPath();
        if (targetPath == null)
            return;
        // 回溯状态
        UIViewRoot viewRoot = uiContext.getViewRoot();
        String stateJsonData = uiContext.getStateData();
        if (stateJsonData == null)
            return;
        List<Map> viewState = JSON.parseArray(stateJsonData, Map.class);
        viewRoot.restoreState(targetPath, viewState);
    };
};