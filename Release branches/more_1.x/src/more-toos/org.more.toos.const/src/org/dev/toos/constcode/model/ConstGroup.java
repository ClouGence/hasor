/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dev.toos.constcode.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dev.toos.constcode.data.ConstDao;
import org.dev.toos.constcode.data.VarDao;
import org.dev.toos.constcode.metadata.ConstBean;
import org.dev.toos.constcode.metadata.ConstVarBean;
import org.dev.toos.constcode.metadata.create.NEW;
import org.dev.toos.constcode.model.bridge.ConstBeanBridge;
import org.dev.toos.constcode.model.bridge.VarBeanBridge;
/**
 * 
 * @version : 2013-2-2
 * @author 赵永春 (zyc@byshell.org) 
 */
public abstract class ConstGroup {
    public static enum FromType {
        DB, Source, JAR,
    }
    private String                name          = null;
    private boolean               isReadOnly    = false;
    private FromType              fromType      = null;
    private boolean               isChanged     = false;
    //
    /*孩子们*/
    private List<ConstBeanBridge> constBeanList = new ArrayList<ConstBeanBridge>();
    //
    //
    protected ConstGroup(FromType fromType) {
        if (fromType == null)
            throw new NullPointerException();
        this.fromType = fromType;
    }
    /**分组是否为只读属性*/
    public boolean isReadOnly() {
        return this.isReadOnly;
    }
    protected void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }
    /**获取来源类型*/
    public FromType getType() {
        return this.fromType;
    }
    /**获取名称*/
    public String getName() {
        return name;
    }
    /**设置名称*/
    public void setName(String name) {
        this.name = name;
    }
    /**确定改组的数据是否已经被修改过。*/
    public boolean isConstChanged() {
        return isChanged;
    }
    /**由外部给定一个值，该值用来说明常量分组的数据已经被修改过。*/
    public void setConstChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }
    /**返回只读形式的常量集合*/
    public List<ConstBeanBridge> constList() {
        return Collections.unmodifiableList(constBeanList);
    }
    /**载入常量的孩子*/
    public List<ConstBeanBridge> loadChildrenConst(ConstBeanBridge constBean) {
        ArrayList<ConstBeanBridge> cbgList = new ArrayList<ConstBeanBridge>();
        ConstBean targetConstBean = constBean.getTarget();
        if (targetConstBean instanceof NEW == false) {
            List<ConstBean> children = this.getConstDao().getConstChildren(targetConstBean);
            if (children != null)
                for (ConstBean cb : children)
                    cbgList.add(new ConstBeanBridge(constBean, cb, this));
        }
        return cbgList;
    }
    /**载入常量值的孩子*/
    public List<VarBeanBridge> loadChildrenVar(VarBeanBridge varBean) {
        ArrayList<VarBeanBridge> cbgList = new ArrayList<VarBeanBridge>();
        ConstVarBean targetVarBean = varBean.getTarget();
        if (targetVarBean instanceof NEW == false/* true is new var*/) {
            List<ConstVarBean> varItems = this.getVarDao().getVarChildren(targetVarBean);
            if (varItems != null)
                for (ConstVarBean cb : varItems)
                    cbgList.add(new VarBeanBridge(varBean.getConst(), varBean, cb, this));
        }
        return cbgList;
    }
    /**载入常量定义的常量值*/
    public List<VarBeanBridge> loadVarRoots(ConstBeanBridge constBean) {
        ArrayList<VarBeanBridge> cbgList = new ArrayList<VarBeanBridge>();
        ConstBean targetConstBean = constBean.getTarget();
        if (targetConstBean instanceof NEW == false) {
            List<ConstVarBean> varItems = this.getVarDao().getVarRoots(targetConstBean);
            if (varItems != null)
                for (ConstVarBean cb : varItems)
                    cbgList.add(new VarBeanBridge(constBean, null, cb, this));
        }
        return cbgList;
    }
    //
    //
    /**返回是否装载成功*/
    public boolean loadData() {
        for (ConstBean constBean : this.getConstDao().getRootConst()) {
            ConstBeanBridge constBridge = new ConstBeanBridge(null, constBean, this);
            this.constBeanList.add(constBridge);
        }
        return true;
    };
    /**重新装载XML.满足下面一条的就放弃重载。（jar、尚未保存）*/
    public boolean reloadData() {
        if (this.getType() == FromType.JAR)//Jar，放弃
            return false;
        if (this.isConstChanged() == true)//尚未保存，放弃
            return false;
        //
        this.constBeanList.clear();
        return this.loadData();
    }
    /**得到通知准备开始更新。*/
    public void beginSave() throws Throwable {};
    /**得到通知更新过程结束。*/
    public void finishSave() throws Throwable {};
    /**持久化处理*/
    public void save() throws Throwable {
        /*在只读模式下放弃操作*/
        if (this.isReadOnly() == true)
            return;
        /*将Bridge上的数据更新到数据模型上,然后进行持久化处理。*/
        for (int i = 0; i < this.constBeanList.size(); i++) {
            ConstBeanBridge beanBridge = this.constBeanList.get(i);
            if (beanBridge.isPropertyChanged() == false)
                continue;
            this.doUpdateConst(i, beanBridge);//TODO 忽略可能的错误
        }
        this.setConstChanged(false);
    }
    /**添加常量*/
    public boolean addConst(ConstBeanBridge constBean) {
        this.setConstChanged(true);
        this.constBeanList.add(constBean);
        return true;
    }
    public boolean addConst(int index, ConstBeanBridge newConst) {
        this.setConstChanged(true);
        this.constBeanList.add(index, newConst);
        return true;
    }
    /**删除常量*/
    public boolean deleteConst(ConstBeanBridge constBean) {
        this.setConstChanged(true);
        if (constBean.isNew() == true)
            this.constBeanList.remove(constBean);
        else
            constBean.delete();
        return true;
    }
    //
    /**持久化处理，常量*/
    private boolean doUpdateConst(int upDataIndex, ConstBeanBridge target) throws Throwable {
        boolean tempRes = true;
        //0.应用新数据
        if (target.applyData() == false)
            return false;
        //1.常量处理
        if (target.isDelete() == true) {
            return this.getConstDao().deleteConst(target.getTarget());
        }
        ConstBean newRes = null;
        if (target.isNew() == true)
            newRes = this.getConstDao().addConst(target.getTarget(), upDataIndex);
        else
            newRes = this.getConstDao().updateConst(target.getTarget(), upDataIndex);
        if (newRes == null)
            return false;
        target.updateState(newRes);
        //2.常量值处理
        List<VarBeanBridge> varList = target.getVarRoots();
        if (varList != null)
            for (int i = 0; i < varList.size(); i++) {
                VarBeanBridge varBean = varList.get(i);
                if (varBean.isPropertyChanged() == false)
                    continue;
                tempRes = this.doUpdateVar(i, varList.get(i));
                if (tempRes == false)
                    return false;
            }
        //3.孩子处理
        List<ConstBeanBridge> constChildren = target.getChildren();
        for (int i = 0; i < constChildren.size(); i++) {
            tempRes = this.doUpdateConst(i, constChildren.get(i));
            if (tempRes == false)
                return false;
        }
        return true;
    };
    /**持久化处理，常量值*/
    private boolean doUpdateVar(int upDataIndex, VarBeanBridge target) throws Throwable {
        boolean tempRes = true;
        //0.应用新数据
        if (target.applyData() == false)
            return false;
        //1.常量处理
        if (target.isDelete() == true)
            return this.getVarDao().deleteVar(target.getTarget());
        ConstVarBean newRes = null;
        if (target.isNew() == true)
            newRes = this.getVarDao().addVar(target.getTarget(), upDataIndex);
        else
            newRes = this.getVarDao().updateVar(target.getTarget(), upDataIndex);
        if (newRes == null)
            return false;
        target.updateState(newRes);
        //2.孩子处理
        List<VarBeanBridge> varList = target.getChildren();
        if (varList != null)
            for (int i = 0; i < varList.size(); i++) {
                VarBeanBridge varBean = varList.get(i);
                if (varBean.isPropertyChanged() == false)
                    continue;
                tempRes = this.doUpdateVar(i, varList.get(i));
                if (tempRes == false)
                    return false;
            }
        return true;
    };
    //
    protected abstract void initGroup();
    protected abstract ConstDao getConstDao();
    protected abstract VarDao getVarDao();
}