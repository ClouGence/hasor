package org.dev.toos.constcode.data.db;
//package org.noe.devtoos.constcode.model.data.db;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import javax.xml.bind.JAXBException;
//import org.eclipse.core.resources.IFile;
//import org.noe.devtoos.constcode.model.data.ConstDao;
//import org.noe.devtoos.constcode.model.data.xml.define.ConfigCodes;
//import org.noe.devtoos.constcode.model.data.xml.define.ConstType;
//import org.noe.devtoos.constcode.model.data.xml.define.VarType;
//import org.noe.devtoos.constcode.model.entity.ConstBean;
//import org.noe.devtoos.constcode.model.entity.ConstVarBean;
//import org.noe.devtoos.constcode.util.Message;
///**
// * 
// * @version : 2013-2-17
// * @author 赵永春 (zyc@byshell.org)
// */
//public class DBConstDao extends ConstDao {
//    private DBSource xmlSource = null;
//    /**冒险直接修改XmlSource数据源中的数据会引发与Dao层不同步的问题。*/
//    protected DBSource getXmlSource() {
//        return xmlSource;
//    }
//    public DBConstDao(IFile constSource) throws IOException, JAXBException {
//        this.xmlSource = new DBSource(constSource);
//        this.initDao();
//    }
//    public DBConstDao(InputStream constSource) throws IOException, JAXBException {
//        this.xmlSource = new DBSource(constSource);
//        this.initDao();
//    }
//    //
//    //
//    //装载过程......
//    private List<ConstBean>        constBeanList = new ArrayList<ConstBean>();
//    private Map<String, ConstBean> constBeanMap  = new HashMap<String, ConstBean>();
//    protected void initDao() throws IOException, JAXBException {
//        ConfigCodes configCodes = this.getXmlSource().getSource();
//        if (configCodes.getConst() != null)
//            for (ConstType vt : configCodes.getConst()) {
//                ConstBean constBean = this.toConst(vt);
//                this.constBeanMap.put(constBean.getConstID(), constBean);
//                this.constBeanList.add(constBean);
//            }
//    }
//    private static int flowID = 9000000;
//    private static int index  = 0;
//    private ConstBean toConst(ConstType vt) {
//        ConstBean constBean = new ConstBean();
//        {
//            //1.Const
//            constBean.setConstID(vt.getCode());
//            constBean.setConstName(vt.getName());
//            constBean.setConstCode(vt.getCode());
//            constBean.setConstValueType(2);//树形
//            constBean.setConstGroupType(0);//不分机构
//            constBean.setConstFlowID(flowID++);
//            constBean.setConstPFlowID(0);
//            constBean.setConstFlowPath(constBean.getConstFlowID() + "@");
//            constBean.setConstDepth(0);
//            constBean.setConstCTime(new Date());
//            constBean.setXmlSource(true);
//            constBean.setConstExtendsData(vt.getExtendsData());
//        }
//        {
//            //2.Var
//            for (VarType var : vt.getVar()) {
//                String node_flowPath = flowID + "@";
//                int node_flowID = flowID++;
//                this.spanNode(constBean, var, constBean.getConstDepth(), node_flowID, node_flowPath);
//            }
//        }
//        return constBean;
//    }
//    private void spanNode(ConstBean constBean, VarType currentVar, int pDepth, int pflowID, String pflowPath) {
//        ConstVarBean cvb = new ConstVarBean();
//        cvb.setVarID(UUID.randomUUID().toString().replace("-", ""));
//        cvb.setVarCode(currentVar.getKey());
//        cvb.setVarName(currentVar.getValue());
//        cvb.setVarIndex(index++);
//        cvb.setVarGroup(null);//无机构
//        cvb.setVarFlowID(flowID++);
//        cvb.setVarPFlowID(pflowID);
//        cvb.setVarFlowPath(pflowPath + cvb.getVarFlowID() + "@");
//        cvb.setVarDepth(pDepth + 1);
//        cvb.setVarCTime(new Date());
//        cvb.setXmlSource(true);
//        cvb.setVarExtendsData(currentVar.getExtendsData());
//        //
//        constBean.getVarChildren().add(cvb);
//        for (VarType var : currentVar.getVar())
//            this.spanNode(constBean, var, cvb.getVarDepth() + 1, cvb.getVarFlowID(), cvb.getVarFlowPath());
//    }
//    //
//    //
//    //
//    @Override
//    public ConstBean getConstByCode(String constCode) {
//        return this.constBeanMap.get(constCode);
//    }
//    @Override
//    public boolean deleteConst(String constCode) {
//        ConstBean constBean = this.constBeanMap.get(constCode);
//        if (constBean == null)
//            return false;
//        try {
//            this.getXmlSource().getSource().getConst().remove(constBean);
//            this.constBeanMap.remove(constCode);
//            this.constBeanList.remove(constBean);
//            return true;
//        } catch (Exception e) {
//            Message.errorInfo("cant deleteConst ‘" + constCode + "’.", e);
//            return false;
//        }
//    }
//    @Override
//    public List<ConstBean> getALLConst() {
//        return Collections.unmodifiableList(this.constBeanList);
//    }
//    @Override
//    public List<ConstBean> getRootConst() {
//        ArrayList<ConstBean> rootConstBean = new ArrayList<ConstBean>();
//        for (ConstBean constBean : this.constBeanList)
//            if (constBean.getConstDepth() == 0)
//                rootConstBean.add(constBean);
//        return Collections.unmodifiableList(rootConstBean);
//    }
//    @Override
//    public List<ConstBean> getConstChildren(ConstBean parentConst) {
//        ArrayList<ConstBean> childrenConstBean = new ArrayList<ConstBean>();
//        for (ConstBean constBean : this.constBeanList)
//            if (constBean.getConstPFlowID() == parentConst.getConstFlowID())
//                childrenConstBean.add(constBean);
//        return Collections.unmodifiableList(childrenConstBean);
//    }
//    @Override
//    public ConstBean getConstByPath(String constPath) {
//        for (ConstBean constBean : this.constBeanList)
//            if (constBean.getConstFlowPath().equals(constPath) == true)
//                return constBean;
//        return null;
//    }
//    @Override
//    public boolean addConst(ConstBean parentConst, String constCode, String constName) {
//        if (this.constBeanMap.containsKey(constCode) == true)
//            return false;
//        try {
//            ConstType constType = new ConstType();
//            constType.setCode(constCode);
//            constType.setName(constName);
//            ConstBean constBean = this.toConst(constType);
//            this.getXmlSource().getSource().getConst().add(constType);
//            this.constBeanMap.put(constBean.getConstID(), constBean);
//            this.constBeanList.add(constBean);
//            return true;
//        } catch (Exception e) {
//            Message.errorInfo("cant addConst ‘" + constCode + "’.", e);
//            return false;
//        }
//    }
//}