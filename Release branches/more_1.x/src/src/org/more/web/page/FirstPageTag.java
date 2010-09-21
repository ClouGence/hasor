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
 * 分页标签负责处理第一页的标签，标签类型是F类标签。
 * @version 2009-6-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class FirstPageTag extends BasePageTag {
    /**  */
    private static final long serialVersionUID = -9201682684698198985L;
    /** 标签属性，指出First标签是否占领页码部分的第一条，如果占领则页码从第二条开始，否则页码从第一条开始。true表示占领，false表示不占领。 */
    Object                    occupyFirst      = "false";
    protected void doFirstStartPageTag() throws JspException {
        Object item = page.info.getItem(0); //获得第一条
        //获得是否占领页码部分的第一条
        boolean occupyFirstB = StringConvert.parseBoolean(this.occupyFirst.toString());
        //first如果决定霸占第一条则第一条将被添加到after集合中标志着条已经被处理。
        if (occupyFirstB == true && item != null)
            this.page.after.add(item);
        this.page.childTags.add(this);//在大环境中注册自己。
    }
    protected int doStartPageTag() throws JspException {
        Object item = page.info.getItem(0); //获得第一条
        //如果不是第一条则不执行
        if (item != this.page.info.getCurrentItem())
            return SKIP_BODY;
        else
            return this.doTag();
    }
    //============================================================
    public void setOccupyFirst(Object occupyFirst) {
        this.occupyFirst = occupyFirst;
    }
}
