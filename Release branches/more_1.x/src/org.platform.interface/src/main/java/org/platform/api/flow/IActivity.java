package org.platform.api.flow;
/**
 * 工作流的一个节点。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface IActivity {
    /**启动*/
    public void start();
    /**执行工作流节点。*/
    public void process();
    /**完成工作*/
    public void finish();
}