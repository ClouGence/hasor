package org.test.more.webui;
import java.util.Map;
import org.more.webui.context.ViewContext;
import org.more.webui.context.guice.Bean;
@Bean("WebUIDemoService")
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
}