package org.noe.biz.common.ftl;
import javax.inject.Inject;
import org.noe.biz.common.startup.PlatformFilter;
import org.noe.platform.context.AppContext;
import org.noe.platform.modules.freemarker.FmMethod;
import org.noe.platform.modules.freemarker.FreemarkerService;
/**
 * Freemarker 模板中通用函数
 * @version : 2013-9-24
 * @author 赵永春(zyc@hasor.net)
 */
public class Functions {
    @Inject
    private AppContext        appContext = null;
    @Inject
    private FreemarkerService fmService  = null;
    /*-----------------------------------------------------------------*/
    //
    //
    /**获取容器路径*/
    @FmMethod("ctxPath")
    public String ctxPath() {
        return PlatformFilter.getLocalServletContext().getContextPath();
    };
    //
    //
    //    /**解析模板获取布尔配置*/
    //    @FmMethod("loadFtl")
    //    public String loadFtl(String templateName) {
    //        StringWriter sw = new StringWriter();
    //        return this.fmService.processTemplate(templateName, rootMap, writer);
    //    };
    //
    //
    /**获取字符串配置*/
    @FmMethod("str_settings")
    public String str_settings(String settingName, String defaultValue) {
        return this.appContext.getSettings().getString(settingName, defaultValue);
    };
    /**获取布尔配置*/
    @FmMethod("bool_settings")
    public Boolean bool_settings(String settingName, Boolean defaultValue) {
        return this.appContext.getSettings().getBoolean(settingName, defaultValue);
    };
    //
    //
}