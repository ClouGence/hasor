package org.dev.toos.constcode.metadata;
/**
 * 
 * @version : 2013-3-1
 * @author 赵永春 (zyc@byshell.org)
 */
public interface UpdateState<T> {
    /**用于通知更新状态*/
    public void updateState(T newState);
}