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
package net.hasor.mvc.controller.support;
import java.util.ArrayList;
import java.util.List;
import net.hasor.core.AppContext;
import com.google.inject.Binding;
import com.google.inject.TypeLiteral;
/** 
 * Action生命周期管理器。
 * @version : 2013-4-20
 * @author 赵永春 (zyc@hasor.net)
 */
class ActionManager {
    private List<ActionNameSpace> nameSpaceList = new ArrayList<ActionNameSpace>();
    //
    /**初始化启动缓存服务。*/
    public void initManager(AppContext appContext) {
        TypeLiteral<ActionNameSpace> SPACE_DEFS = TypeLiteral.get(ActionNameSpace.class);
        for (Binding<ActionNameSpace> entry : appContext.getGuice().findBindingsByType(SPACE_DEFS)) {
            ActionNameSpace space = entry.getProvider().get();
            nameSpaceList.add(space);
        }
        //
        for (ActionNameSpace space : this.nameSpaceList)
            space.initNameSpace(appContext);
    }
    //
    /**销毁缓存服务*/
    public void destroyManager(AppContext appContext) {
        for (ActionNameSpace space : nameSpaceList)
            space.destroyNameSpace(appContext);
    }
    //
    /**根据请求地址查找符合的Action命名空间。返回的map中key是action名。*/
    public ActionNameSpace findNameSpace(String actionNS) {
        ActionNameSpace findSpace = null;
        for (ActionNameSpace space : nameSpaceList) {
            if (actionNS.startsWith(space.getNameSpace()) == true) {
                findSpace = space;
                break;
            }
        }
        if (findSpace == null) {
            findSpace = new ActionNameSpace(actionNS);
            this.nameSpaceList.add(findSpace);
        }
        return findSpace;
    }
    //
    /**获取注册的ActionNameSpace*/
    public List<ActionNameSpace> getNameSpaceList() {
        return this.nameSpaceList;
    }
}