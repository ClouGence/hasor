package org.more.webui;
import javax.servlet.ServletException;
/**
 * 
 * @version : 2012-5-16
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class UILifecycleException extends ServletException {
    private static final long serialVersionUID = 2916980897656847125L;
    public UILifecycleException(String string, Throwable e) {
        super(string, e);
    }
}