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
    private boolean         init        = false;
    //
    public void onEvent(String event, Object[] params) throws Throwable {
        this.appContext = (AppContext) params[0];
        if (this.init == true) {
            return;
        }
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
        this.init = true;
    }
    /**获取AppContext*/
    protected AppContext getAppContext() {
        return this.appContext;
    }
    /**初始化 {@link MappingDefine}*/
    protected void initDefine(MappingDefine define) {
        define.init(this.appContext);
    }
    /**查找符合路径的 {@link MappingDefine}*/
    public final MappingDefine findMapping(String controllerPath) {
        for (MappingDefine invoke : this.invokeArray) {
            if (this.matchingMapping(controllerPath, invoke) == true) {
                return invoke;
            }
        }
        return null;
    }
    /**查找符合的 {@link MappingDefine}*/
    public MappingDefine findMapping(FindMapping findMapping) {
        for (MappingDefine invoke : this.invokeArray) {
            if (findMapping.matching(invoke) == true) {
                return invoke;
            }
        }
        return null;
    }
    /**匹配策略*/
    protected boolean matchingMapping(String controllerPath, MappingDefine atInvoke) {
        return atInvoke.matchingMapping(controllerPath);
    }
}