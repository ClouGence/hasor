package net.example.jfinal.core;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import net.example.jfinal.domain.UserDTO;
import net.example.jfinal.web.Index;
import net.hasor.plugins.jfinal.HasorDataSourceProxy;
import net.hasor.plugins.jfinal.HasorInterceptor;
import net.hasor.plugins.jfinal.HasorPlugin;
/**
 * JFinal API 引导式配置（Hasor系列框架全面深度整合）
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
public class DemoConfig extends JFinalConfig {
    private C3p0Plugin getC3p0Plugin() {
        String jdbcUrl = PropKit.get("jdbc.url").trim();
        String user = PropKit.get("jdbc.user").trim();
        String password = PropKit.get("jdbc.password").trim();
        String driver = PropKit.get("jdbc.driver").trim();
        return new C3p0Plugin(jdbcUrl, user, password, driver);
    }
    /** JFinal 配置常量 */
    public void configConstant(Constants me) {
        //
        // .（可选）加载 JFinal 配置文件，同时作为 Hasor 的环境变量
        PropKit.use("env.config", "utf-8");
    }
    /** JFinal 配置路由 */
    public void configRoute(Routes me) {
        //
        me.add("/", Index.class);
    }
    /** JFinal 配置插件 */
    public void configPlugin(Plugins me) {
        //
        // .JFinal 数据库 DataSource
        C3p0Plugin c3p0 = getC3p0Plugin();
        //
        // .（可选）用 Hasor 全面的事务管理能力接管 JFinal 事务，下面是配置 JFinal 代理数据源
        HasorDataSourceProxy dbProxy = new HasorDataSourceProxy(c3p0);
        me.add(c3p0);
        //
        // .（必选）Hasor 框架的启动和销毁
        me.add(new HasorPlugin(JFinal.me(), dbProxy, new MyModule()));
        //
        // .JFinal 表映射
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dbProxy);
        me.add(arp);
        arp.addMapping("TEST_USER_INFO", "id", UserDTO.class);
    }
    /** JFinal 配置全局拦截器 */
    public void configInterceptor(Interceptors me) {
        //
        // .（可选）为 JFinal 提供 Controller 的依赖注入
        me.add(new HasorInterceptor(JFinal.me()));
    }
    /** JFinal 配置处理器 */
    public void configHandler(Handlers me) {
        //
        // .（可选）将 Hasor 的 Web 功能集成到 JFinal 中
        //me.add(new HasorHandler(JFinal.me()));
    }
}