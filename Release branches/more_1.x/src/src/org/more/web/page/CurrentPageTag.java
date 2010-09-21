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
package org.more.web.page;
import javax.servlet.jsp.JspException;
import org.more.util.StringConvert;
/**
 * 分页标签负责处理当前页的标签，标签类型是C类标签。
 * @version 2009-6-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class CurrentPageTag extends BasePageTag {
    /**  */
    private static final long serialVersionUID = -2131953397444450076L;
    protected void doFirstStartPageTag() throws JspException {
        this.page.childTags.add(this);//在大环境中注册自己。
    }
    protected int doStartPageTag() throws JspException {
        Object item = this.page.info.getCurrentItem();
        //如果当前循环的项不是当前项那么忽略
        if (this.page.currentItem != item)
            return SKIP_BODY;
        //
        //如果当前项是第一项并且first配置了occupyFirst属性为true则忽略输出
        if (page.info.isFirstIndex(0, item) == true) {
            BasePageTag[] fTag = this.page.getTagByClass(FirstPageTag.class);
            if (fTag.length == 1) {
                FirstPageTag ft = (FirstPageTag) fTag[0];
                boolean occupyFirst = StringConvert.parseBoolean(ft.occupyFirst.toString());
                if (occupyFirst == true)
                    return SKIP_BODY;
            }
        }
        //如果当前项是最后一项并且last配置了occupyLast属性为true则忽略输出
        if (page.info.isLastIndex(0, item) == true) {
            BasePageTag[] fTag = this.page.getTagByClass(LastPageTag.class);
            if (fTag.length == 1) {
                LastPageTag ft = (LastPageTag) fTag[0];
                boolean occupyLast = StringConvert.parseBoolean(ft.occupyLast.toString());
                if (occupyLast == true)
                    return SKIP_BODY;
            }
        }
        return this.doTag();
    }
}