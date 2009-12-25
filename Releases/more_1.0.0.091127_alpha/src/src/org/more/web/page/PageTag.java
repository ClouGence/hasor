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
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
/**
 * 分页标签处理程序。
 * Date : 2009-6-17
 * @author 赵永春
 */
public class PageTag extends BodyTagSupport {
    /**  */
    private static final long serialVersionUID  = 2242078414461148271L;
    PageInfo                  info              = null;                         //要执行的分页内容体
    //page_item分页项数据存放在页面上下文中的变量名，使用EL表达试可以访问此数据。
    //page_current当前页分页项数据存放在页面上下文中的变量名，使用EL表达试可以访问此数据。
    //page_items 
    String                    prefix            = "page";                       //变量前缀, 
    private String            expressionCurrent = null;                         //判断当前页的表达试
    //-----------------
    Object                    currentItem       = null;                         //当前页码项对象
    boolean                   firstRun          = true;                         //是否是第一次循环，第一次循环执行目的是为了分配Item的显示顺序
    List<BasePageTag>         childTags         = new ArrayList<BasePageTag>(0); //该自段保存了当前标签中包含的子标签，该数据是由子标签自己向父标签注册的。
    List<Object>              after             = new ArrayList<Object>(0);     //存放已经处理完的
    //====================================================
    /**
     * 获取特定类型的子标签集合。
     * @param type 要获取的标签类型。
     * @return 返回获取的子标签集合
     */
    BasePageTag[] getTagByClass(Class<?> type) {
        List<BasePageTag> al = new ArrayList<BasePageTag>(0);
        for (BasePageTag bpt : childTags)
            //类型一致则加入匹配成功列表
            if (bpt.getClass() == type)
                al.add(bpt);
        //
        BasePageTag[] tags = new BasePageTag[al.size()];
        al.toArray(tags);
        return tags;
    }
    /** 测试obj是否为分页项中的当前分页项 */
    private boolean testCurrentItem(Object obj) {
        //判断是否有不合法表达试或者表达试为空
        if (expressionCurrent == null || Pattern.matches(".*=.*", this.expressionCurrent) == false)
            return false;
        //通过反射方式读取属性
        try {
            String[] ns = expressionCurrent.split("=");
            ns[0] = ns[0].trim();
            ns[1] = ns[1].trim();
            //
            if (ns[0] == null || ns[0].equals(""))
                //当ns[0]为空
                return obj.equals(ns[1]);
            else {
                //当ns[0]不为空
                PropertyDescriptor pe = new PropertyDescriptor(ns[0], obj.getClass());
                Object returnObject = pe.getReadMethod().invoke(obj);
                if (returnObject == null)
                    return false;
                if (returnObject.toString().equals(ns[1]) == true)
                    return true;
                else
                    return false;
            }
        } catch (Exception e) {}
        return false;
    }
    /** 当遇到标签page时执行该方法，该方法将初始化info对象的迭代器用于迭代处理分页项。 */
    @Override
    public int doStartTag() throws JspException {
        this.info.release();
        //测试迭代器中当前项是否为当前分页项
        while (this.info.hasNext() == true) {
            if (this.testCurrentItem(this.info.next()) == true)
                this.currentItem = this.info.getCurrentItem();
            if (this.currentItem != null)
                break;
        }
        this.info.release();
        this.firstRun = true;
        if (this.info.getCurrentItem() != null || this.info.hasNext() == true) {
            //执行标签，并且执行init方法
            return EVAL_BODY_INCLUDE;
        } else
            //不执行标签体执行
            return SKIP_BODY;
    }
    //2
    @Override
    public int doAfterBody() throws JspException {
        if (this.info == null)
            throw new JspException("page标签必须设置info属性，该属性类型是PageInfo类型。");
        if (this.prefix == null || this.prefix.equals(""))
            throw new JspException("page标签必须设置prefix属性，该属性类型是String类型。");
        if (this.info.hasNext() == true) {
            //当不是第一次执行时进行迭代
            if (this.firstRun == false)
                this.info.next();
            //标记执行完第一次
            this.firstRun = false;
            // 循环执行标签
            return EVAL_BODY_AGAIN;
        } else
            //执行本此标签之后不在进行循环执行
            return SKIP_BODY;
    }
    //3清理临时数据
    @Override
    public int doEndTag() throws JspException {
        this.info = null;
        this.childTags.clear();
        this.after.clear();
        this.currentItem = null;
        return EVAL_PAGE;//执行盛下的页面
    }
    //====================================================
    public void setInfo(PageInfo info) {
        this.info = info;
        this.info.initData();//执行初始化数据
    }
    public void setExpressionCurrent(String expressionCurrent) {
        this.expressionCurrent = expressionCurrent;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
