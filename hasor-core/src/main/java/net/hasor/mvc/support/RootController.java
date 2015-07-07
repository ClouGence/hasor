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
import java.util.concurrent.atomic.AtomicBoolean;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
/**
 * 根控制器
 * @version : 2014年8月28日
 * @author 赵永春(zyc@hasor.net)
 */
class RootController implements EventListener {
    private AppContext          appContext  = null;
    private MappingInfoDefine[] invokeArray = new MappingInfoDefine[0];
    private AtomicBoolean       inited      = new AtomicBoolean(false);
    //
    public void onEvent(String event, Object[] params) throws Throwable {
        AppContext appContext = (AppContext) params[0];
        if (!this.inited.compareAndSet(false, true)) {
            return;/*避免被初始化多次*/
        }
        this.appContext = appContext;
        //1.find
        List<MappingInfoDefine> mappingList = this.appContext.findBindingBean(MappingInfoDefine.class);
        Collections.sort(mappingList, new Comparator<MappingInfoDefine>() {
            public int compare(MappingInfoDefine o1, MappingInfoDefine o2) {
                return o1.getMappingTo().compareToIgnoreCase(o2.getMappingTo()) * -1;
            }
        });
        //2.init
        for (MappingInfoDefine define : mappingList) {
            this.initDefine(define);
        }
        MappingInfoDefine[] defineArrays = mappingList.toArray(new MappingInfoDefine[mappingList.size()]);
        if (defineArrays != null) {
            this.invokeArray = defineArrays;
        }
    }
    /** @return 获取AppContext*/
    protected AppContext getAppContext() {
        return this.appContext;
    }
    /**
     * 初始化 {@link MappingInfoDefine}
     * @param define 等待初始化的MappingDefine
     */
    protected void initDefine(MappingInfoDefine define) {
        if (define != null) {
            define.init(this.appContext);
        }
    }
    /**
     * 查找符合 {@link MappingMatching}要求的 {@link MappingInfoDefine}
     * @param findMapping 匹配器
     * @return 返回匹配的MappingDefine。
     */
    public MappingInfoDefine findMapping(MappingMatching findMapping) {
        for (MappingInfoDefine invoke : this.invokeArray) {
            if (findMapping.matching(invoke) == true) {
                return invoke;
            }
        }
        return null;
    }
}