package org.more.webui.web;
import java.util.Map.Entry;
import java.util.Set;
import org.more.util.ClassUtil;
import org.more.webui.context.BeanManager;
import org.more.webui.context.FacesConfig;
import org.more.webui.context.FacesContext;
import org.more.webui.lifestyle.Lifecycle;
import org.more.webui.lifestyle.PhaseListener;
import org.more.webui.lifestyle.UIPhaseListener;
import org.more.webui.render.NoRender;
import org.more.webui.render.Render;
import org.more.webui.render.RenderKit;
import org.more.webui.render.UIRender;
import org.more.webui.support.UICom;
import org.more.webui.support.UIComponent;
import freemarker.template.TemplateModelException;
/**
 * 
 * @version : 2012-6-27
 * @author 赵永春 (zyc@byshell.org)
 */
class FactoryBuild {
    private ClassUtil    classUtil    = null; //扫描的类包路径
    private WebUIFactory webUIFactory = null;
    public FactoryBuild(WebUIFactory webUIFactory) {
        this.webUIFactory = webUIFactory;
    }
    private void setup(FacesConfig config) {
        if (this.classUtil != null)
            return;
        String scanPackageStr = config.getScanPackages();
        String[] scanPackages = scanPackageStr.split(",");
        this.classUtil = ClassUtil.newInstance(scanPackages);
    }
    /**生成{@link FacesContext} */
    public FacesContext buildFacesContext(FacesConfig config) throws TemplateModelException {
        this.setup(config);
        FacesContext fc = this.webUIFactory.createFacesContext(config);
        RenderKit kit = new RenderKit();
        kit.initKit(fc);
        fc.addRenderKit("default", kit);
        Set<Class<?>> classSet = null;
        //A.扫描注解添加组建。
        classSet = classUtil.getClassSet(UICom.class);
        for (Class<?> type : classSet) {
            UICom uicom = type.getAnnotation(UICom.class);
            if (UIComponent.class.isAssignableFrom(type) == false)
                throw new ClassCastException(type + " to UIComponent");
            else {
                /*添加组建*/
                fc.addComponentType(uicom.tagName(), type);
                /*为组建添加默认标签渲染器，因为只有用于渲染器的组建才会生效。*/
                fc.getRenderKit("default").addRenderType(uicom.tagName(), NoRender.class);
            }
        }
        //B.扫描注解添加标签渲染器。
        classSet = classUtil.getClassSet(UIRender.class);
        for (Class<?> type : classSet) {
            UIRender uiRender = type.getAnnotation(UIRender.class);
            if (Render.class.isAssignableFrom(type) == false)
                throw new ClassCastException(type + " to Render");
            else
                fc.getRenderKit("default").addRenderType(uiRender.tagName(), type);
        }
        //C.向全局Freemarker服务中注册标签
        kit = fc.getRenderKit("default");
        for (Entry<String, Object> tagEntry : kit.getTags().entrySet())
            fc.getFreemarker().setSharedVariable(tagEntry.getKey(), tagEntry.getValue());
        return fc;
    }
    /**生成{@link Lifecycle}*/
    public Lifecycle buildLifestyle(FacesConfig config, FacesContext context) {
        this.setup(config);
        /*创建生命周期对象*/
        Lifecycle lifestyle = this.webUIFactory.createLifestyle(config, context);
        /*添加生命周期监听器*/
        BeanManager beanManager = context.getBeanContext();
        Set<Class<?>> classSet = classUtil.getClassSet(UIPhaseListener.class);
        for (Class<?> type : classSet)
            if (PhaseListener.class.isAssignableFrom(type) == false)
                throw new ClassCastException(type + " to PhaseListener");
            else {
                PhaseListener listener = beanManager.getBean(type);
                lifestyle.addPhaseListener(listener);
            }
        return lifestyle;
    }
}