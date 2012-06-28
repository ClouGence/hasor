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
package org.more.webui.components.targetbutton;
import org.more.webui.support.UIButton;
import org.more.webui.support.UICom;
/**
 * 该组建的功能是引发其他组建的action请求。
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
@UICom(tagName = "ui_TargetButton")
public class TargetButton extends UIButton {
    /**通用属性表*/
    public enum Propertys {
        /**所调用的目标ID*/
        target
    }
    public String getTarget() {
        return this.getProperty(Propertys.target.name()).valueTo(String.class);
    }
    public void setTarget(String target) {
        this.getProperty(Propertys.target.name()).value(target);
    }
}