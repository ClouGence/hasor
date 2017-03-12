&emsp;&emsp;Hasor没有内置任何渲染引擎，因此使用模板引擎渲染页面需要您自行整合。下面以 Freemarker 为例进行整合：
```java
@Render({ "html", "htm" })
public class FreemarkerRender implements RenderEngine {
    protected Configuration configuration;
    @Override
    public void initEngine(AppContext appContext) throws Throwable {
        String realPath = appContext.getInstance(ServletContext.class).getRealPath("/");
        TemplateLoader templateLoader = new FileTemplateLoader(new File(realPath), true);
        this.configuration = new Configuration(Configuration.VERSION_2_3_22);
        this.configuration.setTemplateLoader(templateLoader);
        this.configuration.setDefaultEncoding("utf-8");//默认页面编码UTF-8
        this.configuration.setOutputEncoding("utf-8");//输出编码格式UTF-8
        this.configuration.setLocalizedLookup(false);//是否开启国际化false
        this.configuration.setClassicCompatible(true);//null值测处理配置
        //
        // - 各种工具
        this.configuration.setSharedVariable("stringUtils", new StringUtils());
    }
    @Override
    public void process(RenderInvoker renderData, Writer writer) throws Throwable {
        Template temp = this.configuration.getTemplate(renderData.renderTo());
        HashMap<String, Object> data = new HashMap<String, Object>();
        for (String key : renderData.keySet()) {
            data.put(key, renderData.get(key));
        }
        temp.process(data, writer);
    }
    @Override
    public boolean exist(String template) throws IOException {
        return this.configuration.getTemplateLoader().findTemplateSource(template) != null;
    }
}
```

&emsp;&emsp;@Render注解的作用是，告诉 Hasor 渲染器处理的资源类型，一个渲染器可以同时兼顾多个资源类型。上面例子中 FreemarkerRender 渲染器可以处理所有 html、htm 结尾的请求。

&emsp;&emsp;同样启用渲染器也需要开发者手动开启，如下：
```
apiBinder.scanAnnoRender();
```