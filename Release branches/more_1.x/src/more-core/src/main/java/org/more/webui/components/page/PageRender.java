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
package org.more.webui.components.page;
import java.io.IOException;
import java.io.Writer;
import org.more.core.json.JsonUtil;
import org.more.util.Base64;
import org.more.webui.components.page.PageCom.Mode;
import org.more.webui.context.ViewContext;
import org.more.webui.render.Render;
import org.more.webui.render.UIRender;
import org.more.webui.tag.TemplateBody;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
/**
 * 
 * @version : 2012-5-18
 * @author 赵永春 (zyc@byshell.org)
 */
@UIRender(tagName = "ui_Page")
public class PageRender implements Render<PageCom> {
    @Override
    public void beginRender(ViewContext viewContext, PageCom component, TemplateBody arg3, Writer writer) throws IOException {
        writer.write("<div");
        /*-------------------------------------------------*/
        //
        /*-------------------------------------------------*/
        writer.write(" id='" + component.getClientID(viewContext) + "'");
        writer.write(" comID='" + component.getId() + "'");
        writer.write(" comType='ui_Page'");
        String base64 = Base64.base64Encode(JsonUtil.transformToJson(component.saveState()));
        writer.write(" uiState='" + base64 + "'");
        //HTML Att
        writer.write(" style='" + component.getProperty("style").valueTo(String.class) + "'");
        writer.write(" class='" + component.getProperty("class").valueTo(String.class) + "'");
        /*-------------------------------------------------*/
        //
        /*-------------------------------------------------*/
        writer.write(">");
    }
    /**无数据判断，有数据时始终返回true*/
    public boolean noData(boolean hasData, String noDateMode, String contains) {
        boolean doThis = true;
        if (hasData == false)
            if (noDateMode.contains(contains) == false)
                doThis = false;
        return doThis;
    }
    private void clearVar(ViewContext viewContext, TemplateBody arg3) throws TemplateModelException {
        viewContext.remove("isFirst");
        viewContext.remove("isLast");
        viewContext.remove("isCurrent");
        viewContext.remove("PageIndex");
        viewContext.remove("RowNum");
        arg3.getEnvironment().setVariable("isFirst", null);
        arg3.getEnvironment().setVariable("isLast", null);
        arg3.getEnvironment().setVariable("isCurrent", null);
        arg3.getEnvironment().setVariable("PageIndex", null);
        arg3.getEnvironment().setVariable("RowNum", null);
    }
    private void putFirstVar(ViewContext viewContext, TemplateBody arg3, Boolean isFirst) throws TemplateModelException {
        viewContext.put("isFirst", isFirst);
        arg3.getEnvironment().setVariable("isFirst", ObjectWrapper.DEFAULT_WRAPPER.wrap(isFirst));
    }
    private void putLastVar(ViewContext viewContext, TemplateBody arg3, Boolean isLast) throws TemplateModelException {
        viewContext.put("isLast", isLast);
        arg3.getEnvironment().setVariable("isLast", ObjectWrapper.DEFAULT_WRAPPER.wrap(isLast));
    }
    private void putCurrentVar(ViewContext viewContext, TemplateBody arg3, Boolean isCurrent) throws TemplateModelException {
        viewContext.put("isCurrent", isCurrent);
        arg3.getEnvironment().setVariable("isCurrent", ObjectWrapper.DEFAULT_WRAPPER.wrap(isCurrent));
    }
    private void putVar(ViewContext viewContext, TemplateBody arg3, Integer pageIndex, Integer rowNum) throws TemplateModelException {
        viewContext.put("PageIndex", pageIndex);
        viewContext.put("RowNum", rowNum);
        arg3.getEnvironment().setVariable("PageIndex", ObjectWrapper.DEFAULT_WRAPPER.wrap(pageIndex));
        arg3.getEnvironment().setVariable("RowNum", ObjectWrapper.DEFAULT_WRAPPER.wrap(rowNum));
    }
    @Override
    public void render(ViewContext viewContext, PageCom component, TemplateBody arg3, Writer writer) throws IOException, TemplateException {
        /**起始号*/
        int startWith = component.getStartWith();
        /**总数*/
        int rowCount = component.getRowCount();
        /**页大小*/
        int pageSize = component.getPageSize();
        /**当前页码*/
        int current = component.getCurrentPage();
        /**可以分页的最大页码*/
        float _pageMod = (float) (rowCount - startWith) % (float) pageSize;
        float _pageMax = (float) (rowCount - startWith) / (float) pageSize;
        int maxPage = (_pageMod != 0) ? ((int) _pageMax) + 1 : (int) _pageMax;
        /**当没有数据时显示模式，可叠加（逗号分割）。F(首页按钮)、P(上一页按钮)、N(下一页按钮)、L（尾页按钮）、I(页码按钮)、T(显示ui_pNoDate标签内容)：注意I与T只能有一个生效*/
        String noDateMode = component.getNoDateMode();
        /**有无数据*/
        boolean hasData = rowCount - startWith > 0;
        //
        //
        //
        //1.First
        if (component.isShowFirst() == true && noData(hasData, noDateMode, "F") == true) {
            this.clearVar(viewContext, arg3);
            this.putCurrentVar(viewContext, arg3, current <= 0);
            this.putVar(viewContext, arg3, 0, startWith);
            component.runMode = Mode.First;
            arg3.render(writer);
        }
        //2.Prev
        if (component.isShowPrev() == true && noData(hasData, noDateMode, "P") == true) {
            int prevPage = current - 1;
            prevPage = (prevPage <= 0) ? 0 : prevPage;
            prevPage = (prevPage >= maxPage) ? (int) _pageMax - 1 : prevPage;
            int prevRow = prevPage * pageSize + startWith;
            prevRow = (prevRow < startWith) ? startWith : prevRow;
            prevRow = (prevRow >= rowCount) ? (prevPage * pageSize + startWith) : prevRow;
            //
            this.clearVar(viewContext, arg3);
            this.putFirstVar(viewContext, arg3, current <= 0);
            this.putVar(viewContext, arg3, prevPage, prevRow);
            component.runMode = Mode.Prev;
            arg3.render(writer);
        }
        //3.Item
        for (int i = 0; i < maxPage; i++) {
            int itemRow = i * pageSize + startWith;
            itemRow = (itemRow >= rowCount) ? (current * pageSize + startWith) : itemRow;
            //
            this.clearVar(viewContext, arg3);
            this.putCurrentVar(viewContext, arg3, current == i);
            this.putVar(viewContext, arg3, i, itemRow);
            component.runMode = Mode.Item;
            arg3.render(writer);
        }
        //4.NoDate
        if (hasData == false) {
            if (noDateMode.contains("I") == true) {
                /**无数据：页码*/
                this.clearVar(viewContext, arg3);
                this.putCurrentVar(viewContext, arg3, true);
                this.putVar(viewContext, arg3, 0, startWith);
                component.runMode = Mode.Item;
                arg3.render(writer);
            } else if (noDateMode.contains("T") == true) {
                /**无数据：标签*/
                this.clearVar(viewContext, arg3);
                this.putCurrentVar(viewContext, arg3, null);
                this.putVar(viewContext, arg3, null, null);
                component.runMode = Mode.NoDate;
                arg3.render(writer);
            }
        }
        //5.Next
        if (component.isShowNext() == true && noData(hasData, noDateMode, "N") == true) {
            int nextPage = current + 1;
            nextPage = (nextPage >= maxPage) ? (int) _pageMax : nextPage;
            nextPage = (nextPage <= 0) ? 1 : nextPage;
            int nextRow = nextPage * pageSize + startWith;
            nextRow = (nextRow >= rowCount) ? (current * pageSize + startWith) : nextRow;
            nextRow = (nextRow < startWith) ? startWith : nextRow;
            //
            this.clearVar(viewContext, arg3);
            this.putLastVar(viewContext, arg3, current >= (int) _pageMax);
            this.putVar(viewContext, arg3, nextPage, nextRow);
            component.runMode = Mode.Next;
            arg3.render(writer);
        }
        //5.Last
        if (component.isShowLast() == true && noData(hasData, noDateMode, "L") == true) {
            this.clearVar(viewContext, arg3);
            this.putCurrentVar(viewContext, arg3, current >= (int) _pageMax);
            this.putVar(viewContext, arg3, (int) _pageMax, (int) _pageMax * pageSize);
            component.runMode = Mode.Last;
            arg3.render(writer);
        }
    }
    @Override
    public void endRender(ViewContext viewContext, PageCom component, TemplateBody arg3, Writer writer) throws IOException {
        writer.write("</div>");
    }
}