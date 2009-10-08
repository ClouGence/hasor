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
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;
/**
 * 分页标签项的基类，该类中定义了标签首次执行的方法，和执行方法。
 * Date : 2009-6-17
 * @author 赵永春
 */
abstract class BasePageTag extends BodyTagSupport {
    protected PageTag page = null; // 父标签
    //1
    //该方法负责在第一次循环时调用doFirstStartPageTag方法以后的所有循环调用doStartPageTag方法。
    @Override
    public int doStartTag() throws JspException {
        //检查父标签
        Tag tag = this.getParent();
        if (tag instanceof PageTag == false)
            throw new JspException("last标签必须在page标签下。");
        page = (PageTag) tag; //强制转换成父标签的标签类型
        //====检测是否为第一次执行====。
        if (page.firstRun == true) {
            this.doFirstStartPageTag();//执行首次执行处理方法。
            return SKIP_BODY;//跳过执行
        }
        //如果当前项目为空则不执行doStartPageTag，因为当前项目为空可能意味着已经完全循环完毕。
        if (this.page.info.getCurrentItem() == null)
            return SKIP_BODY;
        //
        return this.doStartPageTag();
    }
    //3
    @Override
    public int doEndTag() throws JspException {
        this.page = null;
        return super.doEndTag();
    }
    /** 该方法返回执行标签，并且设置必要的属性 */
    protected int doTag() throws JspException {
        //prefix  page_item  page_current  page_items
        String page_item = page.prefix + "_item";
        String page_current = page.prefix + "_current";
        String page_items = page.prefix + "_items";
        String page_index = page.prefix + "_index";
        //
        if (page.info.getCurrentItem() != null)
            this.pageContext.setAttribute(page_item, page.info.getCurrentItem());
        if (page.currentItem != null)
            this.pageContext.setAttribute(page_current, page.currentItem);
        this.pageContext.setAttribute(page_items, page.info.getList());
        this.pageContext.setAttribute(page_index, page.info.getList().indexOf(page.info.getCurrentItem()));
        return EVAL_BODY_INCLUDE;
    }
    /** 处理首次执行的处理方法 */
    protected abstract void doFirstStartPageTag() throws JspException;
    /** 处理执行的处理方法 */
    protected abstract int doStartPageTag() throws JspException;
}
