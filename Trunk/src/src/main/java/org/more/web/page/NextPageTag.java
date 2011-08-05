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
import org.more.util.StringConvertUtil;
/**
 * 分页标签负责处理页项的标签，标签类型是N类标签。
 * @version 2009-6-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class NextPageTag extends BasePageTag {
    /**  */
    private static final long serialVersionUID = 9093745696928091335L;
    private String            first            = null;                //当前页部分是距第一页的间隔数。
    private String            last             = null;                //当前页部分是距最后一页的间隔数。
    protected void doFirstStartPageTag() throws JspException {
        //如果不存在不用继续考虑其他，因为不存在的分页项不会被执行
        if (this.page.info.hasNext() == false)
            return;
        this.page.childTags.add(this);//注册自己
        //计算firstIndex，lastIndex
        int first = StringConvertUtil.parseInt(this.first, -1);//默认值-1
        int last = StringConvertUtil.parseInt(this.last, -1);//默认值-1
        //没有配置first或last
        if (first == -1 && last == -1)
            return;
        //================================================================
        //配置了first或last
        int last_true = this.page.info.getSize() - 1 - last;//
        //处理firstIndex和lastIndex对等时超出延伸问题。
        if (first >= last_true)
            return;//出现超出延伸,不处理
        //
        Object firstItem = this.page.info.getItem(first);//获取标记的头
        Object lastItem = this.page.info.getItem(last_true);//获取标记的尾
        if (this.page.after.contains(firstItem) == false && firstItem != null)
            this.page.after.add(firstItem); //占领firstItem
        if (this.page.after.contains(lastItem) == false && lastItem != null)
            this.page.after.add(lastItem); //占领lastItem
    }
    protected int doStartPageTag() throws JspException {
        Object item = page.info.getCurrentItem();
        //计算firstIndex，lastIndex
        int first = StringConvertUtil.parseInt(this.first, -1);//默认值-1
        int last = StringConvertUtil.parseInt(this.last, -1);//默认值-1
        //没有配置first或last
        if (first == -1 && last == -1) {
            if (this.page.after.contains(item) == false && item != null) {
                //没人处理next处理
                this.page.after.add(item);
                return this.doTag();
            } else
                return SKIP_BODY;
        }
        //================================================================
        //配置了first或last
        int last_true = this.page.info.getSize() - 1 - last;//
        //处理firstIndex和lastIndex对等时超出延伸问题。
        if (first >= last_true)
            return SKIP_BODY;//出现超出延伸,不处理
        //
        Object firstItem = this.page.info.getItem(first);//获取标记的头
        Object lastItem = this.page.info.getItem(last_true);//获取标记的尾
        if (this.page.after.contains(firstItem) == true || this.page.after.contains(lastItem) == true)
            if (item == firstItem || item == lastItem)
                return this.doTag();
        return SKIP_BODY;
    }
    protected int doTag() throws JspException {
        Object item = page.info.getCurrentItem();
        //
        //如果当前迭代项是第一项并且first配置了occupyFirst属性为true则忽略输出
        if (page.info.isFirstIndex(0, item) == true) {
            BasePageTag[] fTag = this.page.getTagByClass(FirstPageTag.class);
            if (fTag.length == 1) {
                FirstPageTag ft = (FirstPageTag) fTag[0];
                boolean occupyFirst = StringConvertUtil.parseBoolean(ft.occupyFirst.toString());
                if (occupyFirst == true)
                    return SKIP_BODY;
            }
        }
        //如果当前迭代项是最后一项并且last配置了occupyLast属性为true则忽略输出
        if (page.info.isLastIndex(0, item) == true) {
            BasePageTag[] fTag = this.page.getTagByClass(LastPageTag.class);
            if (fTag.length == 1) {
                LastPageTag ft = (LastPageTag) fTag[0];
                boolean occupyLast = StringConvertUtil.parseBoolean(ft.occupyLast.toString());
                if (occupyLast == true)
                    return SKIP_BODY;
            }
        }
        //如果当前迭代项是当前项,并且配置了current标签则不输出
        if (page.currentItem == item) {
            BasePageTag[] fTag = this.page.getTagByClass(CurrentPageTag.class);
            if (fTag.length != 0)
                return SKIP_BODY;
        }
        return super.doTag();
    }
    public int doEndTag() throws JspException {
        return super.doEndTag();
    }
    //====================================================
    public void setFirst(String first) {
        this.first = first;
    }
    public void setLast(String last) {
        this.last = last;
    }
}
