package org.dev.toos.constcode.data.xml;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.dev.toos.constcode.data.ConstDao;
import org.dev.toos.constcode.data.xml.define.ConfigCodes;
import org.dev.toos.constcode.data.xml.define.ConstType;
import org.dev.toos.constcode.data.xml.define.VarType;
import org.dev.toos.constcode.data.xml.metadata.XmlConstBean;
import org.dev.toos.constcode.data.xml.metadata.XmlConstVarBean;
import org.dev.toos.constcode.metadata.ConstBean;
import org.dev.toos.constcode.metadata.LatType;
import org.dev.toos.internal.util.Message;
import org.eclipse.core.resources.IFile;
/**
 * 
 * @version : 2013-2-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlConstDao extends ConstDao {
    private XmlSource xmlSource = null;
    /**冒险直接修改XmlSource数据源中的数据会引发与Dao层不同步的问题。*/
    public XmlSource getSource() {
        return xmlSource;
    }
    public XmlConstDao(IFile constSource) throws IOException, JAXBException {
        this.xmlSource = new XmlSource(constSource);
        this.initDao();
    }
    public XmlConstDao(InputStream constSource) throws IOException, JAXBException {
        this.xmlSource = new XmlSource(constSource);
        this.initDao();
    }
    //
    //
    //装载过程......
    private List<ConstBean> rootConstBean = new ArrayList<ConstBean>();
    protected void initDao() throws IOException, JAXBException {
        ConfigCodes configCodes = this.getSource().getSource();
        if (configCodes.getConst() != null)
            for (ConstType vt : configCodes.getConst()) {
                XmlConstBean constBean = this.toConst(vt, null);
                if (constBean != null)
                    this.rootConstBean.add(constBean);
            }
    }
    private XmlConstBean toConst(ConstType vt, XmlConstBean parentConstBean) {
        XmlConstBean constBean = new XmlConstBean(vt, parentConstBean);
        constBean.setConstCode(vt.getCode());
        constBean.setConstVar(vt.getName());
        constBean.setConstExtData(vt.getExtendsData());
        constBean.setConstLatType(LatType.No);
        for (VarType var : vt.getVar())
            this.spanNode(constBean, null, var);
        //TODO 可以迭代处理孩子...
        return constBean;
    }
    private void spanNode(XmlConstBean constBean, XmlConstVarBean parentVar, VarType currentVar) {
        XmlConstVarBean cvb = new XmlConstVarBean(currentVar, constBean, parentVar);
        cvb.setVarKey(currentVar.getKey());
        cvb.setVarVar(currentVar.getValue());
        cvb.setVarLat(null);
        cvb.setVarExtData(null);
        for (VarType var : currentVar.getVar())
            this.spanNode(constBean, cvb, var);
    }
    @Override
    public boolean deleteConst(ConstBean constBean) {
        if (constBean instanceof XmlConstBean == false)
            return false;
        ConstType ct = ((XmlConstBean) constBean).getTarget();
        List<ConstType> constList = this.asConstList(ct);
        if (constList == null)
            return false;
        constList.remove(ct);
        return true;
    }
    @Override
    public List<ConstBean> getRootConst() {
        this.rootConstBean.clear();
        try {
            this.initDao();
        } catch (Exception e) {}
        return this.rootConstBean;
    }
    @Override
    public List<ConstBean> getConstChildren(ConstBean parentConst) {
        if (parentConst instanceof XmlConstBean == false)
            return null;
        ConstType ct = ((XmlConstBean) parentConst).getTarget();
        //TODO 获取到孩子返回创建的XmlConstBean对象即可。
        return null;
    }
    @Override
    public XmlConstBean addConst(ConstBean constBean, int newIndex) {
        ConstBean parent = constBean.getParent();
        List<ConstType> constList = null;
        if (parent != null) {
            //children add
            if (parent instanceof XmlConstBean == false)
                throw new RuntimeException("parent object did not create.");//父对象尚未添加。
            ConstType ct = ((XmlConstBean) parent).getTarget();
            constList = this.asConstList(ct);
        } else {
            //root add
            try {
                constList = this.getSource().getSource().getConst();
            } catch (Exception e) {
                Message.errorInfo("get ConfigCodes error.", e);
            }
        }
        if (constList == null)
            throw new RuntimeException("collection is null.");
        //
        ConstType constType = new ConstType();
        constType.setCode(constBean.getConstCode());
        constType.setName(constBean.getConstVar());
        constType.setExtendsData(constBean.getConstExtData());
        if (newIndex >= constList.size())
            constList.add(constType);
        else
            constList.add(newIndex, constType);
        return new XmlConstBean(constType, parent);
    }
    public XmlConstBean updateConst(ConstBean constBean, int upIndex) {
        if (constBean instanceof XmlConstBean == false)
            return null;
        ConstType ct = ((XmlConstBean) constBean).getTarget();
        List<ConstType> constList = this.asConstList(ct);
        if (constList == null)
            return null;
        //
        ct.setCode(constBean.getConstCode());
        ct.setName(constBean.getConstVar());
        ct.setExtendsData(constBean.getConstExtData());
        if (constList.indexOf(ct) != upIndex) {
            //索引位置变更
            constList.remove(ct);
            if (upIndex >= constList.size())
                constList.add(ct);
            else
                constList.add(upIndex, ct);
        }
        return (XmlConstBean) constBean;
    }
    //
    //
    //
    /*确定目标所在的集合*/
    private List<ConstType> asConstList(ConstType vt) {
        List<ConstType> constList = null;
        try {
            constList = this.getSource().getSource().getConst();
        } catch (Exception e) {
            Message.errorInfo("get ConfigCodes error.", e);
        }
        //
        for (ConstType constType : constList)
            if (constType == vt)
                return constList;
            else {
                //TODO 递归查找可以扩展实现
            }
        return null;
    };
}