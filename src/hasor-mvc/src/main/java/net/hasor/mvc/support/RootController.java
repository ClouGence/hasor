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
package net.hasor.mvc.support;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
/**
 * 根控制器
 * @version : 2014年8月28日
 * @author 赵永春(zyc@hasor.net)
 */
public class RootController implements EventListener {
    private AppContext      appContext  = null;
    private MappingDefine[] invokeArray = null;
    /**获取AppContext*/
    public AppContext getAppContext() {
        return this.appContext;
    }
    /**初始化*/
    public final void onEvent(String event, Object[] params) throws Throwable {
        this.appContext = (AppContext) params[0];
        this.init();
    }
    protected void init() {
        //1.find
        List<MappingDefine> mappingList = this.appContext.findBindingBean(MappingDefine.class);
        Collections.sort(mappingList, new Comparator<MappingDefine>() {
            public int compare(MappingDefine o1, MappingDefine o2) {
                return o1.getMappingTo().compareToIgnoreCase(o2.getMappingTo()) * -1;
            }
        });
        //2.init
        for (MappingDefine define : mappingList) {
            this.initDefine(define);
        }
        this.invokeArray = mappingList.toArray(new MappingDefine[mappingList.size()]);
    }
    /**初始化{@link MappingDefine}*/
    protected void initDefine(MappingDefine define) {
        define.init(this.appContext);
    }
    /**查找符合路径的MappingDefine*/
    public final MappingDefine findMapping(String controllerPath, Object... params) {
        for (MappingDefine invoke : this.invokeArray) {
            if (this.matchingMapping(controllerPath, invoke, params) == true) {
                return invoke;
            }
        }
        return null;
    }
    /**匹配策略*/
    protected boolean matchingMapping(String controllerPath, MappingDefine atInvoke, Object... params) {
        return atInvoke.matchingMapping(controllerPath);
    }
    //
    /**调用符合路径的MappingDefine*/
    public Object invokeMapping(String controllerPath) throws Throwable {
        MappingDefine define = this.findMapping(controllerPath);
        if (define != null) {
            return define.invoke();
        }
        return null;
    }
}