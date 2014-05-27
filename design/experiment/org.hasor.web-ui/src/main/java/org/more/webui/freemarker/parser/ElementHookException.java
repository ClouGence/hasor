package org.more.webui.freemarker.parser;
import java.io.IOException;
/**
 * {@link ElementHook}接口执行错误。
 * @version : 2012-8-31
 * @author 赵永春 (zyc@byshell.org)
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