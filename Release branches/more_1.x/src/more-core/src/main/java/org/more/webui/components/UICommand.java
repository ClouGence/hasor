package org.more.webui.components;
/**
 * 
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class UICommand extends UIComponent implements ActionSource {
    public String getTagName() {
        return "Command";
    }
    /**通用属性表*/
    public enum Propertys {
        /**Action动作*/
        action,
    }
    /**获取Action EL字符串*/
    public String getAction() {
        return this.getProperty(Propertys.action.name()).valueTo(String.class);
    }
    /**设置Action EL字符串*/
    public void setAction(String action) {
        this.getProperty(Propertys.action.name()).value(action);
        this.methodExp = null;
    }
    private MethodExpression methodExp = null;
    public MethodExpression getActionExpression() {
        if (this.methodExp == null) {
            String actionString = this.getAction();
            if (actionString == null || actionString.equals("")) {} else
                this.methodExp = new MethodExpression(actionString);
        }
        return this.methodExp;
    }
}