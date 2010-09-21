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
 * 分页标签负责处理最后一页的标签，标签类型是L类标签。
 * @version 2009-6-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class LastPageTag extends BasePageTag {
    /**  */
    private static final long serialVersionUID = -6705535575688797813L;
    /** 标签属性，指出Last标签是否占领页码部分的最后一条。true表示占领，false表示不占领。 */
    Object                    occupyLast       = "false";
    protected void doFirstStartPageTag() throws JspException {
        Object item = page.info.getItem(page.info.getSize() - 1); //获得最后一条
        //获得是否占领页码部分的最后一条
        boolean occupyLastB = StringConvert.parseBoolean(this.occupyLast.toString());
        //first如果决定霸占最后一条则最后一条将被添加到after集合中标志着条已经被处理。
        if (occupyLastB == true && item != null)
            this.page.after.add(item);
        this.page.childTags.add(this);//在大环境中注册自己。
    }
    protected int doStartPageTag() throws JspException {
        Object item = page.info.getItem(page.info.getSize() - 1); //获得最后一条
        //如果不是最后一条则不执行
        if (item != this.page.info.getCurrentItem())
            return SKIP_BODY;
        else
            return this.doTag();
    }
    //============================================================
    public void setOccupyLast(Object occupyLast) {
        this.occupyLast = occupyLast;
    }
}
