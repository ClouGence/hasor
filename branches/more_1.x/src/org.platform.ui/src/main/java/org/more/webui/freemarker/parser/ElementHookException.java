package org.more.webui.freemarker.parser;
import java.io.IOException;
/**
 * {@link ElementHook}Ω”ø⁄÷¥––¥ÌŒÛ°£
 * @version : 2012-8-31
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class ElementHookException extends IOException {
    private static final long serialVersionUID = 3571899846181079327L;
    public ElementHookException(String string, Exception e) {
        super(string, e);
    }
    public ElementHookException(String string) {
        super(string);
    }
}