package org.platform.api.safety;
import java.util.Map;
/**
 * 负责登陆认证
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface IAuthorization {
    /**登陆系统*/
    public IUser login(Map<String, Object> params);
    /**退出系统*/
    public void exit(IUser userInfo);
}