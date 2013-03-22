package org.platform.api.safety;
/**
 * 权限认证策略
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface IPowerPolicy {
    /**
     * 进行策略检查。
     * @param userInfo 用户信息对象。
     * @param powerCode 要检查的权限点。
     */
    public boolean test(IUser userInfo, Object powerCode);
}