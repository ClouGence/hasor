package org.more.webui.context;
import org.more.webui.components.htmls.UICommand;
import org.more.webui.context._.UserInfo;
import org.more.webui.render.htmls.HTMLButton;
/**
 * 
 * @version : 2012-4-25
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class DefaultFacesContextFactory extends FacesContextFactory {
    /**¥¥Ω®{@link FacesContext}°£*/
    public FacesContext createFacesContext(FacesConfig config) {
        DefaultFacesContext fc = new DefaultFacesContext(config);
        //
        fc.getAttribute().put("userInfo", new UserInfo());
        fc.getFacesConfig().addLoader("org.more.webui.freemarker.xhtml.parser");
        fc.getFacesConfig().addComponent("Command", UICommand.class);
        fc.getFacesConfig().addRender("default", "Command", HTMLButton.class);
        return fc;
    }
}