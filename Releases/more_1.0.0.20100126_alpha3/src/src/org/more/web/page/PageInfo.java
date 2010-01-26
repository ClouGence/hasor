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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * 分页标签组建所使用的数据对象。可以通过扩展该对象以达到不同的分页分配策略。
 * @version 2009-6-17
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public class PageInfo {
    private List     list                = new ArrayList(); //保存项目的集合对象
    private Iterator iterator            = null;           //列表执行的迭代器
    private Object   iteratorCurrentItem = null;           //迭代器正在迭代的当前项目对象
    //=========================================================================
    //初始化数据
    public void initData() {
        if (this.list == null)
            this.list = new ArrayList();
    }
    /**
     * 获取项总数。
     * @return 返回项总数。
     */
    public int getSize() {
        return this.list.size();
    }
    /**
     * 获取的分页项编号，从0开始计算。如果找不到指定对象返回null。
     * @param index 要获取的分页项编号，从0开始计算。如果找不到指定对象返回null。
     * @return 返回某一个分页项，从0开始计算。如果找不到指定对象返回null。
     */
    public Object getItem(int index) {
        return (this.list.size() > index && index >= 0) ? this.list.get(index) : null;
    }
    /**
     * 添加分页项，如果当前分页数据正在参与分页显示时调用该方法可能导致分页显示的JSP页出现异常。
     * @param obj 要添加的目标对象。
     */
    public void addItem(Object obj) {
        this.list.add(obj);
    }
    /**
     * 删除指定位置的分页项，如果当前分页数据正在参与分页显示时调用该方法可能导致分页显示的JSP页出现异常。
     * @param index 要删除的目标分页项编号。
     */
    public void removeItem(int index) {
        this.list.remove(index);
    }
    /**
     * 删除指定位置的分页项，如果当前分页数据正在参与分页显示时调用该方法可能导致分页显示的JSP页出现异常。
     * @param obj 要删除的目标分页项对象。
     */
    public void removeItem(Object obj) {
        this.list.remove(obj);
    }
    /** 删除所有 */
    public void removeAll() {
        this.list.clear();
    }
    /**
     * 该方法功能是测试item项目是否是猜测的位置，该方法是从第一条开始匹配。
     * 如果猜测的位置正确则返回true否则返回false。
     * @param index 猜测的项目位置。
     * @param item 被猜测的项目。
     * @return 如果猜测的位置正确则返回true否则返回false。
     */
    boolean isFirstIndex(int index, Object item) {
        //(从后面开始)测试item在集合中的位置等于index则返回true否则返回false。
        return (list.indexOf(item) == index) ? true : false;
    }
    /**
     * 该方法功能是测试item项目是否是猜测的位置，该方法是从最后一条开始匹配。
     * 如果猜测的位置正确则返回true否则返回false。
     * @param index 猜测的项目位置。
     * @param item 被猜测的项目。
     * @return 如果猜测的位置正确则返回true否则返回false。
     */
    boolean isLastIndex(int index, Object item) {
        //(从前面开始)测试item在集合中的位置等于index则返回true否则返回false。
        return (list.lastIndexOf(item) == (list.size() - 1) - index) ? true : false;
    }
    Object getCurrentItem() {
        return this.iteratorCurrentItem;
    }
    boolean hasNext() {
        return this.iterator.hasNext();
    }
    Object next() {
        this.iteratorCurrentItem = this.iterator.next();
        return this.iteratorCurrentItem;
    }
    void release() {
        this.iteratorCurrentItem = null;
        this.iterator = null;
        this.iterator = this.list.iterator();
    }
    List getList() {
        return list;
    }
}
