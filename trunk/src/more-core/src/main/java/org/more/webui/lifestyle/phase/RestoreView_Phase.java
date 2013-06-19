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
import java.util.List;
import org.more.webui.context.ViewContext;
import org.more.webui.lifestyle.Phase;
import org.more.webui.lifestyle.PhaseID;
import org.more.webui.support.UIViewRoot;
import com.alibaba.fastjson.JSONObject;
/**
 * µÚ2½×¶Î£¬ÖØËÜUI×é¼þ×´Ì¬¡£
 * @version : 2011-8-4
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
public class RestoreView_Phase extends Phase {
    public static class RestoreView_PhaseID extends PhaseID {
        public String getPhaseID() {
            return "RestoreView";
        };
    };
    private static RestoreView_PhaseID PhaseID = new RestoreView_PhaseID();
    //
    public PhaseID getPhaseID() {
        return PhaseID;
    };
    public void execute(ViewContext uiContext) throws Throwable {
        String target = uiContext.getTarget();
        if (target == null)
            return;
        // »ØËÝ×´Ì¬
        UIViewRoot viewRoot = uiContext.getViewRoot();
        String stateJsonData = uiContext.getStateData();
        if (stateJsonData == null)
            return;
        Object[] viewState = JSONObject.parseObject(stateJsonData, List.class).toArray();
        viewRoot.restoreState(target, viewState);
    };
};