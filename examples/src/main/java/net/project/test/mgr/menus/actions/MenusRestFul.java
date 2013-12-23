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
package net.project.test.mgr.menus.actions;
import net.hasor.plugins.restful.Path;
import net.hasor.plugins.restful.PathParam;
import net.hasor.plugins.restful.RestfulService;
import net.hasor.plugins.result.ext.Redirect;
import net.project.test.mgr.menus.entity.MenuBean;
import net.project.test.mgr.menus.services.MenuServices;
import com.google.inject.Inject;
/**
 * 菜单导航是以门面模式方式实现
 * @version : 2013-12-23
 * @author 赵永春(zyc@hasor.net)
 */
@RestfulService
public class MenusRestFul {
    @Inject
    private MenuServices menuServices;
    /*使用重定向转发*/
    @Redirect
    /*映射 restful 服务地址，并定义一个参数*/
    @Path("/mgr/menus/{menuCode}")
    /*取得并参数，并跳转到对应的页面*/
    public String menuList(@PathParam("menuCode") String menuCode) {
        MenuBean menuBean = menuServices.findMenuByCode(menuCode);
        return (menuBean == null) ? "/mgr" : menuBean.getUrl();
    }
}