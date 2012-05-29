package org.more.webui;
import javax.servlet.ServletException;
public class UIInitException extends ServletException {
    private static final long serialVersionUID = 2916980897656847125L;
    public UIInitException(String string, Exception e) {
        super(string, e);
    }
    public UIInitException(String string) {
        super(string);
    }
}