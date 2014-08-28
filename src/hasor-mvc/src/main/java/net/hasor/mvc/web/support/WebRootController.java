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
package net.hasor.mvc.web.support;
import net.hasor.mvc.support.MappingDefine;
import net.hasor.mvc.support.RootController;
/**
 * 根控制器
 * @version : 2014年8月28日
 * @author 赵永春(zyc@hasor.net)
 */
public class WebRootController extends RootController {
    public MappingDefine findMapping(String method, String controllerPath) {
        // TODO Auto-generated method stub
        return null;s
    }
    protected boolean matchingMapping(String controllerPath, MappingDefine atInvoke) {
        // TODO Auto-generated method stub
        return super.matchingMapping(controllerPath, atInvoke);
    }
}