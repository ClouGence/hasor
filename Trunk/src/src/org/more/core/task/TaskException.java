package org.more.core.task;
/**
 * Task异常
 * @version 2009-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class TaskException extends RuntimeException {
    private static final long serialVersionUID = -5825306826820187090L;
    /** Task异常 */
    public TaskException() {
        super("Task异常");
    }
    /**
     * Task异常，错误信息由参数给出
     * @param msg 异常的描述信息
     */
    public TaskException(String msg) {
        super(msg);
    }
    /**
     * Task异常，错误信息是承接上一个异常而来
     * @param e 承接的异常
     */
    public TaskException(Exception e) {
        super(e);
    }
}