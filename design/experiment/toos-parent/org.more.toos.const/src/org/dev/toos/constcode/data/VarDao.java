package org.dev.toos.constcode.data;
import java.util.List;
import org.dev.toos.constcode.metadata.ConstBean;
import org.dev.toos.constcode.metadata.ConstVarBean;
/**
 * 
 * @version : 2013-2-17
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class VarDao {
    protected abstract void initDao() throws Throwable;
    protected abstract <T> Source<T> getSource();
    //
    //
    /**删除常量值*/
    public abstract boolean deleteVar(ConstVarBean targetVar);
    /**获取根层级常量。*/
    public abstract List<ConstVarBean> getVarRoots(ConstBean parentConst);
    /**获取常量的所有子节点。*/
    public abstract List<ConstVarBean> getVarChildren(ConstVarBean parentVar);
    /**添加常量,返回添加成功之后持久化的常量对象。添加失败返回null；*/
    public abstract ConstVarBean addVar(ConstVarBean newVar, int newVarIndex);
    /**更新常量,返回更新成功之后持久化的常量对象。添加失败返回null；*/
    public abstract ConstVarBean updateVar(ConstVarBean targetVar, int newVarIndex);
}