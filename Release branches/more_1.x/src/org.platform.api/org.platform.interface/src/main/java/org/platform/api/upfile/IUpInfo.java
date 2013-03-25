package org.platform.api.upfile;
import java.util.Enumeration;
/**
 * 
 * @version : 2013-3-12
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public interface IUpInfo {
    public String getParameter(String name);
    public Enumeration<String> getParameterNames();
}