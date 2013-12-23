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
package net.project.test.mgr.menus.services;
import java.util.ArrayList;
import java.util.List;
import net.hasor.core.AppContext;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.project.test.mgr.menus.entity.MenuBean;
import org.more.util.StringUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
/**
 * 单例的
 * @version : 2013-12-23
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class MenuServices {
    /*从构造方法中注入 AppContext 接口对象*/
    @Inject
    public MenuServices(AppContext appContext) {
        this.appContext = appContext;
    };
    //
    //
    private AppContext     appContext;
    private List<MenuBean> menuList;
    private void init() {
        if (menuList != null)
            return;
        this.menuList = new ArrayList<MenuBean>();
        /*获取操纵配置文件的接口*/
        Settings setting = appContext.getSettings();
        /*取得‘/demoProject/menus’ Xml节点*/
        XmlNode xmlNode = setting.getXmlProperty("demoProject.menus");
        /*使用 DOM 方式解析 Xml节点*/
        List<XmlNode> menus = xmlNode.getChildren("menu");
        for (XmlNode node : menus) {
            MenuBean menuBean = new MenuBean();
            menuBean.setCode(node.getAttribute("code"));
            menuBean.setName(node.getAttribute("name"));
            menuBean.setUrl(node.getAttribute("url"));
            menuList.add(menuBean);
        }
    }
    public MenuBean findMenuByCode(String code) {
        init();
        for (MenuBean menu : menuList) {
            /*忽略大小写判断是否相等*/
            if (!StringUtils.endsWithIgnoreCase(menu.getCode(), code))
                continue;
            return menu;
        }
        return null;
    }
    public List<MenuBean> getMenuList() {
        init();
        return this.menuList;
    }
}