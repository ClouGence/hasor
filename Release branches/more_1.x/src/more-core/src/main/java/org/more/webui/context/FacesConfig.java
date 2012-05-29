package org.more.webui.context;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import org.more.webui.UIInitException;
import org.more.webui.components.UIComponent;
import org.more.webui.components.UIViewRoot;
import org.more.webui.freemarker.loader.ITemplateLoader;
import org.more.webui.freemarker.loader.template.ClassPathTemplateLoader;
import org.more.webui.freemarker.loader.template.DirTemplateLoader;
import org.more.webui.freemarker.loader.template.MultiTemplateLoader;
import org.more.webui.freemarker.parser.Hook_Include;
import org.more.webui.freemarker.parser.Hook_UserTag;
import org.more.webui.freemarker.parser.TemplateScanner;
import org.more.webui.render.Render;
import org.more.webui.render.RenderKit;
import freemarker.template.Template;
/**
 * 
 * @version : 2012-5-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class FacesConfig {
    private String                     encoding           = "utf-8";                         //字符编码
    private boolean                    localizedLookup    = false;                           //是否启用国际化的额支持
    private String                     facesSuffix        = ".xhtml";
    private FilterConfig               initConfig         = null;
    private ArrayList<ITemplateLoader> templateLoaderList = new ArrayList<ITemplateLoader>();
    /**组建的标签名和对应的组建类型*/
    private Map<String, Class<?>>      componentMap       = new HashMap<String, Class<?>>();
    /**组建的标签名和对应的渲染器*/
    private Map<String, RenderKit>     renderKitMap       = new HashMap<String, RenderKit>();
    /*----------------------------------------------------------------*/
    public FacesConfig(FilterConfig initConfig) {
        this.initConfig = initConfig;
    }
    /*----------------------------------------------------------------*/
    private static long genID = 0;
    /**根据组件类型，生成个组件ID*/
    public static String generateID(Class<? extends UIComponent> compClass) {
        return "com_" + (genID++);
    }
    /**获取一个boolean值该值决定了模板是否支持国际化。*/
    public boolean isLocalizedLookup() {
        return this.localizedLookup;
    }
    public void setLocalizedLookup(boolean localizedLookup) {
        this.localizedLookup = localizedLookup;
    }
    /**获取页面使用的字符编码*/
    public String getEncoding() {
        return this.encoding;
    };
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    /**获取一个扩展名，凡是具备该扩展名的文件都被视为UI文件。*/
    public String getFacesSuffix() {
        return this.facesSuffix;
    }
    public void setFacesSuffix(String facesSuffix) {
        this.facesSuffix = facesSuffix;
    }
    public String getInitConfig(String key) {
        return this.initConfig.getInitParameter(key);
    }
    public ServletContext getServletContext() {
        return this.initConfig.getServletContext();
    }
    /*----------------------------------------------------------------*/
    /**添加{@link ITemplateLoader}对象。*/
    public void addLoader(ITemplateLoader loader) {
        this.templateLoaderList.add(loader);
    }
    /**添加一个包作为Loader的路径。*/
    public void addLoader(String packageName) {
        ClassPathTemplateLoader loader = new ClassPathTemplateLoader(packageName);
        this.addLoader(loader);
    }
    /**添加一个路径作为Loader的路径。*/
    public void addLoader(File templateDir) throws IOException {
        DirTemplateLoader loader = new DirTemplateLoader(templateDir);
        this.addLoader(loader);
    }
    /**将所有添加的Loader返回成一个{@link MultiTemplateLoader}对象。*/
    public MultiTemplateLoader getMultiTemplateLoader() {
        MultiTemplateLoader multiLoader = new MultiTemplateLoader();
        for (ITemplateLoader loader : this.templateLoaderList)
            multiLoader.addTemplateLoader(loader);
        return multiLoader;
    }
    /**
     * 添加一条组建的注册。
     * @param tagName 组建的标签名。
     * @param componentClass 组建class类型。
     */
    public void addComponent(String tagName, Class<?> componentClass) {
        this.componentMap.put(tagName, componentClass);
    }
    /**创建一个{@link RenderKit}*/
    public RenderKit getRenderKit(String scope) {
        RenderKit kit = this.renderKitMap.get(scope);
        if (kit == null) {
            kit = new RenderKit();
            this.renderKitMap.put(scope, kit);
        }
        return kit;
    }
    /**添加标签的映射关系。*/
    public void addRender(String scope, String tagName, Class<? extends Render> renderClass) {
        this.getRenderKit(scope).addRender(tagName, renderClass);
    }
    /*----------------------------------------------------------------*/
    /**用于创建一个{@link UIViewRoot}对象 */
    public UIViewRoot createViewRoot(Template template, String templateFile) throws UIInitException, IOException {
        //A.创建扫描器
        TemplateScanner scanner = new TemplateScanner();
        scanner.addElementHook("UnifiedCall", new Hook_UserTag(this));/*UnifiedCall：@add*/
        scanner.addElementHook("Include", new Hook_Include(this));/*Include：@Include*/
        //B.解析模板获取UIViewRoot
        UIViewRoot root = (UIViewRoot) scanner.parser(template, new UIViewRoot());
        return root;
    }
    /**根据组建的标签名，创建组建*/
    public UIComponent createComponent(String tagName) throws UIInitException {
        try {
            Class<?> comClass = this.componentMap.get(tagName);
            if (comClass != null)
                return (UIComponent) comClass.newInstance();
            else
                return null;
        } catch (InstantiationException e) {
            throw new UIInitException("组建错误： ‘" + tagName + "’不能被创建.", e);
        } catch (IllegalAccessException e) {
            throw new UIInitException("组建错误：在创建 ‘" + tagName + "’期间遇到一个错误的访问权限.", e);
        }
    }
}