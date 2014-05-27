package org.dev.toos.constcode.model.bridge;
import java.util.Hashtable;
import java.util.Map;
import org.dev.toos.constcode.metadata.UpdateState;
import org.dev.toos.constcode.metadata.create.NEW;
import org.dev.toos.constcode.model.ConstGroup;
import org.more.util.BeanUtil;
/**
 * 
 * @version : 2013-2-19
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractBridge<T> implements UpdateState<T> {
    private ConstGroup          targetSource   = null;                           //代理的数据来源
    private T                   targetBean     = null;                           //代理目标
    private boolean             activateModify = false;                          //是否激活了修改模式
    private boolean             newMark        = false;                          //是否为新建项目
    private boolean             deleteMark     = false;                          //是否删除了该对象
    private Map<String, Object> tempProperty   = new Hashtable<String, Object>();
    //
    //
    public AbstractBridge(T target, ConstGroup targetSource) {
        this.targetSource = targetSource;
        this.targetBean = target;
        if (this.targetBean instanceof NEW)
            this.newMark = true;
    }
    /**是否为只读*/
    public boolean readOnly() {
        return this.targetSource.isReadOnly();
    }
    /**获取代理的目标*/
    public T getTarget() {
        return this.targetBean;
    }
    /**获取代理的目标*/
    public void setTarget(T target) {
        this.targetBean = target;
    }
    /**获取来源*/
    public ConstGroup getSource() {
        return this.targetSource;
    }
    /**判断对象是否为新建的。*/
    public boolean isNew() {
        return newMark;
    }
    /**是否处于编辑模式。*/
    public boolean isActivateModify() {
        return this.activateModify;
    }
    /**进入编辑模式*/
    public void doEdit() {
        this.activateModify = true;
    }
    /**取消编辑模式*/
    public void cancelEdit() {
        this.activateModify = false;
    }
    /**判断该数据是否被删除*/
    public boolean isDelete() {
        return this.deleteMark;
    }
    /**删除数据*/
    public void delete() {
        this.deleteMark = true;
    }
    /**回复数据修改前的状态*/
    public void recover() {
        clearMark();
    }
    /**将Bridge上的数据更新到数据模型上。*/
    public abstract boolean applyData();
    /**清空修改标记*/
    public void clearMark() {
        this.deleteMark = false;
        this.tempProperty.clear();
    }
    /**获取属性值*/
    public Object getProperty(String propertyName) {
        if (this.tempProperty.containsKey(propertyName) == true)
            return this.tempProperty.get(propertyName);
        else if (this.targetBean != null)
            return BeanUtil.readPropertyOrField(this.targetBean, propertyName);
        return null;
    }
    /**设置属性值。*/
    public boolean setProperty(String propertyName, Object newValue) {
        if (this.readOnly() == true)
            return false;
        if (newValue == null)
            return false;
        Object proValue = this.getProperty(propertyName);
        if (proValue == newValue)
            return false;
        if (proValue != null && proValue.equals(newValue) == true)
            return false;
        //
        this.tempProperty.put(propertyName, newValue);
        this.getSource().setConstChanged(true);
        return true;
    }
    /**判断属性是否修改过。*/
    public boolean isPropertyChanged(String propertyName) {
        return this.tempProperty.containsKey(propertyName);
    }
    /**属性是否改变过*/
    public boolean isPropertyChanged() {
        if (this.deleteMark == true)
            return true;
        if (this.newMark == true)
            return true;
        return !this.tempProperty.isEmpty();
    }
}
