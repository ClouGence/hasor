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
package org.test.workflow.runtime;
import org.more.workflow.context.RunContext;
import org.more.workflow.event.EventListener;
import org.more.workflow.event.EventPhase;
import org.more.workflow.runtime.RuntimeMetadata;
import org.more.workflow.runtime.RuntimeProxy;
import org.more.workflow.runtime.RuntimeStateHolder;
import org.more.workflow.runtime.object.ActionRuntime;
import org.more.workflow.util.Config;
public class RuntimeMain {
    /**
     * @param args
     * @throws Throwable
     */
    public static void main(String[] args) throws Throwable {
        RuntimeMetadata fm = new RuntimeMetadata("Action", ActionRuntime.class);
        RuntimeStateHolder rs = new RuntimeStateHolder(fm);
        fm.addProperty("aa", "'testAccount'");//注入是无效的将被忽略
        //
        RunContext rc = new RunContext();
        RuntimeProxy runtime = (RuntimeProxy) rs.newInstance(rc);
        rs.updataMode(runtime, rc.getElContext());
        System.out.println(runtime);
        //
        runtime.addListener(new EventListener() {
            public void doListener(EventPhase event) {
                System.out.println(event.getEventPhaseType() + "---" + event.getEvent());
            }
        });
        //
        runtime.init(new ActionParam());
        runtime.beforeRun(new ActionParam(), rc);
        runtime.doRun(new ActionParam(), rc);
        runtime.afterRun(new ActionParam(), rc);
        runtime.destroy();
    };
};
class ActionParam implements Config {
    public Object getParam(String key) {
        if (key.equals("action"))
            return "test.action";
        else
            return null;
    }
    public Iterable<String> getParamNamesIterable() {
        return null;
    }
}