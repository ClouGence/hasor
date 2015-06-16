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
import java.util.ArrayList;
import java.util.List;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
/**
 * ResultDefine集合
 * @version : 2013-5-10
 * @author 赵永春 (zyc@hasor.net)
 */
class DefineList extends ArrayList<ResultDefine> implements AppContextAware {
    private static final long serialVersionUID = 5293997430939415693L;
    public void setAppContext(AppContext appContext) {
        List<ResultDefine> defineList = appContext.findBindingBean(ResultDefine.class);
        this.addAll(defineList);
    }
}