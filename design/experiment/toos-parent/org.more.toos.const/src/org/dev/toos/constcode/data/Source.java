package org.dev.toos.constcode.data;
/**
 * 
 * @version : 2013-2-17
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Source<T> {
    /**获取用于操作数据的数据源对象*/
    public T getSource() throws Throwable;
    /**测试该数据源对象是否支持修改操作。*/
    public boolean canModify();
    /**保存对数据源进行的修改操作。这个方法针对一些不具备自动保存的数据源有很大的意义。*/
    public void save() throws Throwable;
    /**测试数据源知否支持自动保存。*/
    public boolean isAutoSave();
    /**设置数据源自动保存。*/
    public void setAutoSave(boolean autoSave);
    /**数据源中是否数据发生变化。*/
    public boolean isUpdate();
}