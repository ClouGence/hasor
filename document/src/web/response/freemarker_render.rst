扩展：FreeMarker渲染引擎
------------------------------------

.. code-block:: java
    :linenos:

    /**
     * Freemarker 渲染器
     * @version : 2016年1月3日
     * @author 赵永春 (zyc@hasor.net)
     */
    @Render("flt")
    public class FreemarkerRender implements RenderEngine {
        protected Configuration freemarker;
        //
        /** 内置创建 Freemarker 对象的方法，您也可以通过 apiBinder.bind(Configuration.class).... 来设置您自定义的。 */
        protected Configuration newConfiguration(AppContext appContext, ServletContext servletContext) throws IOException {
            //
            String realPath = servletContext.getRealPath("/");
            TemplateLoader templateLoader = new FileTemplateLoader(new File(realPath), true);
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
            configuration.setTemplateLoader(templateLoader);
            //
            String responseEncoding = appContext.findBindingBean(RuntimeFilter.HTTP_RESPONSE_ENCODING_KEY, String.class);
            if (StringUtils.isBlank(responseEncoding)) {
                responseEncoding = Settings.DefaultCharset;
            }
            configuration.setDefaultEncoding(responseEncoding);
            configuration.setOutputEncoding(responseEncoding);
            configuration.setLocalizedLookup(false);//是否开启国际化false
            configuration.setClassicCompatible(true);//null值测处理配置
            //
            return configuration;
        }
        /** 各种工具&变量 */
        protected void configSharedVariable(AppContext appContext, ServletContext servletContext, Configuration freemarker) throws TemplateModelException {
            freemarker.setSharedVariable("stringUtils", new StringUtils());
            freemarker.setSharedVariable("ctx_path", servletContext.getContextPath());
        }
        public void initEngine(AppContext appContext) throws Throwable {
            ServletContext servletContext = Hasor.assertIsNotNull(appContext.getInstance(ServletContext.class));
            BindInfo<Configuration> bindInfo = appContext.getBindInfo(Configuration.class);
            if (bindInfo == null) {
                this.freemarker = this.newConfiguration(appContext, servletContext);
            } else {
                this.freemarker = appContext.getInstance(bindInfo);
            }
            this.configSharedVariable(appContext, servletContext, Hasor.assertIsNotNull(this.freemarker));
        }
        public boolean exist(String template) throws IOException {
            return this.freemarker.getTemplateLoader().findTemplateSource(template) != null;
        }
        public void process(RenderInvoker renderData, Writer writer) throws Throwable {
            Template temp = this.freemarker.getTemplate(renderData.renderTo());
            if (temp == null) {
                return;
            }
            HashMap<String, Object> data = new HashMap<>();
            for (String key : renderData.keySet()) {
                data.put(key, renderData.get(key));
            }
            temp.process(data, writer);
        }
    }
