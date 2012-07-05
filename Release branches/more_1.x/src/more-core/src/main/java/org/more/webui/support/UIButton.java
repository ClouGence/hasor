package org.more.webui.support;
import org.more.webui.context.ViewContext;
/**
 * Button
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class UIButton extends UIComponent {
    /**通用属性表*/
    public enum Propertys {
        /**表示渲染时候是否使用a标签代替input标签，默认：是*/
        useLink,
        /**显示的名称*/
        title
    }
    @Override
    protected void initUIComponent(ViewContext viewContext) {
        super.initUIComponent(viewContext);
        this.setProperty(Propertys.useLink.name(), true);
        this.setProperty(Propertys.title.name(), "");
    }
    public boolean isUseLink() {
        return this.getProperty(Propertys.useLink.name()).valueTo(Boolean.TYPE);
    }
    public void setUseLink(boolean useLink) {
        this.getProperty(Propertys.useLink.name()).value(useLink);
    }
    public String getTitle() {
        return this.getProperty(Propertys.title.name()).valueTo(String.class);
    }
    public void setTitle(String title) {
        this.getProperty(Propertys.title.name()).value(title);
    }
}