package org.noe;
import java.util.List;
import java.util.Map;
import org.more.webui.context.ViewContext;
@Service("WebUIDemoService")
public class WebUIDemoService {
    private String desc;
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String print(String msg) {
        ViewContext vc = ViewContext.getCurrentViewContext();
        return "SSKKK";
    }
    public String print(Object arg1, Object arg2, Object arg3, Object arg4) {
        ViewContext vc = ViewContext.getCurrentViewContext();
        return "ok";
    }
    public String submitMap(Map mapData) {
        System.out.println(mapData.get("i_name"));
        return "heeel";
    }
    public List<?> getConstData() {
        ConstService constService = AppUtil.getObj(ConstService.class);
        //
        TreeConstBean constBean = constService.loadConstByCode("BIZ_TPS_Setting", null);
        if (constBean == null)
            return null;
        return constBean.getVarList();
    }
}