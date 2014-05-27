package org.dev.toos.constcode.data.xml;
import java.util.ArrayList;
import java.util.List;
import org.dev.toos.constcode.data.VarDao;
import org.dev.toos.constcode.data.xml.define.ConstType;
import org.dev.toos.constcode.data.xml.define.VarType;
import org.dev.toos.constcode.data.xml.metadata.XmlConstBean;
import org.dev.toos.constcode.data.xml.metadata.XmlConstVarBean;
import org.dev.toos.constcode.metadata.ConstBean;
import org.dev.toos.constcode.metadata.ConstVarBean;
import org.dev.toos.internal.util.Message;
/**
 * 
 * @version : 2013-2-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlVarDao extends VarDao {
    private XmlConstDao xmlConstDao = null;
    public XmlVarDao(XmlConstDao xmlConstDao) {
        this.xmlConstDao = xmlConstDao;
    }
    @Override
    protected void initDao() throws Throwable {
        // TODO Auto-generated method stub
    }
    @Override
    protected XmlSource getSource() {
        return this.xmlConstDao.getSource();
    }
    @Override
    public boolean deleteVar(ConstVarBean targetVar) {
        if (targetVar instanceof XmlConstVarBean == false)
            return false;
        VarType vt = ((XmlConstVarBean) targetVar).getTarget();
        List<VarType> varList = this.asVarList(vt);
        if (varList == null)
            return false;
        //
        varList.remove(vt);
        return true;
    }
    @Override
    public List<ConstVarBean> getVarRoots(ConstBean parentConst) {
        if (parentConst instanceof XmlConstBean == false)
            return null;
        ConstType targetConst = ((XmlConstBean) parentConst).getTarget();
        //
        List<VarType> atList = targetConst.getVar();
        List<ConstVarBean> varBeanList = new ArrayList<ConstVarBean>();
        if (atList != null)
            for (VarType varType : atList) {
                XmlConstVarBean varBean = new XmlConstVarBean(varType, parentConst);
                varBean.setVarKey(varType.getKey());
                varBean.setVarVar(varType.getValue());
                varBean.setVarExtData(varType.getExtendsData());
                varBeanList.add(varBean);
            }
        return varBeanList;
    }
    @Override
    public List<ConstVarBean> getVarChildren(ConstVarBean parentVar) {
        if (parentVar instanceof XmlConstVarBean == false)
            return null;
        VarType targetVar = ((XmlConstVarBean) parentVar).getTarget();
        //
        List<VarType> atList = targetVar.getVar();
        List<ConstVarBean> varBeanList = new ArrayList<ConstVarBean>();
        if (atList != null)
            for (VarType varType : atList) {
                XmlConstVarBean varBean = new XmlConstVarBean(varType, parentVar.getConst(), parentVar);
                varBean.setVarKey(varType.getKey());
                varBean.setVarVar(varType.getValue());
                varBean.setVarExtData(varType.getExtendsData());
                varBeanList.add(varBean);
            }
        return varBeanList;
    }
    @Override
    public XmlConstVarBean addVar(ConstVarBean newVar, int newIndex) {
        ConstVarBean parentVar = newVar.getParent();
        ConstBean parentConst = newVar.getConst();
        //1.所属常量类型判断。
        if (parentConst instanceof XmlConstBean == false)
            throw new RuntimeException("parent const did not create.");//父对象尚未添加。
        //
        List<VarType> varList = null;
        if (parentVar != null) {
            //children add
            if (parentVar instanceof XmlConstVarBean == false)
                throw new RuntimeException("parent object did not create.");//父对象尚未添加。
            VarType vt = ((XmlConstVarBean) parentVar).getTarget();
            varList = vt.getVar();
        } else {
            //root add
            varList = ((XmlConstBean) parentConst).getTarget().getVar();
        }
        if (varList == null)
            throw new RuntimeException("collection is null.");
        //
        VarType varType = new VarType();
        varType.setKey(newVar.getVarKey());
        varType.setValue(newVar.getVarVar());
        varType.setExtendsData(newVar.getVarExtData());
        if (newIndex >= varList.size())
            varList.add(varType);
        else
            varList.add(newIndex, varType);
        return new XmlConstVarBean(varType, parentConst, parentVar);
    }
    @Override
    public XmlConstVarBean updateVar(ConstVarBean targetVar, int newVarIndex) {
        if (targetVar instanceof XmlConstVarBean == false)
            return null;
        VarType vt = ((XmlConstVarBean) targetVar).getTarget();
        List<VarType> varList = this.asVarList(vt);
        if (varList == null)
            return null;
        //
        vt.setKey(targetVar.getVarKey());
        vt.setValue(targetVar.getVarVar());
        vt.setExtendsData(targetVar.getVarExtData());
        if (varList.indexOf(vt) != newVarIndex) {
            //索引位置变更
            varList.remove(vt);
            if (newVarIndex >= varList.size())
                varList.add(vt);
            else
                varList.add(newVarIndex, vt);
        }
        return (XmlConstVarBean) targetVar;
    }
    //
    //
    //
    /*确定目标所在的集合*/
    private List<VarType> asVarList(VarType vt) {
        List<ConstType> constList = null;
        try {
            constList = this.getSource().getSource().getConst();
        } catch (Exception e) {
            Message.errorInfo("get ConfigCodes error.", e);
        }
        //
        for (ConstType constType : constList) {
            List<VarType> rootList = constType.getVar();
            for (VarType varType : rootList) {
                if (varType == vt)
                    return rootList;
                List<VarType> resList = findVarList(varType, vt);
                if (resList != null)
                    return resList;
            }
        }
        return null;
    };
    private List<VarType> findVarList(VarType vt, VarType target) {
        List<VarType> childrenList = vt.getVar();
        for (VarType varType : childrenList) {
            if (varType == target)
                return childrenList;
            List<VarType> resList = findVarList(varType, target);
            if (resList != null)
                return resList;
        }
        return null;
    }
}