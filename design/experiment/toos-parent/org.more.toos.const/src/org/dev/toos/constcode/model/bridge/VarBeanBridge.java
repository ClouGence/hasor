package org.dev.toos.constcode.model.bridge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dev.toos.constcode.metadata.ConstVarBean;
import org.dev.toos.constcode.metadata.create.NewConstVarBean;
import org.dev.toos.constcode.model.ConstGroup;
/**
 * 用于view中表述ConstBean对象。
 * @version : 2013-2-16
 * @author 赵永春 (zyc@byshell.org)
 */
public class VarBeanBridge extends AbstractBridge<ConstVarBean> {
    private ConstBeanBridge     parentConst  = null;
    private VarBeanBridge       parentVar    = null;
    private List<VarBeanBridge> childrenList = null;
    //
    /**创建一个新常量值的代理*/
    public VarBeanBridge(ConstBeanBridge parentConst, VarBeanBridge parentVar, ConstGroup targetSource) {
        this(parentConst, parentVar, new NewConstVarBean(parentConst.getTarget(), (parentVar == null) ? null : parentVar.getTarget()), targetSource);
    }
    /**创建一个已经存在的常量代理*/
    public VarBeanBridge(ConstBeanBridge parentConst, VarBeanBridge parentVar, ConstVarBean targetBean, ConstGroup targetSource) {
        super(targetBean, targetSource);
        this.parentConst = parentConst;
        this.parentVar = parentVar;
    }
    /*--------------------------------------------------------------------*/
    /**获取所属的常量*/
    public ConstBeanBridge getConst() {
        return this.parentConst;
    }
    /**获取父*/
    public VarBeanBridge getParent() {
        return this.parentVar;
    }
    /**获取子节点*/
    public List<VarBeanBridge> getChildren() {
        if (this.childrenList == null) {
            this.childrenList = this.getSource().loadChildrenVar(this);
            if (this.childrenList == null)
                this.childrenList = new ArrayList<VarBeanBridge>();
        }
        return Collections.unmodifiableList(this.childrenList);
    }
    /**添加子元素*/
    public void addVar(VarBeanBridge newVar) {
        this.childrenList.add(newVar);
        this.getSource().setConstChanged(true);
    }
    public void deleteVar(VarBeanBridge bridge) {
        if (bridge.isNew() == true)
            this.childrenList.remove(bridge);
        else
            bridge.delete();
        this.getSource().setConstChanged(true);
    }
    /*--------------------------------------------------------------------*/
    /**属性是否改变过，重写方法包含了对子集常量和常量值的检测*/
    public boolean isPropertyChanged() {
        boolean res = super.isPropertyChanged();
        for (VarBeanBridge childrenVar : this.getChildren())
            if (childrenVar.isPropertyChanged() == true) {
                res = true;
                break;
            }
        return res;
    }
    @Override
    public void recover() {
        //1.this recover
        super.recover();
        if (this.isNew() == true) {
            if (this.getParent() != null)
                this.getParent().deleteVar(this);
            else
                this.getConst().deleteVar(this);
        }
        //2.varList recover without delete
        ArrayList<VarBeanBridge> deleteVarList = new ArrayList<VarBeanBridge>();
        if (this.childrenList != null)
            for (VarBeanBridge varBridge : this.childrenList) {
                if (varBridge.isNew() == true)
                    deleteVarList.add(varBridge);
                else
                    varBridge.recover();
            }
        //3.varList recover on delete
        for (VarBeanBridge varBridge : deleteVarList)
            varBridge.recover();
    }
    public boolean applyData() {
        if (this.readOnly() == true)
            return false;
        //
        ConstVarBean target = this.getTarget();
        target.setVarKey(this.getKey());
        target.setVarVar(this.getVar());
        target.setVarLat(this.getLat());
        target.setVarExtData(this.getExtData());
        //处理子元素的applyData
        if (this.childrenList != null)
            for (VarBeanBridge varBean : this.childrenList)
                varBean.applyData();
        return true;
    }
    /*--------------------------------------------------------------------*/
    public void updateState(ConstVarBean target) {
        //A.
        if (this.getTarget() != target)
            this.setTarget(target);
        this.getTarget().setConst(target.getConst());
        //更新孩子的父亲
        if (this.childrenList != null)
            for (VarBeanBridge varBean : this.childrenList) {
                ConstVarBean _target = varBean.getTarget();
                _target.setParent(target);
                _target.setConst(target.getConst());
                varBean.updateState(_target);
            }
    }
    /*--------------------------------------------------------------------*/
    /**VarKey*/
    public String getKey() {
        return (String) this.getProperty("VarKey");
    }
    public boolean setKey(String key) {
        return this.setProperty("VarKey", key);
    }
    public boolean isKeyChanged() {
        return this.isPropertyChanged("VarKey");
    }
    /**VarValue*/
    public String getVar() {
        return (String) this.getProperty("VarVar");
    }
    public boolean setVar(String var) {
        return this.setProperty("VarVar", var);
    }
    public boolean isVarChanged() {
        return this.isPropertyChanged("VarVar");
    }
    /**VarLat*/
    public String getLat() {
        return (String) this.getProperty("VarLat");
    }
    public boolean setLat(String lat) {
        return this.setProperty("VarLat", lat);
    }
    public boolean isLatChanged() {
        return this.isPropertyChanged("VarLat");
    }
    /**VarExtData*/
    public String getExtData() {
        return (String) this.getProperty("VarExtData");
    }
    public boolean setExtData(String extData) {
        return this.setProperty("VarExtData", extData);
    }
    public boolean isExtDataChanged() {
        return this.isPropertyChanged("VarExtData");
    }
}