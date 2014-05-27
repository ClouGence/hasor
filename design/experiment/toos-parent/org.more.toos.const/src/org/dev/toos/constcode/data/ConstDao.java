package org.dev.toos.constcode.data;
import java.util.List;
import org.dev.toos.constcode.metadata.ConstBean;
/**
 * 
 * @version : 2013-2-17
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class ConstDao {
    protected abstract void initDao() throws Throwable;
    public abstract Source getSource();
    //
    //
    /**删除常量*/
    public abstract boolean deleteConst(ConstBean constBean);
    /**获取根层级常量。*/
    public abstract List<ConstBean> getRootConst();
    /**获取常量的所有子节点。*/
    public abstract List<ConstBean> getConstChildren(ConstBean constPath);
    /**添加常量,返回添加成功之后持久化的常量对象。添加失败返回null；*/
    public abstract ConstBean addConst(ConstBean constBean, int newIndex);
    /**添加常量,返回添加成功之后持久化的常量对象。添加失败返回null；*/
    public abstract ConstBean updateConst(ConstBean constBean, int newIndex);
}